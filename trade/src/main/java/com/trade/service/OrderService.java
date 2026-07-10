package com.trade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trade.domain.TradeOrder;
import com.trade.dto.OrderCreateDTO;
import com.trade.vo.OrderVO;

public interface OrderService extends IService<TradeOrder> {
    void createOrder(OrderCreateDTO dto, Long buyerId);
    void cancelOrder(Long orderId, Long userId);
    void finishOrder(Long orderId, Long userId);
    Page<OrderVO> getMyOrder(Long userId,Integer pageNum,Integer pageSize);
}