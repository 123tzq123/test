package com.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trade.domain.GoodsMessage;
import com.trade.dto.MessageDTO;
import com.trade.mapper.GoodsMessageMapper;
import com.trade.service.GoodsMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class GoodsMessageServiceImpl extends ServiceImpl<GoodsMessageMapper, GoodsMessage> implements GoodsMessageService {

    @Override
    public GoodsMessage saveMessage(MessageDTO dto, Long fromUserId) {
        GoodsMessage goodsMessage = new GoodsMessage();
        goodsMessage.setGoodsId(dto.getGoodsId());
        goodsMessage.setFromUserId(fromUserId);
        goodsMessage.setToUserId(dto.getToUserId());
        goodsMessage.setContent(dto.getContent());
        //手动赋值，不再依赖自动填充
        goodsMessage.setCreateTime(LocalDateTime.now());
        this.save(goodsMessage);
        return goodsMessage;
    }

    @Override
    public Page<GoodsMessage> getHistoryMsg(Long goodsId, Long selfId, Long otherId, Integer pageNum, Integer pageSize) {
        Page<GoodsMessage> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<GoodsMessage> wrapper = new LambdaQueryWrapper<>();
        // 括号包裹双向聊天条件，之后再并且goodsId相等
        wrapper.and(w -> w.eq(GoodsMessage::getFromUserId, selfId).eq(GoodsMessage::getToUserId, otherId)
                        .or().eq(GoodsMessage::getFromUserId, otherId).eq(GoodsMessage::getToUserId, selfId))
                .eq(GoodsMessage::getGoodsId, goodsId)
                .orderByAsc(GoodsMessage::getCreateTime);
        this.page(page, wrapper);
        return page;
    }
}