package com.trade.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class GoodsQueryDTO {
    private Long categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String title; //对应实体类里面的商品名称字段title，不是goodsName
    private Integer pageNum;
    private Integer pageSize;
}