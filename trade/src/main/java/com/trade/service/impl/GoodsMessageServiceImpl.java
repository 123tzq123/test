package com.trade.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trade.domain.GoodsMessage;
import com.trade.domain.SysUser;
import com.trade.dto.MessageDTO;
import com.trade.mapper.GoodsMessageMapper;
import com.trade.service.GoodsMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trade.websocket.GoodsMessageWebSocket;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.trade.vo.ChatSessionVO;
import com.trade.mapper.SysUserMapper;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class GoodsMessageServiceImpl extends ServiceImpl<GoodsMessageMapper, GoodsMessage> implements GoodsMessageService {

    @Resource
    private GoodsMessageMapper goodsMessageMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Override
    public GoodsMessage saveMessage(MessageDTO dto, Long fromUserId){
        GoodsMessage goodsMessage = new GoodsMessage();
        goodsMessage.setGoodsId(dto.getGoodsId());
        goodsMessage.setFromUserId(fromUserId);
        goodsMessage.setToUserId(dto.getToUserId());
        goodsMessage.setContent(dto.getContent());
        // 新消息默认未读
        goodsMessage.setReadStatus(0);
        goodsMessage.setCreateTime(LocalDateTime.now());
        this.save(goodsMessage);
        // 查询发送用户头像
        SysUser sendUser = sysUserMapper.selectById(fromUserId);
        String avatar = sendUser.getAvatar() == null ? "" : sendUser.getAvatar();
        // 后端统一推送消息，只发给接收人对方
        Map<String, Object> pushData = new HashMap<>();
        pushData.put("type", "chat_msg");
        pushData.put("id", goodsMessage.getId());
        pushData.put("goodsId", goodsMessage.getGoodsId());
        pushData.put("fromUserId", goodsMessage.getFromUserId());
        pushData.put("toUserId", goodsMessage.getToUserId());
        pushData.put("content", goodsMessage.getContent());
        pushData.put("createTime", goodsMessage.getCreateTime().toString());
        pushData.put("readStatus", goodsMessage.getReadStatus());
        // 头像字段推送给前端
        pushData.put("fromUserAvatar", avatar);
        // 只推送接收消息的对方，不发给发送人自己
        GoodsMessageWebSocket.sendToUser(goodsMessage.getToUserId(), JSON.toJSONString(pushData));
        return goodsMessage;
    }
    @Override
    public Page<GoodsMessage> getHistoryMsg(Long goodsId, Long selfId, Long otherId, Integer pageNum, Integer pageSize) {
        // 1. 更新我方收到消息为已读
        LambdaUpdateWrapper<GoodsMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(GoodsMessage::getReadStatus, 1);
        updateWrapper.eq(GoodsMessage::getToUserId, selfId)
                .eq(GoodsMessage::getFromUserId, otherId)
                .eq(GoodsMessage::getReadStatus, 0);
        this.baseMapper.update(null, updateWrapper);

        List<GoodsMessage> updatedMsgList = baseMapper.selectList(updateWrapper);
        if (!updatedMsgList.isEmpty()) {
            Map<Long, List<Long>> userMsgGroup = updatedMsgList.stream()
                    .collect(Collectors.groupingBy(
                            GoodsMessage::getFromUserId,
                            Collectors.mapping(GoodsMessage::getId, Collectors.toList())
                    ));
            for (Map.Entry<Long, List<Long>> entry : userMsgGroup.entrySet()) {
                Long sendUserId = entry.getKey();
                List<Long> msgIdList = entry.getValue();
                Map<String, Object> pushData = new HashMap<>();
                pushData.put("type", "read_update");
                pushData.put("msgIdList", msgIdList);
                pushData.put("readStatus", 1);
                GoodsMessageWebSocket.sendToUser(sendUserId, JSON.toJSONString(pushData));
            }
        }

        // 2. 查询聊天记录：只匹配双方用户，时间倒序，最新消息第一页
        Page<GoodsMessage> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<GoodsMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(GoodsMessage::getFromUserId, selfId).eq(GoodsMessage::getToUserId, otherId))
                .or(w -> w.eq(GoodsMessage::getFromUserId, otherId).eq(GoodsMessage::getToUserId, selfId));
        // 改为倒序，最新消息排在最前面
        wrapper.orderByDesc(GoodsMessage::getCreateTime);
        List<GoodsMessage> records = goodsMessageMapper.selectMsgWithAvatar(wrapper, page);

        // 反转列表，恢复前端视觉顺序：旧消息在上，新消息在底部
        java.util.Collections.reverse(records);
        page.setRecords(records);
        return page;
    }
    /**
     * 获取当前登录用户全部聊天会话（同一用户合并，不分商品）
     */
    @Override
    public List<ChatSessionVO> getChatSessionList(Long loginUserId) {
        // 1. 查询所有和当前用户相关的消息（我发/我收）
        LambdaQueryWrapper<GoodsMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(GoodsMessage::getFromUserId, loginUserId).or().eq(GoodsMessage::getToUserId, loginUserId));
        wrapper.orderByDesc(GoodsMessage::getCreateTime);
        List<GoodsMessage> allMsgList = baseMapper.selectList(wrapper);

        // 2. 分组：key = 对方用户id，同一用户所有商品消息合并
        Map<Long, List<GoodsMessage>> sessionGroup = allMsgList.stream()
                .collect(Collectors.groupingBy(msg -> {
                    Long otherId = msg.getFromUserId().equals(loginUserId) ? msg.getToUserId() : msg.getFromUserId();
                    return otherId;
                }));

        List<ChatSessionVO> sessionVOList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Map.Entry<Long, List<GoodsMessage>> entry : sessionGroup.entrySet()) {
            List<GoodsMessage> sessionMsg = entry.getValue();
            // 取最新一条消息
            GoodsMessage lastMsg = sessionMsg.get(0);
            // key直接是对方用户ID，goodsId统一0
            Long otherId = entry.getKey();
            Long goodsId = 0L;

            // 查询对方用户信息（头像、昵称）
            SysUser user = sysUserMapper.selectById(otherId);

            // 统计该会话未读数量：toUserId=当前登录人 && readStatus=0
            long unread = sessionMsg.stream()
                    .filter(m -> m.getToUserId().equals(loginUserId) && m.getReadStatus() == 0)
                    .count();

            ChatSessionVO vo = new ChatSessionVO();
            vo.setOtherId(otherId);
            vo.setOtherAvatar(user.getAvatar());
            vo.setOtherName(user.getUsername());
            vo.setLastMsg(lastMsg.getContent());
            vo.setLastMsgTime(lastMsg.getCreateTime().format(formatter));
            vo.setUnreadNum(unread);
            vo.setGoodsId(goodsId);
            sessionVOList.add(vo);
        }

        // 排序规则：1.有未读消息置顶 2.全部按最新消息时间倒序
        sessionVOList.sort((a, b) -> {
            if (a.getUnreadNum() > 0 && b.getUnreadNum() == 0) return -1;
            if (a.getUnreadNum() == 0 && b.getUnreadNum() > 0) return 1;
            return b.getLastMsgTime().compareTo(a.getLastMsgTime());
        });
        return sessionVOList;
    }

    /**
     * 获取当前用户全局总未读消息数量
     */
    @Override
    public Long getTotalUnreadCount(Long loginUserId) {
        LambdaQueryWrapper<GoodsMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsMessage::getToUserId, loginUserId)
                .eq(GoodsMessage::getReadStatus, 0);
        return baseMapper.selectCount(wrapper);
    }

}