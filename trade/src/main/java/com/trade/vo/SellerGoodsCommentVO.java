package com.trade.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.util.List;

@Data
public class SellerGoodsCommentVO {
    //商品信息
    private Long goodsId;
    private String goodsTitle;
    private String coverImg;

    //这条商品对应的所有评价
    @TableField(exist = false)
    private List<GoodsCommentVO> commentList;
}