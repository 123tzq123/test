package com.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.trade.domain.GoodsCategory;

import java.util.List;

public interface CategoryService extends IService<GoodsCategory> {

    List<GoodsCategory> list();

}