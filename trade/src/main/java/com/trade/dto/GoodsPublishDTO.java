package com.trade.dto;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class GoodsPublishDTO {

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;

    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格不能小于0")
    private BigDecimal price;

    private BigDecimal originalPrice;
    private String goodsImg;
}