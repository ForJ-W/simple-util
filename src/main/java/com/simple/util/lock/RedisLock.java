package com.simple.util.lock;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/10/23 9:07
 */
public abstract class RedisLock implements Lock {

    private static final DefaultRedisScript<Object> RENEWAL_SCRIPT;
    protected static final String ID_PREFIX = UUID.randomUUID().toString();
    protected static final int PERMANENT = -1;
    protected final StringRedisTemplate redisTemplate;
    protected final String key;
    protected String releaseTime;

    static {
        RENEWAL_SCRIPT = new DefaultRedisScript<>();
        RENEWAL_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource("renewal.lua")));
    }

    protected RedisLock(StringRedisTemplate redisTemplate, String key) {
        this.redisTemplate = redisTemplate;
        this.key = key;
    }

    /**
     * 获取锁
     *
     * @param releaseTime
     * @return
     */
    public abstract boolean lock(long releaseTime);

    /**
     * 续约
     *
     * @param async
     */
    public <F> void renewal(Future<F> async) {
        // 默认续约的间隔为释放锁的时间 /2
        long time = Long.parseLong(releaseTime) >> 1;
        F f = null;
        try {
            TimeUnit.SECONDS.sleep(time);
            // 获取业务运行状态
            f = async.get(1, TimeUnit.MILLISECONDS);
        } catch (Exception ignored) {
        }
        // null 续约
        if (Objects.isNull(f)) {
            redisTemplate.execute(RENEWAL_SCRIPT,
                    Collections.singletonList(key),
                    this.releaseTime);
            renewal(async);
        }
    }
}
