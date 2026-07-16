package com.trade.dto;
import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CommentPublishDTO {
    @NotNull(message = "订单id不能为空")
    private Long orderId;
    @Min(1)
    @Max(5)
    @NotNull(message = "评分1‑5分")
    private Integer score;
    private String content;
    //图片url集合
    private List<String> imgList;
}