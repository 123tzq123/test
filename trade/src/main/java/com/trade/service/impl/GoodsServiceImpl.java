package com.trade.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trade.domain.IdleGoods;
import com.trade.dto.GoodsPublishDTO;
import com.trade.exception.GlobalException;
import com.trade.mapper.IdleGoodsMapper;
import com.trade.service.GoodsService;
import com.trade.util.RedisUtil;
import com.trade.vo.GoodsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    public Page<GoodsVO> getGoodsList(Integer pageNum, Integer pageSize, Long categoryId) {
        Page<IdleGoods> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<IdleGoods> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null && categoryId != 0) {
            wrapper.eq(IdleGoods::getCategoryId, categoryId);
        }
        //依旧保留只查询上架商品
        wrapper.eq(IdleGoods::getStatus, 1);
        Page<IdleGoods> goodsPage = this.page(page, wrapper);

        Page<GoodsVO> voPage = new Page<>();
        voPage.setCurrent(goodsPage.getCurrent());
        voPage.setSize(goodsPage.getSize());
        voPage.setTotal(goodsPage.getTotal());
        voPage.setPages(goodsPage.getPages());

        //批量转换 IdleGoods → GoodsVO
        List<GoodsVO> voList = goodsPage.getRecords().stream().map(goods -> {
            GoodsVO vo = new GoodsVO();
            BeanUtils.copyProperties(goods, vo); //自动复制名称相同属性，省去大量 set 代码
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
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
        String hashKey = "goods:view:hash" + goodsId;
        //使用Hash自增浏览量
        redisUtil.incrHash(hashKey, goodsId.toString(), 1);
    }

    @Override
    public GoodsVO getDetail(Long goodsId) {
        IdleGoods idleGoods = this.getById(goodsId);
        //商品不存在或者已经下架抛出异常
        if(idleGoods == null || idleGoods.getStatus() != 1){
            throw new GlobalException(404, "商品不存在或已下架");
        }
        //访问详情页，浏览量+1
        addViewCount(goodsId);
        GoodsVO vo = new GoodsVO();
        BeanUtils.copyProperties(idleGoods,vo);
        //后续联表查询给sellerName、categoryName赋值
        return vo;
    }

    @Override
    public Page<GoodsVO> getMyGoods(Integer pageNum, Integer pageSize, Long userId) {
        Page<IdleGoods> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<IdleGoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IdleGoods::getUserId,userId);
        //只查询上架(1)和下架(0)的数据，排除逻辑删除‑1
        wrapper.in(IdleGoods::getStatus, Arrays.asList(0,1));
        Page<IdleGoods> goodsPage = this.page(page,wrapper);
        Page<GoodsVO> voPage = new Page<>();
        voPage.setCurrent(goodsPage.getCurrent());
        voPage.setSize(goodsPage.getSize());
        voPage.setTotal(goodsPage.getTotal());
        voPage.setPages(goodsPage.getPages());
        List<GoodsVO> voList = goodsPage.getRecords().stream().map(item -> {
            GoodsVO vo = new GoodsVO();
            BeanUtils.copyProperties(item,vo);
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public void offSale(Long goodsId, Long loginUserId) {
        IdleGoods goods = this.getById(goodsId);
        if(goods == null){
            throw new GlobalException(404, "商品不存在");
        }
        //判断是否是本人商品
        if(!goods.getUserId().equals(loginUserId)){
            throw new GlobalException(404, "无权操作");
        }
        goods.setStatus(0);
        this.updateById(goods);
    }
    @Override
    public void onSale(Long goodsId, Long userId) {
        IdleGoods goods = this.getById(goodsId);
        if(!goods.getUserId().equals(userId)){
            throw new GlobalException(404, "无权操作");
        }
        goods.setStatus(1); //重新上架
        this.updateById(goods);
    }

    //物理删除（数据库彻底移除这条数据）
    @Override
    public void deleteGoods(Long goodsId, Long userId) {
        IdleGoods goods = this.getById(goodsId);
        if(goods == null || !goods.getUserId().equals(userId)){
            throw new RuntimeException("无权操作该商品");
        }
        //增加业务判断：商品处于上架状态不允许删除
        if(goods.getStatus() == 1){
            throw new RuntimeException("商品上架状态，不能删除，请先下架！");
        }
        //物理删除，数据库中数据被真实删除
        this.removeById(goodsId);
    }
}