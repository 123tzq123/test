package com.trade.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MessageDTO {
    @NotNull(message = "商品id不能为空")
    private Long goodsId;
    @NotNull(message = "接收方id不能为空")
    private Long toUserId;
    @NotBlank(message = "消息内容不能为空")
    private String content;
}