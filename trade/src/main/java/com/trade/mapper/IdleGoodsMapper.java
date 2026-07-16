package com.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trade.domain.IdleGoods;
import org.apache.ibatis.annotations.Param;
import com.trade.vo.GoodsVO;

public interface IdleGoodsMapper extends BaseMapper<IdleGoods> {
    GoodsVO getGoodsDetailWithSeller(@Param("goodsId") Long goodsId);
}