package com.trade.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    //数值自增
    public void incr(String key,int num){
        redisTemplate.opsForValue().increment(key,num);
    }
    //设置过期时间
    public void setExpire(String key, long time, TimeUnit timeUnit){
        redisTemplate.expire(key,time,timeUnit);
    }
    //根据key获取值
    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    // ============新增方法============
    // 获取匹配通配符的所有key
    public Set<String> getKeys(String pattern){
        return redisTemplate.keys(pattern);
    }
    //提供redisTemplate给外部调用
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }
}