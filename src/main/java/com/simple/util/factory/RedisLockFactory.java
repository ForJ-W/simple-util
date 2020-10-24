package com.simple.util.factory;

import com.simple.util.lock.LockModel;
import com.simple.util.lock.RedisLock;
import com.simple.util.lock.ReentrantRedisLock;
import com.simple.util.lock.SimpleRedisLock;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/10/23 15:48
 */
public final class RedisLockFactory {

    private RedisLockFactory() {
    }

    public static RedisLock getInstance(LockModel lockModel,
                                        StringRedisTemplate redisTemplate,
                                        String key) {

        if (lockModel.equals(LockModel.REENTRANT)) {
            return new ReentrantRedisLock(redisTemplate, key);
        }
        return new SimpleRedisLock(redisTemplate, key);
    }
}
