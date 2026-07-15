package com.trade.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("goods_collect")
public class GoodsCollect {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long goodsId;
    private LocalDateTime createTime;
}