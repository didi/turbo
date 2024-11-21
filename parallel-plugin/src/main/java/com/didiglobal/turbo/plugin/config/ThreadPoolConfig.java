package com.didiglobal.turbo.plugin.config;

import com.didiglobal.turbo.engine.util.PluginPropertiesUtil;
import com.didiglobal.turbo.plugin.executor.AsynTaskExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadPoolConfig {

    private static final String TIMEOUT_CONFIG = "turbo.plugin.parallelGateway.threadPool.timeout";

    @Bean
    public AsynTaskExecutor executorService(){
        String timeout = PluginPropertiesUtil.getPropertyValue(TIMEOUT_CONFIG);
        AsynTaskExecutor taskExecutor = new AsynTaskExecutor();
        if (!StringUtils.isEmpty(timeout)) {
            taskExecutor.setTimeout(Long.parseLong(timeout));
        }
        return taskExecutor;
    }
}
