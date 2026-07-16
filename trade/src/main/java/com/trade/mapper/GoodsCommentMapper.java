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

    //根据卖家id，查询该卖家全部商品对应的评价
    @Select("SELECT gc.id,gc.goods_id goodsId,gc.score,gc.content,gc.img_list imgList,gc.create_time createTime,su.id buyerId,su.nickname,su.avatar " +
            "FROM goods_comment gc " +
            "LEFT JOIN sys_user su ON gc.buyer_id = su.id " +
            "LEFT JOIN idle_goods ig ON gc.goods_id = ig.id " +
            "WHERE ig.user_id = #{sellerId}")
    List<GoodsCommentVO> selectCommentBySellerId(@Param("sellerId") Long sellerId);
}