package com.trade.domain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("goods_message")
public class GoodsMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodsId;
    private Long fromUserId;
    private String content;
    private LocalDateTime createTime;
}