package com.simple.util.spring.aop;

import com.simple.util.asserts.AopAssert;
import com.simple.util.factory.ExecutorServiceFactory;
import com.simple.util.factory.RedisLockFactory;
import com.simple.util.lock.RedisLock;
import com.simple.util.spring.SpringAopUtil;
import com.simple.util.spring.annotation.Locked;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/10/23 15:11
 */
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE >> 1)
public class LockAdvice implements RedisKeyProcess, DisposableBean {

    private static final String POOL_PREFIX = "lock-advice-pool-";
    private static final ExecutorService EXECUTOR = ExecutorServiceFactory.create(POOL_PREFIX, 2, 4);
    private static final String LOG_PREFIX = "@Lock";
    private static final Logger LOG = LoggerFactory.getLogger(LOG_PREFIX);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Around("PointcutModel.enableLockMethod()")
    private Object redisLock(ProceedingJoinPoint pjp) {

        AopAssert.isNull(redisTemplate, "[redisLock] redisTemplate is null!");
        Object pjpTarget = pjp.getTarget();
        Class<?> targetClass = pjpTarget.getClass();
        Object[] args = pjp.getArgs();
        RedisLock lock = null;
        CompletableFuture<RunState> async = null;
        Object r = null;
        try {
            // 获取继承体系中顶层类(非Object) 为了不获取到代理类
            targetClass = SpringAopUtil.acquireNotProxyClass(targetClass);
            // 获取此类的当前方法
            Method currentMethod = SpringAopUtil.acquireCurrentMethod(pjp, targetClass);
            // 获取注解信息
            Locked locked = currentMethod.getAnnotation(Locked.class);
            // key的默认处理
            String key = keyProcess(targetClass.getTypeName(), args, locked.value(), currentMethod);
            // 获取lock实例
            lock = RedisLockFactory.getInstance(locked.model(), redisTemplate, key);
            // 获取锁的释放时间 秒
            long releaseTime = locked.releaseTime();
            // 上锁
            if (lock.lock(releaseTime)) {
                // 异步执行业务并封装运行状态
                async = CompletableFuture.supplyAsync(() -> {
                    Object result = null;
                    try {
                        result = pjp.proceed(args);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    return RunState.getInstance(result, 1);
                }, EXECUTOR);
                // 续约
                lock.renewal(async);
            }
            // 如果CompletableFuture为null说明没有获取到锁,返回null
            // 获取到锁返回运行状态中的结果
            try {
                if (Objects.nonNull(async)) {

                    r = async.get().getResult();
                }
            } catch (Exception e) {
                LOG.error("[redisLock] async.get() fail!");
                e.printStackTrace();
            }
        } catch (Throwable throwable) {
            LOG.error("[redisLock] aop lock fail!");
            throwable.printStackTrace();
        } finally {
            // 释放锁
            if (Objects.nonNull(lock)) {

                lock.unlock();
            }
        }
        return r;
    }

    @Override
    public void destroy() throws Exception {

        EXECUTOR.shutdown();
    }
}
