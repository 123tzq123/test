package com.trade.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trade.domain.IdleGoods;
import com.trade.dto.GoodsPublishDTO;
import com.trade.dto.GoodsUpdateDTO;
import com.trade.vo.GoodsVO;

public interface GoodsService extends IService<IdleGoods> {
    //发布商品
    void publishGoods(GoodsPublishDTO dto, Long userId);
    //分页查询商品（热门商品先查Redis）
    Page<GoodsVO> getGoodsList(Integer pageNum, Integer pageSize, Long categoryId);
    //管理员审核商品
    void auditGoods(Long goodsId, Integer status);
    //浏览量+1，Redis缓存
    void addViewCount(Long goodsId);
    //根据Id获取商品信息
    GoodsVO getDetail(Long goodsId);
    //我发布的商品
    Page<GoodsVO> getMyGoods(Integer pageNum, Integer pageSize, Long userId);
    //下架我的商品
    void offSale(Long goodsId, Long loginUserId);

    void onSale(Long goodsId, Long userId);

    void deleteGoods(Long goodsId, Long userId);

    IdleGoods getGoodsById(Long goodsId);

    void updateGoods(GoodsUpdateDTO dto, Long loginUserId);
}