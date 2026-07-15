package com.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trade.domain.GoodsCategory;
import com.trade.mapper.GoodsCategoryMapper;
import com.trade.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<GoodsCategoryMapper, GoodsCategory> implements CategoryService {

    @Override
    public List<GoodsCategory> list() {
        return baseMapper.selectList(null);
    }
}