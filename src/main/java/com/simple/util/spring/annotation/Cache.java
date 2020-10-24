package com.simple.util.spring.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis cache the results returned by the current method
 *
 * @author wujing
 * @date 2020/7/30 16:36
 * @see com.simple.util.spring.aop.CacheAdvice
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Cache {


    /**
     * redis key
     *
     * @return
     */
    @AliasFor("value")
    String key() default "";
    @AliasFor("key")
    String value() default "";

    /**
     * Data expiration time switch
     *
     * @return
     */
    boolean dataExpire() default true;

    /**
     * Key expiration time when the value is empty
     *
     * @return
     */
    long emptyKeyExpireTime() default 60L;

    TimeUnit emptyKeyExpireTimeUnit() default TimeUnit.SECONDS;

    /**
     * cache data expire time
     *
     * @return
     */
    long cacheExpireTime() default 8L;

    TimeUnit cacheExpireTimeUnit() default TimeUnit.HOURS;
}
