package com.openelements.recordstore.server.internal;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerThreadFactory implements ThreadFactory, UncaughtExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(ServerThreadFactory.class);

    private final AtomicInteger threadCount = new AtomicInteger(1);

    @Override
    public Thread newThread(Runnable r) {
        final Thread thread = new Thread(r);
        thread.setName("data-server-worker-" + threadCount.getAndIncrement());
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(this);
        return thread;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Uncaught exception in thread " + t.getName() + ": " + e.getMessage(), e);
    }
}
