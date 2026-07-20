package com.trade.mapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.trade.domain.GoodsMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface GoodsMessageMapper extends BaseMapper<GoodsMessage> {
    // 关联用户表查询头像
    List<GoodsMessage> selectMsgWithAvatar(@Param("ew") LambdaQueryWrapper<GoodsMessage> wrapper, IPage<GoodsMessage> page);
}