package com.simple.util.spring.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Monitor all CRUD operations under the current class
 *
 * @author wujing
 * @date 2020/7/30 16:36
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CacheListener {

    /**
     * listen to redis key
     *
     * @return
     */
    @AliasFor("value")
    String[] key() default "";
    @AliasFor("key")
    String[] value() default "";

    /**
     * Whether to print the redis key log
     *
     * @return
     */
    boolean keyLog() default true;
}
