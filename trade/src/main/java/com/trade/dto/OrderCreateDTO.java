package com.trade.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class OrderCreateDTO {
    @NotNull(message = "商品id不能为空")
    private Long goodsId;
}