package com.simple.util.lock;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/10/23 9:07
 */
public class SimpleRedisLock extends RedisLock {

    public SimpleRedisLock(StringRedisTemplate redisTemplate, String key) {
        super(redisTemplate, key);
    }

    @Override
    public boolean lock() {
        return lock(PERMANENT);
    }

    @Override
    public boolean lock(long releaseTime) {
        // 拼接当前线程id与uuid作为redis value
        this.releaseTime = String.valueOf(releaseTime);
        String value = ID_PREFIX + Thread.currentThread().getId();
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, value, releaseTime, TimeUnit.SECONDS);
        return flag != null && flag;
    }

    @Override
    public void unlock() {
        // 释放时判断当前的锁是否是自己的
        String value = ID_PREFIX + Thread.currentThread().getId();
        // 取出当前的锁的value
        String val = redisTemplate.opsForValue().get(key);
        // 判断是否一致
        if (value.equals(val)) {
            // 删除key
            redisTemplate.delete(key);
        }
    }
}
