package com.trade.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.trade.domain.IdleGoods;
import com.trade.mapper.IdleGoodsMapper;
import com.trade.util.RedisUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

@Component
public class ViewTask {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private IdleGoodsMapper goodsMapper;

    //每30分钟执行一次，测试时改为10000（10秒），上线换回 1000*60*30
    @Scheduled(fixedRate = 1000 * 60 * 30)
    public void flushViewCount() {
        String hashKey = "goods:view:hash";
        Map<String, Object> entries = redisUtil.getHashEntries(hashKey);
        if (entries.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            Long goodsId = Long.parseLong(entry.getKey());
            Long viewCount = Long.parseLong(entry.getValue().toString());
            //数据库字段累加
            LambdaUpdateWrapper<IdleGoods> wrapper = new LambdaUpdateWrapper<>();
            wrapper.setSql("view_count = view_count + " + viewCount)
                    .eq(IdleGoods::getId, goodsId);
            goodsMapper.update(null, wrapper);
            //同步完成之后删除hash里面对应的field
            redisUtil.deleteHashField(hashKey, entry.getKey());
        }
    }
}