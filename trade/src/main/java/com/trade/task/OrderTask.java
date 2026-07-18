package com.trade.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trade.domain.TradeOrder;
import com.trade.service.OrderService;
import com.trade.util.RedisUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class OrderTask {
    @Resource
    private OrderService orderService;
    @Resource
    private RedisUtil redisUtil;

    //30秒执行一次
    @Scheduled(fixedRate = 30 * 1000)
    public void cancelExpireOrder() {
        System.out.println("=====定时任务执行了====");
        // 查询所有待确认订单 status=0
        LambdaQueryWrapper<TradeOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TradeOrder::getStatus, 0);
        List<TradeOrder> orderList = orderService.list(wrapper);

        long now = System.currentTimeMillis();
        //15分钟 毫秒数
        long expireTime = 1 * 60 * 1000;

        for (TradeOrder order : orderList) {
            //将createTime转为毫秒
            LocalDateTime createTime = order.getCreateTime();
            long createTimeMs = createTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            //判断是否超时：当前时间 - 创建时间 >15分钟
            if (now - createTimeMs > expireTime) {
                //修改订单状态为取消
                order.setStatus(2);
                orderService.updateById(order);
                //删除Redis里对应的key
                String key = "order:expire:" + order.getId();
                redisUtil.del(key);
                System.out.println("超时订单："+order.getId()+"，状态修改为取消");
            }
        }
    }
}