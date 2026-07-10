package com.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.trade.domain.GoodsMessage;
import com.trade.dto.MessageDTO;
import java.util.List;

public interface MessageService extends IService<GoodsMessage> {
    void addMessage(MessageDTO dto, Long fromId);
    List<GoodsMessage> getMessageByGoodsId(Long goodsId);
}