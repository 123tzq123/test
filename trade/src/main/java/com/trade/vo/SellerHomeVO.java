package com.trade.vo;

import com.trade.domain.SysUser;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SellerHomeVO {
    //卖家基础信息
    private SysUser sellerInfo;
    //卖家商品平均得分（保留一位小数）
    private BigDecimal avgScore;
    //卖家发布的所有商品
    private List<GoodsVO> goodsList;
}