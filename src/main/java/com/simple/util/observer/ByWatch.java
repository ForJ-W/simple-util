package com.simple.util.observer;

/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/10/23 16:57
 */
public interface ByWatch {

    /**
     * addWatch
     *
     * @param watch
     */
    void addWatch(Watch watch);

    /**
     * removeWatch
     *
     * @param watch
     */
    void removeWatch(Watch watch);
}
