package com.simple.util.spring.annotation;

import com.simple.util.spring.aop.CacheAdvice;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * enable cache
 *
 * @author wujing
 * @date 2020/8/21 14:44
 * @see Cache
 * @see CacheListener
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import(CacheAdvice.class)
public @interface EnableCache {

    /**
     * Whether to print the redis key log
     *
     * @return
     */
    @AliasFor("value")
    boolean keyLog() default true;

    @AliasFor("keyLog")
    boolean value() default true;
}
