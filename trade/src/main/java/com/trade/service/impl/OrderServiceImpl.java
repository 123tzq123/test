package com.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trade.domain.IdleGoods;
import com.trade.domain.SysUser;
import com.trade.domain.TradeOrder;
import com.trade.dto.OrderCreateDTO;
import com.trade.exception.GlobalException;
import com.trade.mapper.IdleGoodsMapper;
import com.trade.mapper.SysUserMapper;
import com.trade.mapper.TradeOrderMapper;
import com.trade.service.OrderService;
import com.trade.vo.OrderVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<TradeOrderMapper, TradeOrder> implements OrderService {
    @Resource
    private IdleGoodsMapper goodsMapper;
    @Resource
    private SysUserMapper userMapper;

    @Override
    @Transactional
    public void createOrder(OrderCreateDTO dto, Long buyerId) {
        Long goodsId = dto.getGoodsId();
        IdleGoods goods = goodsMapper.selectById(goodsId);
        if(goods == null){
            throw new GlobalException(500,"商品不存在");
        }
        if(goods.getStatus() !=1){
            throw new GlobalException(500,"商品不能交易");
        }
        if(goods.getUserId().equals(buyerId)){
            throw new GlobalException(500,"不能购买自己商品");
        }
        //判断商品是否已经产生订单
        LambdaQueryWrapper<TradeOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(TradeOrder::getGoodsId,goodsId)
                .ne(TradeOrder::getStatus,2);
        TradeOrder existOrder = this.getOne(orderWrapper);
        if(existOrder != null){
            throw new GlobalException(500,"商品已经被别人下单");
        }
        //创建订单
        TradeOrder order = new TradeOrder();
        order.setOrderNo(UUID.randomUUID().toString().replace("-",""));
        order.setGoodsId(goodsId);
        order.setBuyerId(buyerId);
        order.setSellerId(goods.getUserId());
        order.setPrice(goods.getPrice());
        order.setStatus(0);
        this.save(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        TradeOrder order = this.getById(orderId);
        if(order == null || !order.getBuyerId().equals(userId)){
            throw new GlobalException(500,"无权限操作");
        }
        if(order.getStatus() !=0){
            throw new GlobalException(500,"只有待确认订单才可以取消");
        }
        order.setStatus(2);
        this.updateById(order);
    }

    @Override
    @Transactional
    public void finishOrder(Long orderId, Long userId) {
        TradeOrder order = this.getById(orderId);
        if(order == null || !order.getBuyerId().equals(userId)){
            throw new GlobalException(500,"无权限");
        }
        if(order.getStatus()!=0){
            throw new GlobalException(500,"订单状态异常");
        }
        order.setStatus(1);
        this.updateById(order);
        //修改商品状态为已售出
        IdleGoods goods = new IdleGoods();
        goods.setId(order.getGoodsId());
        goods.setStatus(3);
        goodsMapper.updateById(goods);
    }

    @Override
    public Page<OrderVO> getMyOrder(Long userId, Integer pageNum, Integer pageSize) {
        Page<TradeOrder> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<TradeOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TradeOrder::getBuyerId,userId);
        Page<TradeOrder> orderPage = this.page(page,wrapper);
        Page<OrderVO> voPage = new Page<>();
        voPage.setPages(orderPage.getPages());
        voPage.setTotal(orderPage.getTotal());
        voPage.setCurrent(orderPage.getCurrent());
        voPage.setSize(orderPage.getSize());
        voPage.setRecords(orderPage.getRecords().stream().map(item->{
            OrderVO vo = new OrderVO();
            vo.setId(item.getId());
            vo.setOrderNo(item.getOrderNo());
            vo.setGoodsId(item.getGoodsId());
            vo.setBuyerId(item.getBuyerId());
            vo.setSellerId(item.getSellerId());
            vo.setPrice(item.getPrice());
            vo.setStatus(item.getStatus());
            vo.setCreateTime(item.getCreateTime());
            IdleGoods goods = goodsMapper.selectById(item.getGoodsId());
            vo.setGoodsTitle(goods.getTitle());
            vo.setGoodsImg(goods.getGoodsImg());
            return vo;
        }).collect(Collectors.toList()));
        return voPage;
    }
}