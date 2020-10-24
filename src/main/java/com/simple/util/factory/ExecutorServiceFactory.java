package com.simple.util.factory;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/7/30 9:27
 */
public final class ExecutorServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorServiceFactory.class);


    private ExecutorServiceFactory() {
    }

    private static final int DEFAULT_CORE_POOL_SIZE = 16;
    private static final int DEFAULT_MAX_POOL_SIZE = DEFAULT_CORE_POOL_SIZE << 1;

    public static ExecutorService create(String poolName) {
        return create(poolName, DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE);
    }

    public static ExecutorService create(String poolName, int corePoolSize, int maxPoolSize) {

        ExecutorService executorService = new ThreadPoolExecutor(corePoolSize,
                maxPoolSize, 30,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(maxPoolSize),
                new CustomizableThreadFactory(poolName), new ThreadPoolExecutor.CallerRunsPolicy()) {

            @Override
            public void shutdown() {
                super.shutdown();
                LOGGER.info("{} executor is shutdown!", poolName);
            }
        };
        LOGGER.info("{} executor is create!", poolName);
        return executorService;
    }
}
