package com.trade.vo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsVO {
    //商品基础字段，从IdleGoods获取
    private Long id;
    private String title;
    private BigDecimal price;
    private String description;
    private Long categoryId;
    private Long userId;

    //VO扩展字段，数据库不存在，页面展示用
    private String sellerName;
    private String categoryName;
    private Integer status; // 新增
}