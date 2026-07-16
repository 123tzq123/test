package com.trade.domain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("goods_comment")
public class GoodsComment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodsId;
    private Long orderId;
    private Long buyerId;
    private Long sellerId;
    private Integer score;
    private String content;
    private String imgList;
    private LocalDateTime createTime;
}