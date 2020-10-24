package com.simple.util.lock;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Collections;

/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/10/23 9:07
 */
 public class ReentrantRedisLock extends RedisLock {

    private static final DefaultRedisScript<Long> LOCK_SCRIPT;
    private static final DefaultRedisScript<Object> UNLOCK_SCRIPT;

    static {
        /**
         * 加载锁脚本
         */
        LOCK_SCRIPT = new DefaultRedisScript<>();
        LOCK_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource("lock.lua")));
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource("unlock.lua")));
    }

    public  ReentrantRedisLock(StringRedisTemplate redisTemplate, String key) {

        super(redisTemplate, key);
    }

    @Override
    public boolean lock() {
        return lock(PERMANENT);
    }

    @Override
    public boolean lock(long releaseTime) {
        // 记录释放锁的时间
        this.releaseTime = String.valueOf(releaseTime);
        // 执行脚本
        Long result = redisTemplate.execute(
                LOCK_SCRIPT,
                Collections.singletonList(key),
                ID_PREFIX + Thread.currentThread().getId(),
                this.releaseTime
        );
        // 判断结果并返回
        return result != null && result.intValue() == 1;
    }

    @Override
    public void unlock() {
        // 执行脚本
        redisTemplate.execute(
                UNLOCK_SCRIPT,
                Collections.singletonList(key),
                ID_PREFIX + Thread.currentThread().getId(),
                this.releaseTime
        );
    }
}
