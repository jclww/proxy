package com.lww.proxy.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    private AtomicInteger num =  new AtomicInteger(1);

    private boolean isDamon;

    public NamedThreadFactory(boolean isDamon) {
        this.isDamon = isDamon;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName(Config.THREAD_NAMEPREFIX + "-" + num.getAndIncrement());
        thread.setDaemon(isDamon);
        return thread;
    }
}
