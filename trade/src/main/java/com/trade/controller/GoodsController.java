package com.trade.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trade.constant.AttributeConst;
import com.trade.dto.GoodsPublishDTO;
import com.trade.domain.IdleGoods;
import com.trade.service.GoodsService;
import com.trade.vo.ResultVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResultVO<Page<IdleGoods>> getList(Long categoryId, Integer pageNum, Integer pageSize){
        Page<IdleGoods> page = goodsService.getGoodsPage(categoryId,pageNum,pageSize);
        return ResultVO.success(page);
    }
}