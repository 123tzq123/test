package com.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.trade.domain.GoodsComment;
import com.trade.domain.IdleGoods;
import com.trade.domain.SysUser;
import com.trade.domain.TradeOrder;
import com.trade.dto.CommentPublishDTO;
import com.trade.mapper.GoodsCommentMapper;
import com.trade.mapper.IdleGoodsMapper;
import com.trade.mapper.SysUserMapper;
import com.trade.mapper.TradeOrderMapper;
import com.trade.service.GoodsCommentService;
import com.trade.vo.GoodsCommentVO;
import com.trade.vo.GoodsVO;
import com.trade.vo.SellerGoodsCommentVO;
import com.trade.vo.SellerHomeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GoodsCommentServiceImpl implements GoodsCommentService {
    @Resource
    private GoodsCommentMapper commentMapper;
    @Resource
    private TradeOrderMapper orderMapper;
    @Resource
    private IdleGoodsMapper goodsMapper;
    @Resource
    private SysUserMapper userMapper;


    @Override
    public void publishComment(Long loginUserId, CommentPublishDTO dto) {
        LambdaQueryWrapper<TradeOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(TradeOrder::getId, dto.getOrderId())
                .eq(TradeOrder::getBuyerId, loginUserId);
        TradeOrder order = orderMapper.selectOne(orderWrapper);
        if(order == null){
            throw new RuntimeException("订单不存在");
        }
        //订单状态为1才可以评价
        if(!order.getStatus().equals(1)){
            throw new RuntimeException("订单未完成，暂时不能评价");
        }
        GoodsComment comment = new GoodsComment();
        comment.setGoodsId(order.getGoodsId());
        comment.setOrderId(dto.getOrderId());
        comment.setBuyerId(loginUserId);
        comment.setSellerId(order.getSellerId());
        comment.setScore(dto.getScore());
        comment.setContent(dto.getContent());
        if(dto.getImgList() != null && dto.getImgList().size()>0){
            comment.setImgList(String.join(",", dto.getImgList()));
        }
        commentMapper.insert(comment);
    }

    @Override
    public List<GoodsCommentVO> getGoodsComment(Long goodsId) {
        List<GoodsCommentVO> commentList = commentMapper.getCommentByGoodsId(goodsId);
        for (GoodsCommentVO vo : commentList) {
            // 先获取字符串格式的图片
            if (StringUtils.hasText(vo.getImgList())) {
                List<String> imgs = Arrays.stream(vo.getImgList().split(","))
                        .collect(Collectors.toList());
                //新增一个临时字段或者新建DTO，不要在这个VO里修改；
                //方案：新建一个VO或者创建单独的属性存放解析后的图片数组
                vo.setImgListStr(imgs);
            }
        }
        return commentList;
    }

    @Override
    public SellerHomeVO getSellerHomeInfo(Long sellerId) {
        SellerHomeVO vo = new SellerHomeVO();
        SysUser seller = userMapper.selectById(sellerId);
        vo.setSellerInfo(seller);
        //平均分
        BigDecimal avgScore = commentMapper.getAvgScoreBySellerId(sellerId);
        if(avgScore != null){
            vo.setAvgScore(avgScore.setScale(1, BigDecimal.ROUND_HALF_UP));
        }
        LambdaQueryWrapper<IdleGoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IdleGoods::getUserId, sellerId);
        List<IdleGoods> goodsList = goodsMapper.selectList(wrapper);
        //转换 IdleGoods → GoodsVO
        List<GoodsVO> goodsVOList = goodsList.stream().map(item -> {
            GoodsVO goodsVO = new GoodsVO();
            BeanUtils.copyProperties(item, goodsVO);
            return goodsVO;
        }).collect(Collectors.toList());
        vo.setGoodsList(goodsVOList);
        return vo;
    }


    @Override
    public boolean isOrderCommented(Long orderId) {
        Integer count = commentMapper.countCommentByOrderId(orderId);
        return count > 0;
    }

    @Override
    public List<SellerGoodsCommentVO> getSellerAllComment(Long sellerId) {
        List<GoodsCommentVO> commentVOList = commentMapper.selectCommentBySellerId(sellerId);
        //1. 根据goodsId分组
        Map<Long, List<GoodsCommentVO>> groupByGoodsId = commentVOList.stream()
                .collect(Collectors.groupingBy(GoodsCommentVO::getGoodsId));

        //2. 查询商品信息
        List<SellerGoodsCommentVO> result = new ArrayList<>();
        for(Map.Entry<Long, List<GoodsCommentVO>> entry : groupByGoodsId.entrySet()){
            Long goodsId = entry.getKey();
            List<GoodsCommentVO> comments = entry.getValue();
            IdleGoods idleGoods = goodsMapper.selectById(goodsId);

            SellerGoodsCommentVO vo = new SellerGoodsCommentVO();
            vo.setGoodsId(goodsId);
            vo.setGoodsTitle(idleGoods.getTitle());
            if(StringUtils.hasText(idleGoods.getGoodsImg())){
                vo.setCoverImg(idleGoods.getGoodsImg().split(",")[0]);
            }
            vo.setCommentList(comments);
            result.add(vo);
        }
        return result;
    }

    // ========== 新增实现：查询当前买家自己的全部评价 ==========
    @Override
    public List<GoodsCommentVO> getMyCommentByBuyerId(Long buyerId) {
        // 直接调用新增mapper方法，一次性返回带buyerInfo的VO
        return commentMapper.selectMyCommentByBuyerId(buyerId);
    }
}