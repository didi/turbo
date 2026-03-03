package com.didiglobal.turbo.plugin.executor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * An executor for async task execution with a configurable timeout.
 * Wraps a standard {@link ThreadPoolExecutor} without any Spring dependency.
 */
public class AsynTaskExecutor extends ThreadPoolExecutor {

    private long timeout;

    private int corePoolSize = 10;
    private int maxPoolSize = 20;
    private int queueCapacity = 100;
    private int keepAliveSeconds = 60;
    private String threadNamePrefix = "parallel-gateway-";
    private boolean virtualThreads = false;

    /**
     * Creates an executor with default settings.
     * Call {@link #initialize()} after setting properties.
     */
    public AsynTaskExecutor() {
        super(10, 20, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));
    }

    /**
     * Re-initializes the underlying pool with the configured parameters.
     * Should be called after all setters have been invoked.
     */
    public void initialize() {
        setCorePoolSize(corePoolSize);
        setMaximumPoolSize(maxPoolSize);
        setKeepAliveTime(keepAliveSeconds, TimeUnit.SECONDS);
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        super.setCorePoolSize(corePoolSize);
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        super.setMaximumPoolSize(maxPoolSize);
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
        super.setKeepAliveTime(keepAliveSeconds, TimeUnit.SECONDS);
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
        setThreadFactory(r -> {
            Thread thread = new Thread(r, threadNamePrefix + "-thread");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void setVirtualThreads(boolean virtualThreads) {
        this.virtualThreads = virtualThreads;
    }
}
