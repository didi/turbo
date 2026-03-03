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
        super.setCorePoolSize(corePoolSize);
        super.setMaximumPoolSize(maxPoolSize);
        super.setKeepAliveTime(keepAliveSeconds, TimeUnit.SECONDS);
        if (virtualThreads) {
            try {
                // Use virtual thread factory when available (Java 21+)
                java.util.concurrent.ThreadFactory vtFactory =
                        (java.util.concurrent.ThreadFactory) Thread.class
                                .getMethod("ofVirtual")
                                .invoke(null);
                // ofVirtual() returns a Thread.Builder.OfVirtual; call .factory() on it
                java.util.concurrent.ThreadFactory factory =
                        (java.util.concurrent.ThreadFactory) vtFactory.getClass()
                                .getMethod("factory")
                                .invoke(vtFactory);
                setThreadFactory(factory);
            } catch (Exception ignored) {
                // Virtual threads not available; fall back to platform threads
            }
        }
        setThreadFactory(r -> {
            Thread thread = new Thread(r, threadNamePrefix + r.hashCode());
            thread.setDaemon(true);
            return thread;
        });
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

    /**
     * Sets the queue capacity. Note: for an already-constructed executor the queue size is
     * fixed. Changing this value only affects the initial setup via {@link #initialize()}.
     * To apply a different queue capacity, construct a new {@code AsynTaskExecutor}.
     */
    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
        super.setKeepAliveTime(keepAliveSeconds, TimeUnit.SECONDS);
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    /**
     * Enables virtual thread mode (Java 21+). When set to {@code true}, virtual threads
     * will be used if supported by the current JVM; otherwise platform threads are used.
     */
    public void setVirtualThreads(boolean virtualThreads) {
        this.virtualThreads = virtualThreads;
    }
}
