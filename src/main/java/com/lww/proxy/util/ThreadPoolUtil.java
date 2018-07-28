package com.lww.proxy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPoolUtil {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolUtil.class);

    private static AtomicBoolean initFlag = new AtomicBoolean(false);
    private static ThreadPoolExecutor threadPool;


    private static void init(int corePoolSize, int maxPoolSize) {
        if (!initFlag.compareAndSet(false, true)) {
            return;
        }
        threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
                10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1000), new NamedThreadFactory(false),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public static synchronized ThreadPoolExecutor getInstance(int corePoolSize, int maxPoolSize) {
        init(corePoolSize, maxPoolSize);
        return threadPool;
    }

    public static synchronized ThreadPoolExecutor getDefaultInstance() {
        init(Config.DEFAULT_COREPOOLSIZE, Config.DEFAULT_MAXPOOLSIZE);
        return threadPool;
    }


}
