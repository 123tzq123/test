package com.trade.controller;

import com.alibaba.fastjson2.JSON;
import com.trade.config.DeepSeekConfig;
import com.trade.constant.AttributeConst;
import com.trade.vo.ResultVO;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiChatController {
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private DeepSeekConfig deepSeekConfig;

    @PostMapping("/chat")
    public ResultVO<Map<String, String>> chat(@RequestBody Map<String, String> param, HttpServletRequest request) {
        // 登录校验
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        if (userId == null) {
            return ResultVO.fail(401, "请先登录后使用AI助手");
        }
        String question = param.get("question");
        if (question == null || question.trim().isEmpty()) {
            return ResultVO.fail(400, "提问内容不能为空");
        }
        String answer = callDeepSeekApi(question);
        Map<String, String> data = new HashMap<>();
        data.put("answer", answer);
        return ResultVO.success(data);
    }

    private String callDeepSeekApi(String question) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", deepSeekConfig.getModel());

        List<Map<String, String>> messages = new ArrayList<>();
        // 系统提示词
        Map<String,String> sysMsg = new HashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content", "你是闲置优品二手交易平台AI助手，仅解答平台发布商品、订单、收藏、评价、聊天交易相关问题，回答简洁简短。");
        messages.add(sysMsg);

        // 用户提问
        Map<String,String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", question);
        messages.add(userMsg);

        body.put("messages", messages);
        body.put("temperature", 0.7);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + deepSeekConfig.getApiKey());
        HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(body), headers);

        try {
            ResponseEntity<String> resp = restTemplate.postForEntity(deepSeekConfig.getApiUrl(), entity, String.class);
            if (resp.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> resMap = JSON.parseObject(resp.getBody());
                List<Map<String, Object>> choices = (List<Map<String, Object>>) resMap.get("choices");
                Map<String, Object> msg = (Map<String, Object>) choices.get(0).get("message");
                return (String) msg.get("content");
            } else {
                return "AI服务请求失败";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "AI服务异常：" + e.getMessage();
        }
    }
}