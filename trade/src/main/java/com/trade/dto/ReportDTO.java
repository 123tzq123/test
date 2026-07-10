package com.trade.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ReportDTO {
    @NotNull
    private Long goodsId;
    @NotBlank(message = "填写举报理由")
    private String reason;
}