package com.trade.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.trade.domain.IdleGoods;
import com.trade.mapper.IdleGoodsMapper;
import com.trade.util.RedisUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Set;

//@Component
public class ViewTask {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private IdleGoodsMapper goodsMapper;

    //每隔30分钟把Redis浏览量刷回数据库
    @Scheduled(fixedRate = 1000*60*30)
    public void flushViewCount(){
        //调用新增的getKeys方法获取所有前缀匹配的key
        Set<String> keys = redisUtil.getKeys("goods:view:*");
        if(keys == null || keys.isEmpty()){
            return;
        }
        for(String key : keys){
            String idStr = key.split(":")[2];
            Long goodsId = Long.parseLong(idStr);
            Integer view = Integer.parseInt(redisUtil.get(key).toString());
            LambdaUpdateWrapper<IdleGoods> wrapper = new LambdaUpdateWrapper<>();
            wrapper.setSql("view_count = view_count + "+view)
                    .eq(IdleGoods::getId,goodsId);
            goodsMapper.update(null,wrapper);
            //清空redis里这条key
            redisUtil.getRedisTemplate().delete(key);
        }
    }
}