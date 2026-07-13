package com.trade.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trade.constant.AttributeConst;
import com.trade.dto.GoodsPublishDTO;
import com.trade.domain.IdleGoods;
import com.trade.dto.GoodsUpdateDTO;
import com.trade.service.GoodsService;
import com.trade.util.JwtUtil;
import com.trade.vo.GoodsVO;
import com.trade.vo.ResultVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Resource
    private GoodsService goodsService;

    //发布闲置商品
    @PostMapping("/publish")
    public ResultVO<Void> publish(@Validated @RequestBody GoodsPublishDTO dto, HttpServletRequest request){
        //从token解析登录用户id
        Long userId = (Long)request.getAttribute(AttributeConst.LOGIN_USER_ID);
        goodsService.publishGoods(dto,userId);
        return ResultVO.success(null);
    }

    //分页查询商品列表
    @GetMapping("/list")
    public ResultVO<Page<GoodsVO>> getGoodsList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "1") Integer pageSize,
                                                Long categoryId) {
        Page<GoodsVO> pageData = goodsService.getGoodsList(pageNum, pageSize, categoryId);
        return ResultVO.success(pageData);
    }
    /**
     * 获取商品详情
     */
    @GetMapping("/detail/{goodsId}")
    public ResultVO<GoodsVO> getGoodsDetail(@PathVariable Long goodsId){
        GoodsVO goodsVO = goodsService.getDetail(goodsId);
        return ResultVO.success(goodsVO);
    }

    @GetMapping("/my")
    public ResultVO<Page<GoodsVO>> getMyGoods(Integer pageNum, Integer pageSize, HttpServletRequest request){
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        Page<GoodsVO> page = goodsService.getMyGoods(pageNum,pageSize,userId);
        return ResultVO.success(page);
    }

    @PutMapping("/offSale/{goodsId}")
    public ResultVO<Void> offSale(@PathVariable Long goodsId, HttpServletRequest request){
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        goodsService.offSale(goodsId,userId);
        return ResultVO.success(null);
    }

    //新增：商品上架
    @PutMapping("/onSale/{goodsId}")
    public ResultVO<Void> onSale(@PathVariable Long goodsId, HttpServletRequest request){
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        goodsService.onSale(goodsId,userId);
        return ResultVO.success(null);
    }

    //新增：商品删除（逻辑删除）
    @DeleteMapping("/delete/{goodsId}")
    public ResultVO<Void> deleteGoods(@PathVariable Long goodsId, HttpServletRequest request){
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        goodsService.deleteGoods(goodsId,userId);
        return ResultVO.success(null);
    }

    /**
     * 根据商品id获取商品信息，编辑页面回显
     * @param goodsId 商品id
     * @return IdleGoods原始数据
     */
    @GetMapping("/getById/{id}")
    public ResultVO<IdleGoods> getGoodsById(@PathVariable("id") Long goodsId) {
        IdleGoods idleGoods = goodsService.getGoodsById(goodsId);
        return ResultVO.success(idleGoods);
    }

    /**
     * 修改商品信息
     */
    @PutMapping("/update")
    public ResultVO<Void> updateGoods(@Validated @RequestBody GoodsUpdateDTO dto, HttpServletRequest request) {
        Long loginUserId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        goodsService.updateGoods(dto, loginUserId);
        return ResultVO.success(null);
    }


}