package com.trade.mapper;
import com.trade.domain.GoodsComment;
import com.trade.vo.GoodsCommentVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;
import java.util.List;

public interface GoodsCommentMapper {
    //新增评价
    int insert(GoodsComment comment);

    //根据商品id获取该商品全部评价（关联买家信息）
    List<GoodsCommentVO> getCommentByGoodsId(@Param("goodsId") Long goodsId);

    //查询卖家全部评价的平均分
    @Select("SELECT IFNULL(AVG(score),0) FROM goods_comment WHERE seller_id = #{sellerId}")
    BigDecimal getAvgScoreBySellerId(@Param("sellerId") Long sellerId);

    //判断该订单是否已经评价，返回1代表已评价，0未评价
    @Select("SELECT COUNT(*) FROM goods_comment WHERE order_id = #{orderId}")
    Integer countCommentByOrderId(@Param("orderId") Long orderId);

    @Select("SELECT gc.id,gc.goods_id goodsId,gc.score,gc.content,gc.img_list imgList,gc.create_time createTime," +
            "su.id \"buyerInfo.id\",su.nickname \"buyerInfo.nickname\",su.avatar \"buyerInfo.avatar\",ig.title goodsTitle " +
            "FROM goods_comment gc " +
            "LEFT JOIN sys_user su ON gc.buyer_id = su.id " +
            "LEFT JOIN idle_goods ig ON gc.goods_id = ig.id " +
            "WHERE ig.user_id = #{sellerId}")
    List<GoodsCommentVO> selectCommentBySellerId(@Param("sellerId") Long sellerId);

    // ========== 修改后的：查询当前买家自己发布的所有评价（左联商品+卖家用户，带出卖家信息） ==========
    @Select("SELECT gc.id,gc.goods_id goodsId,gc.score,gc.content,gc.img_list imgList,gc.create_time createTime,ig.title goodsTitle," +
            "su.id \"buyerInfo.id\",su.nickname \"buyerInfo.nickname\",su.avatar \"buyerInfo.avatar\"," +
            "seller.id \"goodsSeller.id\",seller.nickname \"goodsSeller.nickname\",seller.avatar \"goodsSeller.avatar\" " +
            "FROM goods_comment gc " +
            "LEFT JOIN sys_user su ON gc.buyer_id = su.id " +
            "LEFT JOIN idle_goods ig ON gc.goods_id = ig.id " +
            "LEFT JOIN sys_user seller ON ig.user_id = seller.id " +
            "WHERE gc.buyer_id = #{buyerId}")
    List<GoodsCommentVO> selectMyCommentByBuyerId(@Param("buyerId") Long buyerId);
}