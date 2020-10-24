package com.simple.util.concurrent;

import java.util.function.Supplier;

/**
 * @author wujing
 * @version 1.0.0
 * @date 2020/10/22 21:28
 */
public final class ThreadLocalHelper {



    private ThreadLocalHelper() {
    }

    public static <T> ThreadLocal<T> empty() {

        return new ThreadLocal<>();
    }

    public static <T> ThreadLocal<T> defaultSet(Supplier<? extends T> supplier) {

        return ThreadLocal.withInitial(supplier);
    }

    public static void release(ThreadLocal<?>... threadLocal) {

        if (null != threadLocal) {
            for (ThreadLocal<?> local : threadLocal) {
                if (null != local) {

                    local.remove();
                }
            }
        }
    }
}
