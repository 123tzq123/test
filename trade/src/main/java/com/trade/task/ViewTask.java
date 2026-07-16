package com.trade.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.trade.domain.IdleGoods;
import com.trade.mapper.IdleGoodsMapper;
import com.trade.util.RedisUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Map;

@Component
public class ViewTask {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private IdleGoodsMapper goodsMapper;

    private static final String GOODS_VIEW_HASH = "goods:view:hash";

    // 测试期间10秒执行一次，上线改为 1000 * 60 * 30（30分钟）
    @Scheduled(fixedRate = 10000)
    public void flushViewCount() {
        Map<String, Object> entries = redisUtil.getHashEntries(GOODS_VIEW_HASH);
        if (entries.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            String field = entry.getKey();
            Long goodsId = Long.parseLong(field);
            Long addView = Long.parseLong(entry.getValue().toString());
            if (addView <= 0) {
                continue;
            }
            // 1、MySQL增量累加浏览量
            LambdaUpdateWrapper<IdleGoods> wrapper = new LambdaUpdateWrapper<>();
            wrapper.setSql("view_count = view_count + " + addView)
                    .eq(IdleGoods::getId, goodsId);
            goodsMapper.update(null, wrapper);

            // 2、Redis减去已经同步的数量，定时任务执行期间新增的浏览量会保留在Redis中，不会丢失
            Long remainCount = redisUtil.incrHash(GOODS_VIEW_HASH, field, -addView);
            // 剩余值<=0则删除hash的field，不为0就继续留在Redis等待下一轮定时任务
            if (remainCount <= 0) {
                redisUtil.deleteHashField(GOODS_VIEW_HASH, field);
            }
        }
    }
}