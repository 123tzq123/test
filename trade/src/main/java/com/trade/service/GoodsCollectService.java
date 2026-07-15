package com.trade.service;
import com.trade.dto.CollectDTO;
import com.trade.domain.IdleGoods;
import java.util.List;

public interface GoodsCollectService {
    //收藏或取消收藏
    String collectGoods(Long userId, CollectDTO collectDTO);
    //获取我的收藏列表
    List<IdleGoods> getMyCollect(Long userId);
    //判断该商品是否被当前用户收藏（首页用来展示是否已收藏）
    boolean isCollect(Long userId,Long goodsId);
}