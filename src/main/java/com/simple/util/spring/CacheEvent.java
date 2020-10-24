package com.simple.util.spring;

/**
 * Custom cache event
 *
 * @author wujing
 * @date 2020/8/21 10:27
 * @see com.simple.util.spring.aop.CacheAdvice
 */
public interface CacheEvent {

    String KEY_HIDDEN_LOG = "key is hidden,If you need to view, please enable 'keyLog'";

    /**
     * flush cache flag
     */
    void flush();
}
