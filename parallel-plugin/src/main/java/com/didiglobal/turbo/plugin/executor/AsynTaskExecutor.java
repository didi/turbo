package com.didiglobal.turbo.plugin.executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class AsynTaskExecutor extends ThreadPoolTaskExecutor {
    private long timeout;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}