package com.trade.vo;

import com.trade.domain.TradeOrder;
import lombok.Data;

@Data
public class OrderVO extends TradeOrder {
    private String goodsTitle;
    private String goodsImg;
}