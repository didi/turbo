package com.didiglobal.turbo.plugin.config;

import com.didiglobal.turbo.engine.util.PluginPropertiesUtil;
import com.didiglobal.turbo.plugin.lock.LocalParallelMergeLock;
import com.didiglobal.turbo.plugin.lock.ParallelMergeLock;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 并行网关分支合并锁配置
 * 
 * <p>提供默认的单机锁实现（{@link LocalParallelMergeLock}），
 * 用户可以通过实现 {@link ParallelMergeLock} 接口并注册为 Spring Bean 来提供自定义的锁实现。
 * 
 * <p>支持以下配置项：
 * <ul>
 *   <li>turbo.plugin.parallelGateway.lock.retryIntervalMs - 获取锁失败后的重试间隔（毫秒），默认：50</li>
 *   <li>turbo.plugin.parallelGateway.lock.maxRetryTimes - 最大重试次数，默认：100</li>
 * </ul>
 * 
 * <p><strong>自定义锁实现示例（Redis 分布式锁）：</strong>
 * <pre>
 * &#64;Configuration
 * public class CustomLockConfig {
 *     &#64;Bean
 *     public ParallelMergeLock parallelMergeLock() {
 *         return new RedisParallelMergeLock();
 *     }
 * }
 * </pre>
 * 
 * @author turbo
 * @since 1.1.0
 */
@Configuration
public class ParallelMergeLockConfig {

    private static final String RETRY_INTERVAL_MS_CONFIG = "turbo.plugin.parallelGateway.lock.retryIntervalMs";
    private static final String MAX_RETRY_TIMES_CONFIG = "turbo.plugin.parallelGateway.lock.maxRetryTimes";

    // 默认值
    private static final long DEFAULT_RETRY_INTERVAL_MS = 50L;
    private static final int DEFAULT_MAX_RETRY_TIMES = 10;

    /**
     * 创建并行网关分支合并锁
     * 
     * <p>使用 @ConditionalOnMissingBean 注解，允许用户通过自定义 Bean 来覆盖此配置
     * 
     * @return ParallelMergeLock 实例（默认使用 LocalParallelMergeLock）
     */
    @Bean
    @ConditionalOnMissingBean(ParallelMergeLock.class)
    public ParallelMergeLock parallelMergeLock() {
        return new LocalParallelMergeLock();
    }

    /**
     * 获取重试间隔（毫秒）
     * 
     * @return 重试间隔，默认 50ms
     */
    public static long getRetryIntervalMs() {
        String value = PluginPropertiesUtil.getPropertyValue(RETRY_INTERVAL_MS_CONFIG);
        return StringUtils.isEmpty(value) ? DEFAULT_RETRY_INTERVAL_MS : Long.parseLong(value);
    }

    /**
     * 获取最大重试次数
     * 
     * @return 最大重试次数，默认 100 次
     */
    public static int getMaxRetryTimes() {
        String value = PluginPropertiesUtil.getPropertyValue(MAX_RETRY_TIMES_CONFIG);
        return StringUtils.isEmpty(value) ? DEFAULT_MAX_RETRY_TIMES : Integer.parseInt(value);
    }
}

