package com.trade.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trade.domain.GoodsMessage;
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

@Service
public class GoodsMessageServiceImpl extends ServiceImpl<GoodsMessageMapper, GoodsMessage> implements GoodsMessageService {

    @Resource
    private GoodsMessageMapper goodsMessageMapper;
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
        return goodsMessage;
    }
    @Override
    public Page<GoodsMessage> getHistoryMsg(Long goodsId, Long selfId, Long otherId, Integer pageNum, Integer pageSize) {
        // 1. 批量更新：对方发给我的消息全部改为已读
        LambdaUpdateWrapper<GoodsMessage> updateWrapper = new LambdaUpdateWrapper<>();
        // 设置要更新的字段：已读状态1
        updateWrapper.set(GoodsMessage::getReadStatus, 1);
        // 更新where条件
        updateWrapper.eq(GoodsMessage::getToUserId, selfId)
                .eq(GoodsMessage::getFromUserId, otherId)
                .eq(GoodsMessage::getReadStatus, 0);
        // 商品筛选条件
        if(goodsId != null && goodsId > 0){
            updateWrapper.eq(GoodsMessage::getGoodsId, goodsId);
        }
        // 执行更新，第一个参数传实体/直接用wrapper自带set
        this.baseMapper.update(null, updateWrapper);

        // 1. 查询本次被更新为已读的所有消息
        List<GoodsMessage> updatedMsgList = baseMapper.selectList(updateWrapper);
        if (!updatedMsgList.isEmpty()) {
            // 按发送人分组，key=fromUserId，value=消息id集合
            Map<Long, List<Long>> userMsgGroup = updatedMsgList.stream()
                    .collect(Collectors.groupingBy(
                            GoodsMessage::getFromUserId,
                            Collectors.mapping(GoodsMessage::getId, Collectors.toList())
                    ));
            // 遍历分组，给每个发送用户推送已读通知
            for (Map.Entry<Long, List<Long>> entry : userMsgGroup.entrySet()) {
                Long sendUserId = entry.getKey();
                List<Long> msgIdList = entry.getValue();
                // 组装推送数据
                Map<String, Object> pushData = new HashMap<>();
                pushData.put("type", "read_update");
                pushData.put("msgIdList", msgIdList);
                pushData.put("readStatus", 1);
                // 调用websocket推送
                GoodsMessageWebSocket.sendToUser(sendUserId, JSON.toJSONString(pushData));
            }
        }
        // 2. 分页查询消息，关联用户拿头像
        Page<GoodsMessage> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<GoodsMessage> wrapper = new LambdaQueryWrapper<>();
        // 双向聊天双方
        wrapper.and(w -> w.eq(GoodsMessage::getFromUserId, selfId).eq(GoodsMessage::getToUserId, otherId))
                .or(w -> w.eq(GoodsMessage::getFromUserId, otherId).eq(GoodsMessage::getToUserId, selfId));
        // goodsId>0 才筛选商品
        if(goodsId != null && goodsId > 0){
            wrapper.eq(GoodsMessage::getGoodsId, goodsId);
        }
        wrapper.orderByAsc(GoodsMessage::getCreateTime);

        // 关联sys_user查询头像（自定义mapper方法，下面提供）
        List<GoodsMessage> records = goodsMessageMapper.selectMsgWithAvatar(wrapper, page);
        page.setRecords(records);
        return page;
    }

}