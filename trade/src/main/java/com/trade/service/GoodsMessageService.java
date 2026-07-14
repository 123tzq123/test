package com.trade.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trade.domain.GoodsMessage;
import com.trade.dto.MessageDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface GoodsMessageService extends IService<GoodsMessage> {
    //保存聊天记录
    GoodsMessage saveMessage(MessageDTO dto, Long fromUserId);
    //获取该商品两个人的历史聊天记录
    Page<GoodsMessage> getHistoryMsg(Long goodsId, Long selfId, Long otherId, Integer pageNum, Integer pageSize);
}