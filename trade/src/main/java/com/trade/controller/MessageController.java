package com.trade.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trade.constant.AttributeConst;
import com.trade.domain.GoodsMessage;
import com.trade.exception.GlobalException;
import com.trade.service.GoodsMessageService;
import com.trade.vo.ResultVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Resource
    private GoodsMessageService goodsMessageService;

    //获取历史聊天记录
    @GetMapping("/history")
    public ResultVO<Page<GoodsMessage>> getHistoryMsg(@RequestParam Long goodsId,
                                                      @RequestParam Long otherId,
                                                      @RequestParam(defaultValue = "1") Integer pageNum,
                                                      @RequestParam(defaultValue = "20") Integer pageSize,
                                                      HttpServletRequest request){
        //手动判断参数
        if(goodsId == null || goodsId <=0 || otherId == null || otherId <=0){
            throw new GlobalException(500,"参数错误");
        }
        Long selfId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        Page<GoodsMessage> page = goodsMessageService.getHistoryMsg(goodsId,selfId,otherId,pageNum,pageSize);
        return ResultVO.success(page);
    }
}