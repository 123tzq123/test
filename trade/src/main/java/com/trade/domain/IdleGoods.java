package com.trade.domain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("idle_goods")
public class IdleGoods {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long categoryId;
    private String title;
    private String content;
    private BigDecimal price = new BigDecimal(0);
    private BigDecimal originalPrice;
    private String goodsImg;
    private Integer status;
    private Integer viewCount; //浏览量
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}