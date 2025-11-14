# 并发安全机制

> **版本**: 1.1.1+  
> **适用场景**: 并行网关和包容网关的分支合并

## 概述

在并行网关和包容网关的分支合并场景中，当多个分支同时到达汇聚节点时，可能会出现数据覆盖问题。例如：

- **问题场景**：分支 A 和分支 B 几乎同时到达汇聚节点
- **风险**：分支 A 读取汇聚节点状态 → 分支 B 读取汇聚节点状态 → 分支 A 更新状态 → 分支 B 更新状态（覆盖了 A 的更新）

为了解决这个问题，parallel-plugin 提供了可扩展的并发锁机制。

## 核心特性

✅ **默认单机锁**：开箱即用，无需额外配置  
✅ **可扩展设计**：支持自定义锁实现（如 Redis 分布式锁）  
✅ **自动重试**：获取锁失败时自动等待并重试，不会直接失败  
✅ **线程安全**：使用 `ReentrantLock` 保证并发安全  
✅ **资源管理**：自动释放锁，确保不会出现死锁

## 默认实现（单机锁）

### 工作原理

默认使用 `LocalParallelMergeLock` 实现，基于 `ReentrantLock`：

- **锁存储**：使用 `ConcurrentHashMap` 存储锁，key 格式：`{flowInstanceId}:{nodeKey}`
- **锁类型**：公平锁（`ReentrantLock(true)`），确保先到先得
- **可重入**：同一线程可以多次获取同一把锁
- **自动清理**：锁在获取时自动创建，但不会自动清理（避免频繁创建/删除的开销）

### 配置项

在 `application.properties` 或 `plugin.properties` 中配置：

```properties
# 获取锁失败后的重试间隔（毫秒），默认：50
turbo.plugin.parallelGateway.lock.retryIntervalMs=50

# 最大重试次数，默认：10
turbo.plugin.parallelGateway.lock.maxRetryTimes=10
```

### 使用场景

✅ **单机部署**：默认实现完全满足需求  
✅ **开发测试**：无需额外配置即可使用  
❌ **多机部署**：需要使用分布式锁（见下方扩展指南）

## 扩展指南

### 实现自定义锁

#### 1. 实现接口

实现 `ParallelMergeLock` 接口：

```java
package com.example.plugin;

import com.didiglobal.turbo.plugin.lock.ParallelMergeLock;
import org.springframework.stereotype.Component;

@Component
public class RedisParallelMergeLock implements ParallelMergeLock {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private static final String LOCK_PREFIX = "parallel:merge:lock:";
    private static final long DEFAULT_LOCK_TIMEOUT_MS = 30000; // 30秒
    
    @Override
    public boolean tryLock(String flowInstanceId, String nodeKey, long waitTimeMs) {
        String lockKey = LOCK_PREFIX + flowInstanceId + ":" + nodeKey;
        String lockValue = Thread.currentThread().getName() + ":" + System.currentTimeMillis();
        
        if (waitTimeMs <= 0) {
            // 立即尝试获取锁
            Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, Duration.ofMillis(DEFAULT_LOCK_TIMEOUT_MS));
            return Boolean.TRUE.equals(result);
        } else {
            // 等待指定时间后尝试获取
            long endTime = System.currentTimeMillis() + waitTimeMs;
            while (System.currentTimeMillis() < endTime) {
                Boolean result = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, lockValue, Duration.ofMillis(DEFAULT_LOCK_TIMEOUT_MS));
                if (Boolean.TRUE.equals(result)) {
                    return true;
                }
                try {
                    Thread.sleep(50); // 短暂等待后重试
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return false;
        }
    }
    
    @Override
    public void unlock(String flowInstanceId, String nodeKey) {
        String lockKey = LOCK_PREFIX + flowInstanceId + ":" + nodeKey;
        // 使用 Lua 脚本确保只删除当前线程持有的锁
        String script = 
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";
        redisTemplate.execute(
            new DefaultRedisScript<>(script, Long.class),
            Collections.singletonList(lockKey),
            Thread.currentThread().getName()
        );
    }
}
```

#### 2. 注册 Bean

通过 `@Component` 注解或配置类注册：

**方式一：使用 @Component（推荐）**
```java
@Component
public class RedisParallelMergeLock implements ParallelMergeLock {
    // ... 实现代码
}
```

**方式二：使用配置类**
```java
@Configuration
public class CustomLockConfig {
    @Bean
    public ParallelMergeLock parallelMergeLock() {
        return new RedisParallelMergeLock();
    }
}
```

#### 3. 验证

插件会自动检测到自定义的 `ParallelMergeLock` Bean，并使用它替代默认实现。

### Redis 分布式锁最佳实践

#### 使用 Redisson（推荐）

```java
@Component
public class RedissonParallelMergeLock implements ParallelMergeLock {
    
    @Autowired
    private RedissonClient redissonClient;
    
    @Override
    public boolean tryLock(String flowInstanceId, String nodeKey, long waitTimeMs) {
        String lockKey = "parallel:merge:lock:" + flowInstanceId + ":" + nodeKey;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            if (waitTimeMs <= 0) {
                return lock.tryLock();
            } else {
                return lock.tryLock(waitTimeMs, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    public void unlock(String flowInstanceId, String nodeKey) {
        String lockKey = "parallel:merge:lock:" + flowInstanceId + ":" + nodeKey;
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
```

#### 使用 Spring Data Redis

参考上面的 Redis 示例代码，注意以下几点：

1. **锁超时**：设置合理的锁超时时间（建议 30 秒），防止死锁
2. **原子性**：使用 Lua 脚本确保解锁的原子性
3. **锁值**：使用唯一值（如线程名+时间戳）标识锁的持有者
4. **异常处理**：正确处理 `InterruptedException`

## 工作原理

### 锁获取流程

```
分支到达汇聚节点
    ↓
尝试获取锁（立即返回）
    ↓
成功？ → 是 → 执行分支合并逻辑 → 释放锁
    ↓
    否
    ↓
等待重试间隔（默认 50ms）
    ↓
再次尝试获取锁
    ↓
成功？ → 是 → 执行分支合并逻辑 → 释放锁
    ↓
    否
    ↓
重复重试（最多 10 次）
    ↓
达到最大重试次数 → 抛出异常
```

### 锁的粒度

- **锁的 key**：`{flowInstanceId}:{nodeKey}`
- **粒度**：每个流程实例的每个汇聚节点使用独立的锁
- **优势**：不同流程实例或不同汇聚节点之间不会相互阻塞

## 性能考虑

### 单机锁（默认）

- **性能**：极高，无网络开销
- **适用**：单机部署、开发测试
- **限制**：不支持多机部署

### 分布式锁（Redis）

- **性能**：较高，有网络开销（通常 < 1ms）
- **适用**：多机部署、高可用场景
- **建议**：
  - 使用 Redis 集群提高可用性
  - 合理设置锁超时时间
  - 考虑使用 Redisson 等成熟框架

### 重试参数调优

根据实际场景调整重试参数：

**高并发场景**：
```properties
# 缩短重试间隔，加快响应
turbo.plugin.parallelGateway.lock.retryIntervalMs=20
# 增加重试次数，提高成功率
turbo.plugin.parallelGateway.lock.maxRetryTimes=20
```

**低并发场景**：
```properties
# 默认配置即可
turbo.plugin.parallelGateway.lock.retryIntervalMs=50
turbo.plugin.parallelGateway.lock.maxRetryTimes=10
```

## 常见问题

### Q1: 什么时候需要使用分布式锁？

**A**: 当您的应用部署在多台服务器上时，必须使用分布式锁。单机部署使用默认的单机锁即可。

### Q2: 锁获取失败会怎样？

**A**: 插件会自动重试（默认最多 10 次，每次间隔 50ms）。如果所有重试都失败，会抛出异常，建议在业务层进行重试。

### Q3: 锁会影响性能吗？

**A**: 
- **单机锁**：影响极小（通常 < 0.1ms）
- **分布式锁**：有轻微影响（通常 < 1ms），但能保证数据一致性

### Q4: 如何监控锁的使用情况？

**A**: 
- 查看日志中的锁获取和释放信息
- 对于 Redis 分布式锁，可以使用 Redis 监控工具查看锁的状态
- 建议在生产环境添加锁获取失败率的监控指标

### Q5: 锁超时时间如何设置？

**A**: 
- **单机锁**：无需设置，由 JVM 管理
- **分布式锁**：建议设置为业务操作最大耗时的 2-3 倍（如业务操作最多 10 秒，锁超时设置为 30 秒）

## 相关文档

- 📖 [并行网关使用指南](./Parallel&InclusiveGateway.md)
- 📖 [线程池配置指南](./ThreadPoolConfiguration.md)
- 📋 [更新日志](./CHANGELOG.md)

