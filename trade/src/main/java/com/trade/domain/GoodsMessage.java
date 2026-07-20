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

    // 新增：消息阅读状态 0=未读 1=已读
    private Integer readStatus;

    // 数据库不存在该字段，仅查询关联用户时临时返回头像，不持久化
    @TableField(exist = false)
    private String fromUserAvatar;


    @TableField(fill = FieldFill.INSERT)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}