package com.trade.controller;

import com.trade.constant.AttributeConst;
import com.trade.dto.CommentPublishDTO;
import com.trade.service.GoodsCommentService;
import com.trade.vo.GoodsCommentVO;
import com.trade.vo.ResultVO;
import com.trade.vo.SellerGoodsCommentVO;
import com.trade.vo.SellerHomeVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class GoodsCommentController {
    @Resource
    private GoodsCommentService commentService;

    //买家提交评价
    @PostMapping("/publish")
    public ResultVO<Void> publishComment(@Validated @RequestBody CommentPublishDTO dto, HttpServletRequest request){
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        commentService.publishComment(userId, dto);
        return ResultVO.success(null);
    }

    //根据商品id获取评价列表（商品详情页调用，不需要登录）
    @GetMapping("/list/{goodsId}")
    public ResultVO<List<GoodsCommentVO>> getCommentList(@PathVariable Long goodsId){
        List<GoodsCommentVO> list = commentService.getGoodsComment(goodsId);
        return ResultVO.success(list);
    }

    //进入卖家个人主页
    @GetMapping("/seller/{sellerId}")
    public ResultVO<SellerHomeVO> getSellerHome(@PathVariable Long sellerId){
        SellerHomeVO vo = commentService.getSellerHomeInfo(sellerId);
        return ResultVO.success(vo);
    }

    @GetMapping("/sellerComment/{sellerId}")
    public ResultVO<List<SellerGoodsCommentVO>> getSellerComment(@PathVariable Long sellerId){
        List<SellerGoodsCommentVO> list = commentService.getSellerAllComment(sellerId);
        return ResultVO.success(list);
    }

    /**
     * 获取当前登录买家自己发布的所有评价（过往评价页面使用）
     */
    @GetMapping("/my")
    public ResultVO<List<GoodsCommentVO>> getMyComment(HttpServletRequest request){
        Long loginBuyerId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        List<GoodsCommentVO> myCommentList = commentService.getMyCommentByBuyerId(loginBuyerId);
        return ResultVO.success(myCommentList);
    }

}