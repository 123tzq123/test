package com.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trade.domain.GoodsCollect;
import com.trade.domain.IdleGoods;
import com.trade.dto.CollectDTO;
import com.trade.mapper.GoodsCollectMapper;
import com.trade.service.GoodsCollectService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service
public class GoodsCollectServiceImpl implements GoodsCollectService {

    @Resource
    private GoodsCollectMapper collectMapper;

    /**
     * 收藏/取消收藏
     * @param userId 当前登录用户id
     * @param collectDTO 商品id
     * @return 返回提示消息
     */
    @Override
    public String collectGoods(Long userId, CollectDTO collectDTO) {
        Long goodsId = collectDTO.getGoodsId();
        LambdaQueryWrapper<GoodsCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsCollect::getUserId,userId)
                .eq(GoodsCollect::getGoodsId,goodsId);
        GoodsCollect collect = collectMapper.selectOne(wrapper);
        if(collect != null){
            //已经收藏，执行取消收藏
            collectMapper.delete(wrapper);
            return "取消收藏成功";
        }else {
            //未收藏，添加收藏
            GoodsCollect goodsCollect = new GoodsCollect();
            goodsCollect.setUserId(userId);
            goodsCollect.setGoodsId(goodsId);
            collectMapper.insert(goodsCollect);
            return "收藏成功";
        }
    }

    //查询我的收藏商品
    @Override
    public List<IdleGoods> getMyCollect(Long userId) {
        return collectMapper.getCollectGoodsByUserId(userId);
    }

    //判断当前用户是否收藏该商品
    @Override
    public boolean isCollect(Long userId, Long goodsId) {
        LambdaQueryWrapper<GoodsCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsCollect::getUserId,userId)
                .eq(GoodsCollect::getGoodsId,goodsId);
        Long count = collectMapper.selectCount(wrapper);
        return count > 0;
    }
}