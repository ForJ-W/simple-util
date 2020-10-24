package com.simple.util.spring.annotation;

import com.simple.util.lock.LockModel;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/10/23 15:03
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Locked {

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
     * lock model
     *
     * @return
     */
    LockModel model() default LockModel.SIMPLE;

    /**
     * lock release time
     *
     * @return
     */
    long releaseTime() default 30;
}
