package com.simple.util.spring.aop;

import org.aspectj.lang.annotation.Pointcut;

/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/10/23 15:13
 */
public interface PointcutModel {

    /**
     * class pointcut
     */
    @Pointcut("@within(com.simple.util.spring.annotation.Log)")
    static void logClass() {

    }

    /**
     * method pointcut
     */
    @Pointcut("@annotation(com.simple.util.spring.annotation.Log)")
    static void logMethod() {

    }

    /**
     * class pointcut
     */
    @Pointcut("@within(com.simple.util.spring.annotation.CacheListener)")
    static void cacheListenerClass() {

    }

    /**
     * method pointcut
     */
    @Pointcut("@annotation(com.simple.util.spring.annotation.Locked)")
    static void enableLockMethod() {

    }
}
