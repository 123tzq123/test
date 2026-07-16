package com.trade.service;
import com.trade.dto.CommentPublishDTO;
import com.trade.vo.GoodsCommentVO;
import com.trade.vo.SellerGoodsCommentVO;
import com.trade.vo.SellerHomeVO;
import java.util.List;

public interface GoodsCommentService {
    //买家提交评价
    void publishComment(Long loginUserId, CommentPublishDTO dto);
    //获取商品下面所有评价
    List<GoodsCommentVO> getGoodsComment(Long goodsId);
    //获取卖家主页信息（头像昵称、平均分、发布商品）
    SellerHomeVO getSellerHomeInfo(Long sellerId);
    //判断订单是否已经评价
    boolean isOrderCommented(Long orderId);

    List<SellerGoodsCommentVO> getSellerAllComment(Long sellerId);
}