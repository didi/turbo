package com.didiglobal.turbo.plugin.config;

import com.didiglobal.turbo.engine.util.PluginPropertiesUtil;
import com.didiglobal.turbo.plugin.executor.AsynTaskExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 并行网关线程池配置
 *
 * <p>支持以下配置项：
 * <ul>
 *   <li>turbo.plugin.parallelGateway.threadPool.corePoolSize - 核心线程数，默认：10</li>
 *   <li>turbo.plugin.parallelGateway.threadPool.maxPoolSize - 最大线程数，默认：20</li>
 *   <li>turbo.plugin.parallelGateway.threadPool.queueCapacity - 队列容量，默认：100</li>
 *   <li>turbo.plugin.parallelGateway.threadPool.keepAliveSeconds - 线程空闲时间（秒），默认：60</li>
 *   <li>turbo.plugin.parallelGateway.threadPool.threadNamePrefix - 线程名称前缀，默认：parallel-gateway-</li>
 *   <li>turbo.plugin.parallelGateway.threadPool.timeout - 执行超时时间（毫秒），默认：0（不超时）</li>
 * </ul>
 *
 * <p>使用者也可以通过自定义 Bean 来完全覆盖此配置：
 * <pre>
 * &#64;Configuration
 * public class CustomThreadPoolConfig {
 *     &#64;Bean
 *     public AsynTaskExecutor executorService() {
 *         AsynTaskExecutor taskExecutor = new AsynTaskExecutor();
 *         taskExecutor.setCorePoolSize(50);
 *         taskExecutor.setMaxPoolSize(100);
 *         // ... 其他配置
 *         return taskExecutor;
 *     }
 * }
 * </pre>
 */
@Configuration
public class ThreadPoolConfig {

    private static final String CORE_POOL_SIZE_CONFIG = "turbo.plugin.parallelGateway.threadPool.corePoolSize";
    private static final String MAX_POOL_SIZE_CONFIG = "turbo.plugin.parallelGateway.threadPool.maxPoolSize";
    private static final String QUEUE_CAPACITY_CONFIG = "turbo.plugin.parallelGateway.threadPool.queueCapacity";
    private static final String KEEP_ALIVE_SECONDS_CONFIG = "turbo.plugin.parallelGateway.threadPool.keepAliveSeconds";
    private static final String THREAD_NAME_PREFIX_CONFIG = "turbo.plugin.parallelGateway.threadPool.threadNamePrefix";
    private static final String TIMEOUT_CONFIG = "turbo.plugin.parallelGateway.threadPool.timeout";

    // 默认值
    private static final int DEFAULT_CORE_POOL_SIZE = 10;
    private static final int DEFAULT_MAX_POOL_SIZE = 20;
    private static final int DEFAULT_QUEUE_CAPACITY = 100;
    private static final int DEFAULT_KEEP_ALIVE_SECONDS = 60;
    private static final String DEFAULT_THREAD_NAME_PREFIX = "parallel-gateway-";
    private static final long DEFAULT_TIMEOUT = 0L;

    /**
     * 创建并行网关异步任务执行器
     *
     * <p>使用 @ConditionalOnMissingBean 注解，允许用户通过自定义 Bean 来覆盖此配置
     *
     * @return AsynTaskExecutor 实例
     */
    @Bean
    @ConditionalOnMissingBean(AsynTaskExecutor.class)
    public AsynTaskExecutor executorService() {
        AsynTaskExecutor taskExecutor = new AsynTaskExecutor();

        configurePlatformThreadExecutor(taskExecutor);

        // 线程名称前缀
        String threadNamePrefix = PluginPropertiesUtil.getPropertyValue(THREAD_NAME_PREFIX_CONFIG);
        taskExecutor.setThreadNamePrefix(StringUtils.isEmpty(threadNamePrefix)
            ? DEFAULT_THREAD_NAME_PREFIX
            : threadNamePrefix);

        // 超时时间
        String timeout = PluginPropertiesUtil.getPropertyValue(TIMEOUT_CONFIG);
        taskExecutor.setTimeout(StringUtils.isEmpty(timeout)
            ? DEFAULT_TIMEOUT
            : Long.parseLong(timeout));

        // 初始化线程池
        taskExecutor.initialize();

        return taskExecutor;
    }

    /**
     * 配置线程池执行器
     */
    private void configurePlatformThreadExecutor(AsynTaskExecutor taskExecutor) {
        // 核心线程数
        String corePoolSize = PluginPropertiesUtil.getPropertyValue(CORE_POOL_SIZE_CONFIG);
        taskExecutor.setCorePoolSize(StringUtils.isEmpty(corePoolSize)
            ? DEFAULT_CORE_POOL_SIZE
            : Integer.parseInt(corePoolSize));

        // 最大线程数
        String maxPoolSize = PluginPropertiesUtil.getPropertyValue(MAX_POOL_SIZE_CONFIG);
        taskExecutor.setMaxPoolSize(StringUtils.isEmpty(maxPoolSize)
            ? DEFAULT_MAX_POOL_SIZE
            : Integer.parseInt(maxPoolSize));

        // 队列容量
        String queueCapacity = PluginPropertiesUtil.getPropertyValue(QUEUE_CAPACITY_CONFIG);
        taskExecutor.setQueueCapacity(StringUtils.isEmpty(queueCapacity)
            ? DEFAULT_QUEUE_CAPACITY
            : Integer.parseInt(queueCapacity));

        // 线程空闲时间
        String keepAliveSeconds = PluginPropertiesUtil.getPropertyValue(KEEP_ALIVE_SECONDS_CONFIG);
        taskExecutor.setKeepAliveSeconds(StringUtils.isEmpty(keepAliveSeconds)
            ? DEFAULT_KEEP_ALIVE_SECONDS
            : Integer.parseInt(keepAliveSeconds));
    }
}
