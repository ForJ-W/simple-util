package com.simple.util.lock;

/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/10/23 9:07
 */
public interface Lock {

    /**
     * 获取锁
     *
     * @return
     */
    boolean lock();

    /**
     * 释放锁
     */
    void unlock();
}