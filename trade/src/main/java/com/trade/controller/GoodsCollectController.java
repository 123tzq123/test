package com.trade.controller;

import com.trade.constant.AttributeConst;
import com.trade.dto.CollectDTO;
import com.trade.domain.IdleGoods;
import com.trade.service.GoodsCollectService;
import com.trade.vo.ResultVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/collect")
public class GoodsCollectController {

    @Resource
    private GoodsCollectService collectService;

    //从Cookie中获取userId
    private Long getUserId(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        System.out.println("cookies数组："+cookies);
        if(cookies == null) {
            System.out.println("后端没有接收到cookie");
            return null;
        }
        for(Cookie cookie : cookies){
            System.out.println("cookie名称："+cookie.getName()+" 值："+cookie.getValue());
            if("userId".equals(cookie.getName())){
                try {
                    Long id = Long.parseLong(cookie.getValue());
                    System.out.println("获取到userId = "+id);
                    return id;
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
        }
        System.out.println("cookie中没有userId");
        return null;
    }

    //收藏或者取消收藏
    @PostMapping("/change")
    public ResultVO<String> changeCollect(@RequestBody CollectDTO collectDTO){
        Long userId = collectDTO.getUserId();
        if(userId == null){
            return ResultVO.fail("请先登录");
        }
        String msg = collectService.collectGoods(userId, collectDTO);
        return ResultVO.success(msg);
    }


    //获取我的收藏列表（使用拦截器获取userId，前端不用传参）
    @PostMapping("/myList")
    public ResultVO<List<IdleGoods>> myCollectList(HttpServletRequest request){
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        if(userId == null){
            return ResultVO.fail("请登录");
        }
        List<IdleGoods> list = collectService.getMyCollect(userId);
        return ResultVO.success(list);
    }

    //判断商品是否被当前用户收藏
    @PostMapping("/isCollect")
    public ResultVO<Boolean> isCollect(@RequestBody CollectDTO collectDTO){
        Long userId = collectDTO.getUserId();
        if(userId == null){
            return ResultVO.success(false);
        }
        boolean flag = collectService.isCollect(userId, collectDTO.getGoodsId());
        return ResultVO.success(flag);
    }
}