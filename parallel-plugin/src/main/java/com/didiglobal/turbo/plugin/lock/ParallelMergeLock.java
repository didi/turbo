package com.didiglobal.turbo.plugin.lock;

/**
 * 并行网关分支合并锁接口
 * 
 * <p>用于在分支合并时提供并发控制，防止多个分支同时到达时出现数据覆盖问题。
 * 
 * <p>默认提供单机锁实现（{@link LocalParallelMergeLock}），
 * 用户可以通过实现此接口并注册为 Spring Bean 来提供自定义的锁实现（如基于 Redis 的分布式锁）。
 * 
 * <p>锁的 key 格式为：{flowInstanceId}:{nodeKey}，确保同一流程实例的同一汇聚节点使用同一个锁。
 * 
 * <p><strong>实现要求：</strong>
 * <ul>
 *   <li>锁应该是可重入的（同一线程可以多次获取）</li>
 *   <li>如果获取锁失败，应该返回 false 而不是抛出异常</li>
 *   <li>释放锁时应该是幂等的（多次释放不会出错）</li>
 * </ul>
 * 
 * <p><strong>使用示例（Redis 分布式锁）：</strong>
 * <pre>
 * &#64;Component
 * public class RedisParallelMergeLock implements ParallelMergeLock {
 *     &#64;Autowired
 *     private RedisTemplate&lt;String, String&gt; redisTemplate;
 *     
 *     &#64;Override
 *     public boolean tryLock(String flowInstanceId, String nodeKey, long waitTimeMs) {
 *         String lockKey = flowInstanceId + ":" + nodeKey;
 *         // 使用 Redis SET NX EX 实现分布式锁
 *         Boolean result = redisTemplate.opsForValue()
 *             .setIfAbsent(lockKey, Thread.currentThread().getName(), 
 *                 Duration.ofMillis(waitTimeMs));
 *         return Boolean.TRUE.equals(result);
 *     }
 *     
 *     &#64;Override
 *     public void unlock(String flowInstanceId, String nodeKey) {
 *         String lockKey = flowInstanceId + ":" + nodeKey;
 *         redisTemplate.delete(lockKey);
 *     }
 * }
 * </pre>
 * 
 * @author turbo
 * @since 1.1.0
 */
public interface ParallelMergeLock {

    /**
     * 尝试获取锁
     * 
     * <p>如果锁已被其他线程持有，应该等待一段时间后重试，直到成功获取或超时。
     * 
     * @param flowInstanceId 流程实例ID
     * @param nodeKey 节点key（汇聚节点）
     * @param waitTimeMs 等待时间（毫秒），如果为0则立即返回，不等待
     * @return true 如果成功获取锁，false 如果获取失败（超时或异常）
     */
    boolean tryLock(String flowInstanceId, String nodeKey, long waitTimeMs);

    /**
     * 释放锁
     * 
     * <p>释放指定流程实例和节点的锁。如果锁不存在或已被释放，应该静默处理（幂等性）。
     * 
     * @param flowInstanceId 流程实例ID
     * @param nodeKey 节点key（汇聚节点）
     */
    void unlock(String flowInstanceId, String nodeKey);
}

