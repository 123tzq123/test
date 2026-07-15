package com.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trade.domain.GoodsCollect;
import com.trade.domain.IdleGoods;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface GoodsCollectMapper extends BaseMapper<GoodsCollect> {
    //根据用户id查询收藏的商品列表（关联商品表）
    List<IdleGoods> getCollectGoodsByUserId(@Param("userId") Long userId);
}