package com.trade.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AiGoodsDetectVO {
    // 商品标题
    private String title;
    // 分类ID（后端固定映射）
    private Long categoryId;
    // 预估价格
    private BigDecimal price;
    // 商品描述
    private String content;
}