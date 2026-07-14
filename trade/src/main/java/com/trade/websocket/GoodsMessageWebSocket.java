package com.trade.websocket;

import com.alibaba.fastjson2.JSON;
import com.trade.domain.GoodsMessage;
import com.trade.dto.MessageDTO;
import com.trade.service.GoodsMessageService;
import com.trade.util.JwtUtil;
import com.trade.util.SpringContextUtil;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/ws/chat")
public class GoodsMessageWebSocket {
    // key:userId, value:会话session
    private static final Map<Long, Session> onlineUser = new ConcurrentHashMap<>();

    // 从Spring上下文获取Bean
    private GoodsMessageService getGoodsMessageService() {
        return SpringContextUtil.getBean(GoodsMessageService.class);
    }

    //建立连接 ws://localhost:8080/ws/chat?token=xxx
    @OnOpen
    public void onOpen(Session session) {
        Map<String, List<String>> paramMap = session.getRequestParameterMap();
        List<String> tokenList = paramMap.get("token");
        String token = null;
        if(tokenList != null && !tokenList.isEmpty()){
            token = tokenList.get(0);
        }
        try {
            if(token == null || token.trim().isEmpty()){
                session.close();
                return;
            }
            Long userId = JwtUtil.parseToken(token);
            onlineUser.put(userId, session);
            //连接建立成功后，给前端发送初始化消息，传递当前登录用户id
            String initMsg = "{\"type\":\"init\",\"userId\":"+userId+"}";
            session.getBasicRemote().sendText(initMsg);
        }catch (Exception e) {
            try {
                session.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //收到前端发送的消息
    @OnMessage
    public void onMessage(String msgJson, Session session) {
        MessageDTO dto = JSON.parseObject(msgJson, MessageDTO.class);
        Long fromUserId = null;
        for (Map.Entry<Long, Session> entry : onlineUser.entrySet()) {
            if (entry.getValue() == session) {
                fromUserId = entry.getKey();
                break;
            }
        }
        GoodsMessage message = getGoodsMessageService().saveMessage(dto, fromUserId);
        //手动构造Map，把LocalDateTime转为字符串，避开fastjson2序列化LocalDateTime的坑
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("id", message.getId());
        resultMap.put("goodsId", message.getGoodsId());
        resultMap.put("fromUserId", message.getFromUserId());
        resultMap.put("toUserId", message.getToUserId());
        resultMap.put("content", message.getContent());
        resultMap.put("createTime", message.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        String jsonStr = JSON.toJSONString(resultMap);

        //推送给接收方
        Session toSession = onlineUser.get(dto.getToUserId());
        if (toSession != null && toSession.isOpen()) {
            try {
                toSession.getBasicRemote().sendText(jsonStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //发送者自己收到消息
        Session selfSession = onlineUser.get(fromUserId);
        if(selfSession != null && selfSession.isOpen()){
            try {
                selfSession.getBasicRemote().sendText(jsonStr);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //断开连接
    @OnClose
    public void onClose(Session session) {
        onlineUser.entrySet().removeIf(entry -> entry.getValue() == session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        onlineUser.entrySet().removeIf(entry -> entry.getValue() == session);
        throwable.printStackTrace();
    }
}