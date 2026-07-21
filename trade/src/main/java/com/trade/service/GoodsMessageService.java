package com.trade.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trade.domain.GoodsMessage;
import com.trade.dto.MessageDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trade.vo.ChatSessionVO;

import java.util.List;

public interface GoodsMessageService extends IService<GoodsMessage> {
    //保存聊天记录
    GoodsMessage saveMessage(MessageDTO dto, Long fromUserId);
    //获取该商品两个人的历史聊天记录
    Page<GoodsMessage> getHistoryMsg(Long goodsId, Long selfId, Long otherId, Integer pageNum, Integer pageSize);
    // 查询当前用户全部聊天会话列表（我的消息左侧会话）
    List<ChatSessionVO> getChatSessionList(Long loginUserId);
    // 查询当前用户总未读消息数量（导航栏红点）
    Long getTotalUnreadCount(Long loginUserId);
}