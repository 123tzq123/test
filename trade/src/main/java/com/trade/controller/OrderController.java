package com.trade.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trade.constant.AttributeConst;
import com.trade.dto.OrderCreateDTO;
import com.trade.service.OrderService;
import com.trade.vo.OrderVO;
import com.trade.vo.ResultVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    @PostMapping("/create")
    public ResultVO<Void> create(@Validated @RequestBody OrderCreateDTO dto, HttpServletRequest request){
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        orderService.createOrder(dto,userId);
        return ResultVO.success(null);
    }
    @PostMapping("/cancel/{orderId}")
    public ResultVO<Void> cancel(@PathVariable("orderId") Long orderId, HttpServletRequest request){
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        orderService.cancelOrder(orderId,userId);
        return ResultVO.success(null);
    }
    @PostMapping("/finish/{orderId}")
    public ResultVO<Void> finish(@PathVariable("orderId") Long orderId, HttpServletRequest request){
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        orderService.finishOrder(orderId,userId);
        return ResultVO.success(null);
    }
    @GetMapping("/my")
    public ResultVO<Page<OrderVO>> myOrder(Integer pageNum, Integer pageSize, HttpServletRequest request){
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        Page<OrderVO> page = orderService.getMyOrder(userId,pageNum,pageSize);
        return ResultVO.success(page);
    }
    @GetMapping("/seller")
    public ResultVO<Page<OrderVO>> getSellerOrder(Integer pageNum,Integer pageSize,HttpServletRequest request){
        Long sellerId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        Page<OrderVO> page = orderService.getSellerOrder(sellerId,pageNum,pageSize);
        return ResultVO.success(page);
    }
}