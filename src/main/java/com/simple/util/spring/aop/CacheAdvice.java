package com.simple.util.spring.aop;

import com.simple.util.enums.ProcessEnum;
import com.simple.util.exception.ExceptionUtil;
import com.simple.util.spring.CacheEvent;
import com.simple.util.spring.SpringAopUtil;
import com.simple.util.spring.annotation.Cache;
import com.simple.util.spring.annotation.CacheListener;
import com.simple.util.spring.annotation.EnableCache;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import static com.simple.util.StringUtil.merge;
import static com.simple.util.constant.StringConstant.ASTERISK;
import static com.simple.util.enums.ProcessEnum.*;

/**
 * Use custom annotations to implement redis caching through spring aop
 *
 * @author wujing
 * @date 2020/7/30 13:46
 */
@Component
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE >> 2)
@SuppressWarnings("unchecked")
public final class CacheAdvice implements CacheEvent, RedisKeyProcess {

    private CacheAdvice() {
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String LOG_PREFIX = "@Cache";
    private static final Logger LOG = LoggerFactory.getLogger(LOG_PREFIX);
    private static final String CHANGE_FLAG_PRE = "changeFlag:";

    @PostConstruct
    private void init() {

        flush();
    }

    @Override
    public void flush() {

        redisTemplate.delete(redisTemplate.keys(merge(CHANGE_FLAG_PRE, ASTERISK)));
    }

    /**
     * cache around advice
     *
     * @param pjp
     * @return
     */
    @Around("PointcutModel.cacheListenerClass()")
    private Object cacheListener(ProceedingJoinPoint pjp) {
        Object pjpTarget = pjp.getTarget();
        Object[] args = pjp.getArgs();
        String[] keyArr = null;
        Object result = null;
        try {
            // get @CacheListener for class
            // If @CacheListener is not on the current class or Implemented interface, return directly
            Class<?> targetClass = pjpTarget.getClass();
            if (!targetClass.isAnnotationPresent(CacheListener.class) && 0 == targetClass.getInterfaces().length) {

                return pjp.proceed(args);
            }

            targetClass = SpringAopUtil.acquireClassForAnnotation(CacheListener.class, targetClass);
            CacheListener cacheListener = targetClass.getAnnotation(CacheListener.class);
            // keyLogProcess
            boolean keyLog = keyLogProcess(cacheListener);
            // get current method
            Method currentMethod = SpringAopUtil.acquireCurrentMethod(pjp, targetClass);
            // If @Cache is on the current method
            ProcessEnum processEnum = SpringAopUtil.acquireMethodProcessType(currentMethod.getName());
            if (currentMethod.isAnnotationPresent(Cache.class)) {
                return cache(keyLog, targetClass.getTypeName(), currentMethod.getAnnotation(Cache.class),
                        args, currentMethod, pjp);
            }
            // execute first
            result = pjp.proceed(args);
            // Get the listening key
            keyArr = listenerKeyHandler(cacheListener, targetClass);
            for (String key : keyArr) {
                // if current method is R(read)
                if (R.equals(processEnum)) {
                    continue;
                }

                Set<String> keySet = redisTemplate.keys(merge(key, ASTERISK));
                if (CollectionUtils.isEmpty(keySet)) {
                    continue;
                }
                for (String realKey : keySet) {
                    if (C.equals(processEnum) || U.equals(processEnum)) {

                        redisTemplate.opsForValue().set(merge(CHANGE_FLAG_PRE, realKey), true);
                        LOG.info("[cacheListener] update ok!");
                        keyLogPrint(keyLog, realKey);
                    } else if (D.equals(processEnum)) {

                        redisTemplate.delete(key);
                        LOG.info("[cacheListener] delete ok!");
                        keyLogPrint(keyLog, realKey);
                    }

                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            LOG.error("[cacheListener] fail! keys: {}", Arrays.toString(keyArr));
            ExceptionUtil.throwAopException("cache fail! ");
        }
        return result;
    }

    /**
     * key log process
     *
     * @param cacheListener
     * @return
     */
    private boolean keyLogProcess(CacheListener cacheListener) {
        // Whenever one is closed, close
        Object starterBean = SpringAopUtil.acquireStarterBeanForAnnotation(EnableCache.class);
        return (Objects.isNull(starterBean) || (starterBean.getClass().getAnnotation(EnableCache.class).keyLog()))
                &&
                cacheListener.keyLog();
    }

    /**
     * redis key log print
     *
     * @param keyLog
     * @param key
     * @return
     */
    private void keyLogPrint(boolean keyLog, String key) {

        if (keyLog) {

            LOG.info("key-> {}", key);
        } else {

            LOG.warn("key-> {}", KEY_HIDDEN_LOG);
        }
    }


    /**
     * @param cacheListener
     * @param targetClass
     * @return
     */
    private String[] listenerKeyHandler(CacheListener cacheListener, Class<?> targetClass) {

        String[] keyArr = cacheListener.value();
        String[] methodNameArr = SpringAopUtil.acquireMethodNameArr(targetClass);
        String[] keyPreArr = Arrays.stream(methodNameArr)
                .filter(methodName -> R.equals(SpringAopUtil.acquireMethodProcessType(methodName)))
                .map(methodName -> merge(targetClass.getTypeName(), ":", methodName, ":"))
                .toArray(String[]::new);
        return StringUtils.isBlank(keyArr[0]) ? keyPreArr : keyArr;
    }

    /**
     * cache data
     *
     * @param keyLog
     * @param className
     * @param cache
     * @param args
     * @param currentMethod
     * @param pjp
     * @return
     * @throws Throwable
     */
    private Object cache(boolean keyLog, String className, Cache cache, Object[] args,
                         Method currentMethod, ProceedingJoinPoint pjp) throws Throwable {

        Object result;
        // get key
        String key = keyProcess(className, args, cache.key(), currentMethod);
        // is cache
        boolean cacheFlag = cacheFlag(key);
        if (cacheFlag) {
            // get cache
            result = redisTemplate.opsForValue().get(key);
            if (Objects.isNull(result)) {
                LOG.warn("[cache] result is null!");
                keyLogPrint(keyLog, key);
            }
            LOG.info("[cache] get to cache!");
        } else {
            // execute
            if (StringUtils.isBlank(cache.key())) {
                LOG.warn("[cache] key is empty! use current method name!");
            }
            result = pjp.proceed(args);
            if (Objects.nonNull(result)) {
                // if set expire key
                if (cache.dataExpire()) {
                    redisTemplate.opsForValue().set(key, result, cache.cacheExpireTime(), cache.cacheExpireTimeUnit());
                } else {
                    redisTemplate.opsForValue().set(key, result);
                }
            } else {
                // set empty key and expire key
                redisTemplate.opsForValue().set(key, null, cache.emptyKeyExpireTime(), cache.emptyKeyExpireTimeUnit());
            }
            // set change flag
            redisTemplate.opsForValue().set(merge(CHANGE_FLAG_PRE, key), false);
            LOG.info("[cache] ok!");
        }
        keyLogPrint(keyLog, key);
        return result;
    }

    /**
     * Decide whether the data is fetched from the cache or from the original method logic
     * the key of the data exists
     * the key of the data change exists
     * the data has not been changed
     *
     * @param key
     * @return
     */
    private boolean cacheFlag(String key) {

        Boolean hasKey = redisTemplate.hasKey(key);
        Boolean hasChangeKey = redisTemplate.hasKey(merge(CHANGE_FLAG_PRE, key));
        return (Objects.nonNull(hasKey) && hasKey)
                && (Objects.nonNull(hasChangeKey) && hasChangeKey)
                && !Boolean.parseBoolean(String.valueOf(redisTemplate.opsForValue().get(merge(CHANGE_FLAG_PRE, key))));
    }
}