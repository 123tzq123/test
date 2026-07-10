package com.trade.controller;

import com.trade.domain.GoodsCategory;
import com.trade.service.CategoryService;
import com.trade.vo.ResultVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;
    @GetMapping("/list")
    public ResultVO<List<GoodsCategory>> getList(){
        List<GoodsCategory> list = categoryService.list();
        return ResultVO.success(list);
    }
}