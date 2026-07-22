package com.trade.controller;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trade.config.DashScopeConfig;
import com.trade.config.DeepSeekConfig;
import com.trade.constant.AttributeConst;
import com.trade.domain.GoodsCategory;
import com.trade.dto.GoodsPublishDTO;
import com.trade.domain.IdleGoods;
import com.trade.dto.GoodsQueryDTO;
import com.trade.dto.GoodsUpdateDTO;
import com.trade.exception.GlobalException;
import com.trade.service.CategoryService;
import com.trade.service.GoodsService;
import com.trade.vo.AiGoodsDetectVO;
import com.trade.vo.GoodsVO;
import com.trade.vo.ResultVO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Resource
    private GoodsService goodsService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private DeepSeekConfig deepSeekConfig;

    @Resource
    private DashScopeConfig dashScopeConfig;


    //发布闲置商品
    @PostMapping("/publish")
    public ResultVO<Void> publish(@Validated @RequestBody GoodsPublishDTO dto, HttpServletRequest request){
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        if(userId == null){
            throw new GlobalException(401,"登录失效，请重新登录");
        }
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
    public ResultVO<GoodsVO> getGoodsDetail(@PathVariable Long goodsId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        GoodsVO goodsVO = goodsService.getDetail(goodsId, userId);
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

    //商品上架
    @PutMapping("/onSale/{goodsId}")
    public ResultVO<Void> onSale(@PathVariable Long goodsId, HttpServletRequest request){
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        goodsService.onSale(goodsId,userId);
        return ResultVO.success(null);
    }

    //商品删除（物理删除）
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

    @PostMapping("/list")
    public ResultVO<Page<IdleGoods>> getGoodsList(@RequestBody GoodsQueryDTO queryDTO){
        //设置默认分页参数
        if(queryDTO.getPageNum() == null) queryDTO.setPageNum(1);
        if(queryDTO.getPageSize() == null) queryDTO.setPageSize(8);
        Page<IdleGoods> page = goodsService.findGoodsByCondition(queryDTO);
        return ResultVO.success(page);
    }

    //获取全部商品分类，给前端下拉框使用
    @GetMapping("/category/list")
    public ResultVO<List<GoodsCategory>> getAllCategory(){
        List<GoodsCategory> list = categoryService.list();
        return ResultVO.success(list);
    }

    @PostMapping("/ai/detect")
    public ResultVO<AiGoodsDetectVO> aiDetectGoods(
            @RequestBody Map<String, List<String>> param,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        if(userId == null){
            return ResultVO.fail(401,"请登录");
        }
        List<String> imgUrls = param.get("imgUrlList");
        if(imgUrls == null || imgUrls.isEmpty()){
            return ResultVO.fail(400,"请上传图片");
        }
        try {
            AiGoodsDetectVO detectResult = callQwenVL(imgUrls);
            return ResultVO.success(detectResult);
        }catch (Exception e){
            e.printStackTrace();
            return ResultVO.fail(500,"AI图像识别失败，请更换图片重试");
        }
    }
    private AiGoodsDetectVO callQwenVL(List<String> imgUrls) {
        Map<String, Object> body = new HashMap<>();
       // body.put("model", "deepseek-v4-flash"); // 改为 qwen-vl-max
        body.put("model", "qwen-vl-max");

        List<Map<String, Object>> messages = new ArrayList<>();

        String systemPrompt = "你是闲置二手商品图片识别助手。\n" +
                "重点：先仔细观察图片内容，严格依据图片实物进行识别，禁止凭空猜测，价格参考二手市场行情，不要按照全新商品估价！\n" +
                "可选分类编号：\n" +
                "1 电子产品\n" +
                "2 图书教材\n" +
                "3 衣物鞋帽\n" +
                "4 生活用品\n" +
                "5 运动器材\n" +
                "\n" +
                "输出硬性规则：\n" +
                "1. title：简短精准商品名称，图片是图书就写书籍名称；\n" +
                "2. categoryId：严格匹配上面分类数字；\n" +
                "3. price：合理二手市场价格；\n" +
                "4. content：描述商品外观、成色；\n" +
                "5. 只返回纯JSON，禁止任何额外文字、markdown注释。\n" +
                "\n" +
                "返回示例：\n" +
                "{\"title\":\"无线蓝牙耳机\",\"categoryId\":1,\"price\":35,\"content\":\"无线蓝牙耳机，外观完好，少量使用痕迹。\"}";

        messages.add(Map.of("role", "system", "content", systemPrompt));

        List<Object> userContent = new ArrayList<>();
        userContent.add(Map.of("type", "text", "text", "识别图片内的二手商品，输出纯JSON"));
        for (String url : imgUrls) {
            Map<String, Object> imgItem = new HashMap<>();
            imgItem.put("type", "image_url");
            imgItem.put("image_url", Map.of("url", url));
            userContent.add(imgItem);
        }
        messages.add(Map.of("role", "user", "content", userContent));

        body.put("messages", messages);
        body.put("temperature", 0.3);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + dashScopeConfig.getApiKey());
        HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(body), headers);

        String requestBodyJson = JSON.toJSONString(body);
        System.out.println("【发给通义千问请求体】" + requestBodyJson);

        ResponseEntity<String> resp = restTemplate.postForEntity(dashScopeConfig.getApiUrl(), entity, String.class);
        System.out.println("【通义千问原始返回】" + resp.getBody());

        if (resp.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> result = JSON.parseObject(resp.getBody());
            // 兼容模式 没有 output！直接拿choices
            List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");
            Map<String, Object> msg = (Map<String, Object>) choices.get(0).get("message");
            String jsonText = (String) msg.get("content");
            jsonText = jsonText.replaceAll("```json", "").replaceAll("```", "").trim();
            jsonText = jsonText.replaceAll("\\s+", " ").trim();
            System.out.println("【待解析JSON】" + jsonText);
            return JSON.parseObject(jsonText, AiGoodsDetectVO.class);
        }
        throw new RuntimeException("AI识别请求失败");
    }



}