package com.trade.domain;
import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("goods_message")
public class GoodsMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodsId;
    private Long fromUserId;
    private Long toUserId;
    private String content;

    @TableField(fill = FieldFill.INSERT)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}