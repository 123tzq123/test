package com.trade.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //浏览量自增，采用Hash结构
    //原来：public Long incrHash(String hashKey, String field, int num)
    public Long incrHash(String hashKey, String field, long num) {
        return stringRedisTemplate.opsForHash().increment(hashKey, field, num);
    }

    //获取Hash中field对应值
    public Long getHashValue(String hashKey, String field) {
        Object val = stringRedisTemplate.opsForHash().get(hashKey, field);
        if(val == null){
            return 0L;
        }
        return Long.parseLong(val.toString());
    }

    //获取hash里面所有field‑value
    public Map<String, Object> getHashEntries(String hashKey) {
        Map<Object, Object> objectMap = stringRedisTemplate.opsForHash().entries(hashKey);
        Map<String, Object> resultMap = new HashMap<>();
        for(Map.Entry<Object,Object> entry : objectMap.entrySet()){
            resultMap.put(entry.getKey().toString(), entry.getValue());
        }
        return resultMap;
    }

    //删除hash里面指定field
    public void deleteHashField(String hashKey, String field) {
        stringRedisTemplate.opsForHash().delete(hashKey, field);
    }

    //原来的方法保留，供其它业务使用
    public Long incr(String key, int num) {
        return stringRedisTemplate.opsForValue().increment(key, num);
    }
    public void setExpire(String key, long time, TimeUnit timeUnit) {
        redisTemplate.expire(key, time, timeUnit);
    }
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    public Set<String> getKeys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }
    //存入数据并设置过期时间
    public void setEx(String key, Object value, long time, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, time, unit);
    }

    //普通存入数据
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    //删除key
    public void del(String key) {
        redisTemplate.delete(key);
    }
    // 判断key是否存在
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // 获取String‑Key对应的整数，不存在返回0
    public Integer getInt(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        if(obj == null) {
            return 0;
        }
        return Integer.parseInt(obj.toString());
    }
}