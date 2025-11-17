package com.didiglobal.turbo.plugin.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 本地并行网关分支合并锁实现（单机锁）
 * 
 * <p>使用 {@link ReentrantLock} 实现，适用于单机部署场景。
 * 
 * <p>锁存储在内存中的 {@link ConcurrentHashMap} 中，key 格式为：{flowInstanceId}:{nodeKey}
 * 
 * <p><strong>注意：</strong>
 * <ul>
 *   <li>此实现仅适用于单机部署，多机部署时需要使用分布式锁（如 Redis）</li>
 *   <li>锁会在获取时自动创建，但不会自动清理（避免频繁创建/删除的开销）</li>
 *   <li>锁是可重入的，同一线程可以多次获取</li>
 * </ul>
 * 
 * <p>此实现由 {@link com.didiglobal.turbo.plugin.config.ParallelMergeLockConfig} 配置类创建，
 * 用户可以通过实现 {@link ParallelMergeLock} 接口并注册为 Spring Bean 来覆盖默认实现。
 * 
 * @author turbo
 * @since 1.1.0
 */
public class LocalParallelMergeLock implements ParallelMergeLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalParallelMergeLock.class);

    /**
     * 锁存储：key = flowInstanceId:nodeKey, value = ReentrantLock
     */
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    public boolean tryLock(String flowInstanceId, String nodeKey, long waitTimeMs) {
        String lockKey = buildLockKey(flowInstanceId, nodeKey);
        ReentrantLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantLock(true)); // 公平锁
        
        try {
            if (waitTimeMs <= 0) {
                // 立即尝试获取，不等待
                return lock.tryLock();
            } else {
                // 等待指定时间后尝试获取
                return lock.tryLock(waitTimeMs, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.warn("Interrupted while waiting for lock.||lockKey={}", lockKey, e);
            return false;
        } catch (Exception e) {
            LOGGER.error("Failed to acquire lock.||lockKey={}", lockKey, e);
            return false;
        }
    }

    @Override
    public void unlock(String flowInstanceId, String nodeKey) {
        String lockKey = buildLockKey(flowInstanceId, nodeKey);
        ReentrantLock lock = locks.get(lockKey);
        if (lock != null && lock.isHeldByCurrentThread()) {
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException e) {
                // 锁已被释放或不是当前线程持有，静默处理（幂等性）
                LOGGER.debug("Lock already released or not held by current thread.||lockKey={}", lockKey);
            }
        }
    }

    /**
     * 构建锁的 key
     */
    private String buildLockKey(String flowInstanceId, String nodeKey) {
        return flowInstanceId + ":" + nodeKey;
    }
}

