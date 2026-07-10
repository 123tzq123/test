package com.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trade.domain.GoodsCategory;
import com.trade.mapper.GoodsCategoryMapper;
import com.trade.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<GoodsCategoryMapper, GoodsCategory> implements CategoryService {
}