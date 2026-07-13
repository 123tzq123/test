package com.trade.vo;
import lombok.Data;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
public class GoodsVO {
    //商品基础字段，从IdleGoods获取
    private Long id;
    private String title;
    private BigDecimal price;
    private String description;
    private Long categoryId;
    private Long userId;

    //VO扩展字段，数据库不存在，页面展示用
    private String sellerName;
    private String categoryName;
    private Integer status; // 新增
    //新增图片字段
    private String goodsImg;

    // transient注解不会存入数据库，仅后端给前端返回使用
    @Transient
    private List<String> imgList;

    @Transient
    private String coverImg;

    //拆分图片数组给前端详情页
    public List<String> getImgList() {
        if(Objects.isNull(goodsImg) || goodsImg.trim().length() ==0){
            return new ArrayList<>();
        }
        return Arrays.asList(goodsImg.split(","));
    }

    //获取第一张图片作为首页封面
    public String getCoverImg(){
        if(Objects.isNull(goodsImg) || goodsImg.trim().length() ==0){
            return null;
        }
        String[] split = goodsImg.split(",");
        return split[0];
    }
}