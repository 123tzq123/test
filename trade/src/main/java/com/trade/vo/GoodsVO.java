package com.trade.vo;

import com.trade.domain.IdleGoods;
import lombok.Data;

@Data
public class GoodsVO extends IdleGoods {
    private String sellerName;
    private String categoryName;
}