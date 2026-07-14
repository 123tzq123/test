package com.trade.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long goodsId;
    private Long buyerId;
    private Long sellerId;
    private BigDecimal price;
    private Integer status; //0待确认 1交易完成 2取消订单
    private LocalDateTime createTime;

    //商品信息
    private String goodsTitle;
    private String goodsImg;

    //拆分图片数组给前端
    public List<String> getImgList(){
        if(goodsImg == null || goodsImg.isEmpty()){
            return new ArrayList<>();
        }
        return Arrays.asList(goodsImg.split(","));
    }

    //封面图片
    public String getCoverImg(){
        if(goodsImg == null || goodsImg.isEmpty()){
            return null;
        }
        String[] split = goodsImg.split(",");
        return split[0];
    }
}