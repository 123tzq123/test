package com.trade.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trade.domain.IdleGoods;
import com.trade.dto.GoodsPublishDTO;
import com.trade.dto.GoodsQueryDTO;
import com.trade.dto.GoodsUpdateDTO;
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
import java.util.concurrent.TimeUnit;

@Service
public class GoodsServiceImpl extends ServiceImpl<IdleGoodsMapper, IdleGoods> implements GoodsService {
    @Resource
    private RedisUtil redisUtil;

    // 全局Hash-key存放所有商品总浏览量
    private static final String GOODS_VIEW_HASH = "goods:view:hash";
    // 用户访问记录key前缀，30分钟内同一用户访问同一商品只计数一次
    private static final String GOODS_USER_VIEW = "goods:userView:";
    // 30分钟，单位秒
    private static final long VIEW_EXPIRE_SECONDS = 30 * 60;

    @Override
    public void publishGoods(GoodsPublishDTO dto, Long userId) {
        IdleGoods goods = new IdleGoods();
        goods.setTitle(dto.getTitle());
        goods.setPrice(dto.getPrice());
        goods.setCategoryId(dto.getCategoryId());
        goods.setContent(dto.getContent());
        goods.setOriginalPrice(dto.getOriginalPrice());
        //手动设置发布者id
        goods.setUserId(userId);
        goods.setStatus(1);
        goods.setViewCount(0); //新建商品浏览量初始化为0
        if (dto.getImgList() != null && dto.getImgList().size() > 0) {
            goods.setGoodsImg(String.join(",", dto.getImgList()));
        } else {
            goods.setGoodsImg("");
        }
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

        //批量转换 IdleGoods → GoodsVO，并赋值浏览量
        List<GoodsVO> voList = goodsPage.getRecords().stream().map(goods -> {
            GoodsVO vo = new GoodsVO();
            BeanUtils.copyProperties(goods, vo);
            //从Redis读取浏览量
            Long viewNum = redisUtil.getHashValue(GOODS_VIEW_HASH, goods.getId().toString());
            if (viewNum > 0) {
                vo.setViewCount(viewNum.intValue());
            } else {
                vo.setViewCount(goods.getViewCount() == null ? 0 : goods.getViewCount());
            }
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

    //修改后的浏览量增加方法：增加userId参数，实现30分钟去重统计
    @Override
    public void addViewCount(Long goodsId, Long userId) {
        //未登录用户不统计浏览量
        if (userId == null) {
            return;
        }
        String userViewKey = GOODS_USER_VIEW + userId + ":" + goodsId;
        //30分钟之内访问过，直接返回，浏览量不加
        if (redisUtil.hasKey(userViewKey)) {
            return;
        }
        //浏览量 +1，存入全局hash
        redisUtil.incrHash(GOODS_VIEW_HASH, goodsId.toString(), 1);
        //设置用户本次访问记录，30分钟过期
        redisUtil.setEx(userViewKey, "1", VIEW_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    //新增：获取商品浏览量
    @Override
    public Integer getViewCount(Long goodsId) {
        // 获取Redis中还未同步到MySQL的新增浏览量
        Long redisAddView = redisUtil.getHashValue(GOODS_VIEW_HASH, goodsId.toString());
        // 获取数据库里面已经持久化的浏览量
        IdleGoods goods = this.getById(goodsId);
        Long dbView = goods.getViewCount() == null ? 0L : goods.getViewCount();
        // 数据库值 + Redis增量 = 最终展示给前端的总浏览量
        return (int) (dbView + redisAddView);
    }

    @Override
    public GoodsVO getDetail(Long goodsId, Long userId) {
        //直接拿到已经包含sellerName和avatar的VO对象，省去复制对象步骤
        GoodsVO vo = baseMapper.getGoodsDetailWithSeller(goodsId);
        if (vo == null || vo.getStatus() != 1) {
            throw new GlobalException(404, "商品不存在或已下架");
        }
        //传入当前登录的userId
        addViewCount(goodsId, userId);
        //设置浏览量给VO
        vo.setViewCount(getViewCount(goodsId));
        //图片拆分逻辑不变，由GoodsVO里面getImgList()完成
        return vo;
    }

    @Override
    public Page<GoodsVO> getMyGoods(Integer pageNum, Integer pageSize, Long userId) {
        Page<IdleGoods> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<IdleGoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IdleGoods::getUserId, userId);
        //只查询上架(1)和下架(0)的数据，排除逻辑删除‑1
        wrapper.in(IdleGoods::getStatus, Arrays.asList(0, 1));
        Page<IdleGoods> goodsPage = this.page(page, wrapper);
        Page<GoodsVO> voPage = new Page<>();
        voPage.setCurrent(goodsPage.getCurrent());
        voPage.setSize(goodsPage.getSize());
        voPage.setTotal(goodsPage.getTotal());
        voPage.setPages(goodsPage.getPages());
        List<GoodsVO> voList = goodsPage.getRecords().stream().map(item -> {
            GoodsVO vo = new GoodsVO();
            BeanUtils.copyProperties(item, vo);
            Long viewNum = redisUtil.getHashValue(GOODS_VIEW_HASH, item.getId().toString());
            if (viewNum > 0) {
                vo.setViewCount(viewNum.intValue());
            } else {
                vo.setViewCount(item.getViewCount() == null ? 0 : item.getViewCount());
            }
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public void offSale(Long goodsId, Long loginUserId) {
        IdleGoods goods = this.getById(goodsId);
        if (goods == null) {
            throw new GlobalException(404, "商品不存在");
        }
        //判断是否是本人商品
        if (!goods.getUserId().equals(loginUserId)) {
            throw new GlobalException(404, "无权操作");
        }
        goods.setStatus(0);
        this.updateById(goods);
    }

    @Override
    public void onSale(Long goodsId, Long userId) {
        IdleGoods goods = this.getById(goodsId);
        if (!goods.getUserId().equals(userId)) {
            throw new GlobalException(404, "无权操作");
        }
        goods.setStatus(1); //重新上架
        this.updateById(goods);
    }

    //物理删除（数据库彻底移除这条数据）
    @Override
    public void deleteGoods(Long goodsId, Long userId) {
        IdleGoods goods = this.getById(goodsId);
        if (goods == null || !goods.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该商品");
        }
        //增加业务判断：商品处于上架状态不允许删除
        if (goods.getStatus() == 1) {
            throw new RuntimeException("商品上架状态，不能删除，请先下架！");
        }
        //物理删除，数据库中数据被真实删除
        this.removeById(goodsId);
    }

    @Override
    public IdleGoods getGoodsById(Long goodsId) {
        return this.getById(goodsId);
    }

    @Override
    public void updateGoods(GoodsUpdateDTO dto, Long loginUserId) {
        IdleGoods goods = this.getById(dto.getId());
        //权限校验：只能修改自己发布的商品
        if (!goods.getUserId().equals(loginUserId)) {
            throw new GlobalException(500, "你无权修改别人的商品");
        }
        BeanUtils.copyProperties(dto, goods);
        this.updateById(goods);
    }

    @Override
    public Page<IdleGoods> findGoodsByCondition(GoodsQueryDTO queryDTO) {
        Page<IdleGoods> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<IdleGoods> wrapper = new LambdaQueryWrapper<>();
        //只查询上架商品 status=1
        wrapper.eq(IdleGoods::getStatus, 1);

        //分类筛选
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(IdleGoods::getCategoryId, queryDTO.getCategoryId());
        }
        //价格区间筛选
        if (queryDTO.getMinPrice() != null) {
            wrapper.ge(IdleGoods::getPrice, queryDTO.getMinPrice());
        }
        if (queryDTO.getMaxPrice() != null) {
            wrapper.le(IdleGoods::getPrice, queryDTO.getMaxPrice());
        }
        //商品名称模糊查询，使用title字段
        if (queryDTO.getTitle() != null && !queryDTO.getTitle().trim().isEmpty()) {
            wrapper.like(IdleGoods::getTitle, queryDTO.getTitle().trim());
            System.out.println("title: " + queryDTO.getTitle());
        }
        //按创建时间降序
        wrapper.orderByDesc(IdleGoods::getCreateTime);
        this.page(page, wrapper);
        return page;
    }
}