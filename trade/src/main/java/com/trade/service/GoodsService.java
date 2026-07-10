package com.trade.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trade.domain.IdleGoods;
import com.trade.dto.GoodsPublishDTO;

public interface GoodsService extends IService<IdleGoods> {
    //发布商品
    void publishGoods(GoodsPublishDTO dto, Long userId);
    //分页查询商品（热门商品先查Redis）
    Page<IdleGoods> getGoodsPage(Long categoryId, Integer pageNum, Integer pageSize);
    //管理员审核商品
    void auditGoods(Long goodsId, Integer status);
    //浏览量+1，Redis缓存
    void addViewCount(Long goodsId);
}