package com.trade.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trade.constant.AttributeConst;
import com.trade.domain.GoodsMessage;
import com.trade.dto.MessageDTO;
import com.trade.exception.GlobalException;
import com.trade.service.GoodsMessageService;
import com.trade.vo.ChatSessionVO;
import com.trade.vo.ResultVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Resource
    private GoodsMessageService goodsMessageService;

    // 获取历史聊天记录（原有接口不变）
    @GetMapping("/history")
    public ResultVO<Page<GoodsMessage>> getHistoryMsg(
            @RequestParam Long goodsId,
            @RequestParam Long otherId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            HttpServletRequest request){
        if(otherId == null || otherId <=0){
            throw new GlobalException(500,"参数错误");
        }
        Long selfId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        Page<GoodsMessage> page = goodsMessageService.getHistoryMsg(goodsId, selfId, otherId, pageNum, pageSize);
        return ResultVO.success(page);
    }

    // 会话列表
    @GetMapping("/session/list")
    public ResultVO<List<ChatSessionVO>> getChatSessionList(HttpServletRequest request){
        Long loginUserId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        List<ChatSessionVO> sessionList = goodsMessageService.getChatSessionList(loginUserId);
        return ResultVO.success(sessionList);
    }

    // 未读消息总数
    @GetMapping("/unread/total")
    public ResultVO<Long> getUnreadTotal(HttpServletRequest request){
        Long loginUserId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        Long totalUnread = goodsMessageService.getTotalUnreadCount(loginUserId);
        return ResultVO.success(totalUnread);
    }

    @PostMapping("/save")
    public ResultVO<GoodsMessage> saveMsg(@RequestBody MessageDTO dto, HttpServletRequest request){
        Long fromUserId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        GoodsMessage msg = goodsMessageService.saveMessage(dto, fromUserId);
        return ResultVO.success(msg);
    }
}