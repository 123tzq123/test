package com.trade.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

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
    private String content;
    private Long categoryId;
    private Long userId;
    private Integer viewCount;

    //VO扩展字段，数据库不存在，页面展示用
    private String sellerName;
    private String avatar; // 卖家头像
    private String categoryName;
    private Integer status;
    private String goodsImg;

    //数据库中不存在该字段，Mybatis‑Plus忽略此字段
    @TableField(exist = false)
    private List<String> imgList;

    @TableField(exist = false)
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