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
 *   <li>turbo.plugin.parallelGateway.threadPool.useVirtualThreads - 是否使用虚拟线程，默认：false（推荐 JDK 21 启用）</li>
 *   <li>turbo.plugin.parallelGateway.threadPool.corePoolSize - 核心线程数，默认：10（虚拟线程模式下忽略）</li>
 *   <li>turbo.plugin.parallelGateway.threadPool.maxPoolSize - 最大线程数，默认：20（虚拟线程模式下忽略）</li>
 *   <li>turbo.plugin.parallelGateway.threadPool.queueCapacity - 队列容量，默认：100（虚拟线程模式下忽略）</li>
 *   <li>turbo.plugin.parallelGateway.threadPool.keepAliveSeconds - 线程空闲时间（秒），默认：60（虚拟线程模式下忽略）</li>
 *   <li>turbo.plugin.parallelGateway.threadPool.threadNamePrefix - 线程名称前缀，默认：parallel-gateway-</li>
 *   <li>turbo.plugin.parallelGateway.threadPool.timeout - 执行超时时间（毫秒），默认：0（不超时）</li>
 * </ul>
 * 
 * <p><strong>虚拟线程模式（JDK 21+）</strong>：
 * <pre>
 * turbo.plugin.parallelGateway.threadPool.useVirtualThreads=true
 * </pre>
 * 启用后将使用虚拟线程执行并行任务，具有以下优势：
 * <ul>
 *   <li>极低的内存和 CPU 开销</li>
 *   <li>支持大规模并行（可轻松支持数千个并行分支）</li>
 *   <li>无需复杂的线程池参数调优</li>
 *   <li>更适合 I/O 密集型任务（数据库操作、用户任务等待等）</li>
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

    private static final String USE_VIRTUAL_THREADS_CONFIG = "turbo.plugin.parallelGateway.threadPool.useVirtualThreads";
    private static final String CORE_POOL_SIZE_CONFIG = "turbo.plugin.parallelGateway.threadPool.corePoolSize";
    private static final String MAX_POOL_SIZE_CONFIG = "turbo.plugin.parallelGateway.threadPool.maxPoolSize";
    private static final String QUEUE_CAPACITY_CONFIG = "turbo.plugin.parallelGateway.threadPool.queueCapacity";
    private static final String KEEP_ALIVE_SECONDS_CONFIG = "turbo.plugin.parallelGateway.threadPool.keepAliveSeconds";
    private static final String THREAD_NAME_PREFIX_CONFIG = "turbo.plugin.parallelGateway.threadPool.threadNamePrefix";
    private static final String TIMEOUT_CONFIG = "turbo.plugin.parallelGateway.threadPool.timeout";

    // 默认值
    private static final boolean DEFAULT_USE_VIRTUAL_THREADS = false;
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
     * <p>支持虚拟线程模式（JDK 21+），通过配置 useVirtualThreads=true 启用
     *
     * @return AsynTaskExecutor 实例
     */
    @Bean
    @ConditionalOnMissingBean(AsynTaskExecutor.class)
    public AsynTaskExecutor executorService() {
        AsynTaskExecutor taskExecutor = new AsynTaskExecutor();
        
        // 检查是否使用虚拟线程
        String useVirtualThreadsStr = PluginPropertiesUtil.getPropertyValue(USE_VIRTUAL_THREADS_CONFIG);
        boolean useVirtualThreads = StringUtils.isEmpty(useVirtualThreadsStr) 
            ? DEFAULT_USE_VIRTUAL_THREADS 
            : Boolean.parseBoolean(useVirtualThreadsStr);
        
        if (useVirtualThreads) {
            // 使用虚拟线程模式（JDK 21+）
            configureVirtualThreadExecutor(taskExecutor);
        } else {
            // 使用传统平台线程池模式
            configurePlatformThreadExecutor(taskExecutor);
        }
        
        // 线程名称前缀（两种模式都适用）
        String threadNamePrefix = PluginPropertiesUtil.getPropertyValue(THREAD_NAME_PREFIX_CONFIG);
        taskExecutor.setThreadNamePrefix(StringUtils.isEmpty(threadNamePrefix) 
            ? DEFAULT_THREAD_NAME_PREFIX 
            : threadNamePrefix);
        
        // 超时时间（两种模式都适用）
        String timeout = PluginPropertiesUtil.getPropertyValue(TIMEOUT_CONFIG);
        taskExecutor.setTimeout(StringUtils.isEmpty(timeout) 
            ? DEFAULT_TIMEOUT 
            : Long.parseLong(timeout));
        
        // 初始化线程池
        taskExecutor.initialize();
        
        return taskExecutor;
    }

    /**
     * 配置虚拟线程执行器（JDK 21+）
     * 虚拟线程轻量级，可以创建大量线程而不会耗尽系统资源
     */
    private void configureVirtualThreadExecutor(AsynTaskExecutor taskExecutor) {
        try {
            // 使用虚拟线程工厂
            taskExecutor.setVirtualThreads(true);
            // 虚拟线程模式下不需要限制线程池大小
            taskExecutor.setCorePoolSize(Integer.MAX_VALUE);
            taskExecutor.setMaxPoolSize(Integer.MAX_VALUE);
            taskExecutor.setQueueCapacity(0); // 不使用队列，直接执行
        } catch (Exception e) {
            // JDK 版本低于 21 或不支持虚拟线程，回退到平台线程模式
            configurePlatformThreadExecutor(taskExecutor);
        }
    }

    /**
     * 配置传统平台线程池执行器
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
