package com.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trade.domain.GoodsMessage;
import com.trade.dto.MessageDTO;
import com.trade.mapper.GoodsMessageMapper;
import com.trade.service.MessageService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<GoodsMessageMapper, GoodsMessage> implements MessageService {
    @Override
    public void addMessage(MessageDTO dto, Long fromId) {
        GoodsMessage message = new GoodsMessage();
        message.setGoodsId(dto.getGoodsId());
        message.setFromUserId(fromId);
        message.setContent(dto.getContent());
        this.save(message);
    }

    @Override
    public List<GoodsMessage> getMessageByGoodsId(Long goodsId) {
        LambdaQueryWrapper<GoodsMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsMessage::getGoodsId,goodsId);
        wrapper.orderByDesc(GoodsMessage::getCreateTime);
        return this.list(wrapper);
    }
}