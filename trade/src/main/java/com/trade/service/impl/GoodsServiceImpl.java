package com.trade.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trade.domain.IdleGoods;
import com.trade.dto.GoodsPublishDTO;
import com.trade.mapper.IdleGoodsMapper;
import com.trade.service.GoodsService;
import com.trade.util.RedisUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class GoodsServiceImpl extends ServiceImpl<IdleGoodsMapper, IdleGoods> implements GoodsService {
    @Resource
    private RedisUtil redisUtil;

    @Override
    public void publishGoods(GoodsPublishDTO dto, Long userId) {
        IdleGoods goods = new IdleGoods();
        goods.setUserId(userId);
        goods.setCategoryId(dto.getCategoryId());
        goods.setTitle(dto.getTitle());
        goods.setContent(dto.getContent());
        goods.setPrice(dto.getPrice());
        goods.setOriginalPrice(dto.getOriginalPrice());
        goods.setGoodsImg(dto.getGoodsImg());
        goods.setStatus(0); //0待审核
        this.save(goods);
    }

    @Override
    public Page<IdleGoods> getGoodsPage(Long categoryId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<IdleGoods> wrapper = new LambdaQueryWrapper<>();
        if(categoryId != null){
            wrapper.eq(IdleGoods::getCategoryId,categoryId);
        }
        wrapper.eq(IdleGoods::getStatus,1); //只查上架商品
        Page<IdleGoods> page = new Page<>(pageNum, pageSize);
        return this.page(page, wrapper);
    }

    @Override
    public void auditGoods(Long goodsId, Integer status) {
        IdleGoods goods = this.getById(goodsId);
        goods.setStatus(status);
        this.updateById(goods);
    }

    @Override
    public void addViewCount(Long goodsId) {
        //Redis做浏览量缓存，key:goods:view:id
        String key = "goods:view:" + goodsId;
        redisUtil.incr(key,1);
    }
}