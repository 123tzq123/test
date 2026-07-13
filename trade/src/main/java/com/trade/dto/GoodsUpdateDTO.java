package com.trade.dto;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class GoodsUpdateDTO {
    @NotNull(message = "商品id不能为空")
    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    //多张图片url，逗号拼接
    private String goodsImg;
}