package com.trade.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trade.domain.IdleGoods;
import com.trade.dto.GoodsPublishDTO;
import com.trade.dto.GoodsQueryDTO;
import com.trade.dto.GoodsUpdateDTO;
import com.trade.vo.GoodsVO;

public interface GoodsService extends IService<IdleGoods> {
    void publishGoods(GoodsPublishDTO dto, Long userId);

    Page<GoodsVO> getGoodsList(Integer pageNum, Integer pageSize, Long categoryId);

    void auditGoods(Long goodsId, Integer status);

    //更新方法签名，新增userId
    void addViewCount(Long goodsId, Long userId);

    //新增获取浏览量方法
    Integer getViewCount(Long goodsId);

    //新增入参userId
    GoodsVO getDetail(Long goodsId, Long userId);

    Page<GoodsVO> getMyGoods(Integer pageNum, Integer pageSize, Long userId);

    void offSale(Long goodsId, Long loginUserId);

    void onSale(Long goodsId, Long userId);

    void deleteGoods(Long goodsId, Long userId);

    IdleGoods getGoodsById(Long goodsId);

    void updateGoods(GoodsUpdateDTO dto, Long loginUserId);

    Page<IdleGoods> findGoodsByCondition(GoodsQueryDTO queryDTO);
}