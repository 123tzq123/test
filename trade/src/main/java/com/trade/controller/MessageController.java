package com.trade.controller;

import com.trade.constant.AttributeConst;
import com.trade.domain.GoodsMessage;
import com.trade.dto.MessageDTO;
import com.trade.service.MessageService;
import com.trade.vo.ResultVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Resource
    private MessageService messageService;

    @PostMapping("/add")
    public ResultVO<Void> addMessage(@Validated @RequestBody MessageDTO dto, HttpServletRequest request){
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        messageService.addMessage(dto,userId);
        return ResultVO.success(null);
    }
    @GetMapping("/list/{goodsId}")
    public ResultVO<List<GoodsMessage>> getMsg(@PathVariable Long goodsId){
        List<GoodsMessage> list = messageService.getMessageByGoodsId(goodsId);
        return ResultVO.success(list);
    }
}