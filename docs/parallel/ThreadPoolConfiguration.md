# 并行网关线程池配置指南

> 📖 **返回主文档**：[并行网关&包容网关使用指南](./Parallel&InclusiveGateway.md)

parallel-plugin 提供了灵活的线程池配置方式，支持通过配置文件配置或完全自定义 Bean。

## 概述

并行网关在执行并行分支任务时，使用独立的线程池来提升性能。合理配置线程池参数可以：
- ✅ 提升并行执行效率
- ✅ 避免线程资源耗尽
- ✅ 支持高并发场景
- ✅ 实现超时控制

## 方式一：配置文件配置（推荐）

### 平台线程池配置

并行网关使用传统的平台线程池（ThreadPoolExecutor）来执行并行分支任务。

**application.properties 示例：**

```properties
# 平台线程池配置
turbo.plugin.parallelGateway.threadPool.corePoolSize=10
turbo.plugin.parallelGateway.threadPool.maxPoolSize=20
turbo.plugin.parallelGateway.threadPool.queueCapacity=100
turbo.plugin.parallelGateway.threadPool.keepAliveSeconds=60
turbo.plugin.parallelGateway.threadPool.threadNamePrefix=parallel-gateway-
turbo.plugin.parallelGateway.threadPool.timeout=0
```

**application.yml 示例：**

```yaml
turbo:
  plugin:
    parallelGateway:
      threadPool:
        corePoolSize: 10
        maxPoolSize: 20
        queueCapacity: 100
        keepAliveSeconds: 60
        threadNamePrefix: parallel-gateway-
        timeout: 0
```

### 配置项说明

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `corePoolSize` | 核心线程数 | 10 |
| `maxPoolSize` | 最大线程数 | 20 |
| `queueCapacity` | 队列容量 | 100 |
| `keepAliveSeconds` | 线程空闲时间（秒） | 60 |
| `threadNamePrefix` | 线程名称前缀 | parallel-gateway- |
| `timeout` | 执行超时时间（毫秒） | 0（不超时） |

### 配置建议

根据不同的使用场景，可以参考以下配置：

1. **轻量级场景**（并行分支数 ≤ 3，并发流程实例 ≤ 10）
   ```properties
   turbo.plugin.parallelGateway.threadPool.corePoolSize=5
   turbo.plugin.parallelGateway.threadPool.maxPoolSize=10
   turbo.plugin.parallelGateway.threadPool.queueCapacity=50
   ```

2. **中等负载场景**（并行分支数 3-10，并发流程实例 10-100）
   ```properties
   turbo.plugin.parallelGateway.threadPool.corePoolSize=10
   turbo.plugin.parallelGateway.threadPool.maxPoolSize=20
   turbo.plugin.parallelGateway.threadPool.queueCapacity=100
   ```

3. **高负载场景**（并行分支数 > 10，并发流程实例 > 100）
   ```properties
   turbo.plugin.parallelGateway.threadPool.corePoolSize=20
   turbo.plugin.parallelGateway.threadPool.maxPoolSize=50
   turbo.plugin.parallelGateway.threadPool.queueCapacity=200
   ```

## 方式二：自定义 Bean（高级用户）

如果需要更精细的控制，可以通过自定义 Bean 来完全覆盖默认配置。

### 自定义线程池配置示例

```java
import com.didiglobal.turbo.plugin.executor.AsynTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class CustomThreadPoolConfig {
    
    @Bean
    public AsynTaskExecutor executorService() {
        AsynTaskExecutor taskExecutor = new AsynTaskExecutor();
        
        // 基本配置
        taskExecutor.setCorePoolSize(50);
        taskExecutor.setMaxPoolSize(100);
        taskExecutor.setQueueCapacity(500);
        taskExecutor.setKeepAliveSeconds(300);
        taskExecutor.setThreadNamePrefix("my-parallel-");
        
        // 超时配置
        taskExecutor.setTimeout(30000L); // 30秒超时
        
        // 拒绝策略（可选）
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 等待任务完成后再关闭（优雅停机）
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        
        // 初始化线程池
        taskExecutor.initialize();
        
        return taskExecutor;
    }
}
```

## 配置优先级

1. **用户自定义 Bean** > 配置文件配置 > 默认值
2. 如果定义了自定义 Bean，配置文件的配置将被忽略
3. 如果没有配置，使用默认值

## 监控和调优

### 线程池监控

```java
@Autowired
private AsynTaskExecutor executorService;

public void monitorThreadPool() {
    ThreadPoolExecutor executor = executorService.getThreadPoolExecutor();
    logger.info("Active threads: {}", executor.getActiveCount());
    logger.info("Core pool size: {}", executor.getCorePoolSize());
    logger.info("Max pool size: {}", executor.getMaximumPoolSize());
    logger.info("Current pool size: {}", executor.getPoolSize());
    logger.info("Queue size: {}", executor.getQueue().size());
    logger.info("Completed tasks: {}", executor.getCompletedTaskCount());
}
```

### 调优建议

1. **核心线程数设置**：
   - 计算密集型：CPU 核心数 + 1
   - I/O 密集型（数据库操作等）：CPU 核心数 * 2 ~ CPU 核心数 * 4
   - 并行网关场景：建议 >= 最大并行分支数

2. **最大线程数设置**：
   - 根据系统负载和内存情况设置
   - 建议：核心线程数 * 2 ~ 核心线程数 * 3
   - 注意：每个线程占用 1-2 MB 内存

3. **队列容量设置**：
   - 任务提交速度 > 处理速度：增大队列容量
   - 希望快速失败：减小队列容量
   - 建议：根据并发流程实例数和并行分支数估算

4. **超时时间设置**：
   - 分支执行时间可控：设置合理的超时时间
   - 超时后会抛出 `PARALLEL_EXECUTE_TIMEOUT` 异常
   - 建议：根据业务场景设置，避免任务无限等待

5. **拒绝策略选择**：
   - `AbortPolicy`（默认）：直接抛出异常，适合快速失败场景
   - `CallerRunsPolicy`：由调用线程执行，适合降级场景
   - `DiscardPolicy`：静默丢弃，不推荐使用
   - `DiscardOldestPolicy`：丢弃最老的任务，适合可容忍丢失的场景

## 常见问题

### Q1: 如何选择合适的线程池大小？
**A**: 
- 核心线程数建议 >= 最大并行分支数
- 最大线程数根据系统负载和内存情况设置，建议为核心线程数的 2-3 倍
- 队列容量根据并发流程实例数和并行分支数估算

### Q2: 线程池满了会怎样？
**A**: 默认使用 `AbortPolicy` 策略，会抛出 `RejectedExecutionException`。建议：
- 调整线程池大小（增大 `maxPoolSize` 或 `queueCapacity`）
- 使用自定义拒绝策略（如 `CallerRunsPolicy`）

### Q3: 如何监控线程池状态？
**A**: 可以通过注入 `AsynTaskExecutor` Bean，调用 `getThreadPoolExecutor()` 方法获取 `ThreadPoolExecutor` 实例，然后监控其各项指标（活跃线程数、队列大小、已完成任务数等）。

### Q4: 高并发场景下如何优化？
**A**: 
- 增大 `maxPoolSize` 和 `queueCapacity`
- 根据实际业务场景调整 `corePoolSize`
- 设置合理的超时时间避免资源占用
- 考虑使用自定义拒绝策略进行降级处理

### Q5: 如何实现优雅停机？
**A**: 在自定义 Bean 配置中设置：
```java
taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
taskExecutor.setAwaitTerminationSeconds(60);
```

## 使用场景示例

### 场景 1：嵌套并行网关

**流程**：3 个一级分支，其中 1 个分支包含 2 个二级分支

**配置示例：**
```properties
# 至少需要 5 个线程（3 + 2）
turbo.plugin.parallelGateway.threadPool.corePoolSize=5
turbo.plugin.parallelGateway.threadPool.maxPoolSize=10
turbo.plugin.parallelGateway.threadPool.queueCapacity=50
```

### 场景 2：高并发环境

**流程**：100 个并发流程实例，每个流程有 10 个并行分支

**配置示例：**
```properties
# 需要足够的线程和队列容量来应对高并发
turbo.plugin.parallelGateway.threadPool.corePoolSize=100
turbo.plugin.parallelGateway.threadPool.maxPoolSize=200
turbo.plugin.parallelGateway.threadPool.queueCapacity=1000
turbo.plugin.parallelGateway.threadPool.timeout=10000
```

**注意**：高并发场景下，1000 个并行任务需要约 1-2 GB 内存（每个线程 1-2 MB）。如果内存有限，可以考虑：
- 减小 `maxPoolSize`，增大 `queueCapacity`
- 使用自定义拒绝策略进行限流
- 优化业务流程，减少并行分支数

### 场景 3：启用超时保护

```properties
# 设置 10 秒超时，避免任务无限等待
turbo.plugin.parallelGateway.threadPool.timeout=10000
turbo.plugin.parallelGateway.threadPool.corePoolSize=10
turbo.plugin.parallelGateway.threadPool.maxPoolSize=20
```

### 场景 4：CPU 密集型任务

如果并行分支主要是 CPU 密集型计算任务：

```properties
# CPU 核心数 + 1
turbo.plugin.parallelGateway.threadPool.corePoolSize=9
turbo.plugin.parallelGateway.threadPool.maxPoolSize=18
turbo.plugin.parallelGateway.threadPool.queueCapacity=50
```

### 场景 5：I/O 密集型任务

如果并行分支主要是 I/O 操作（数据库查询、网络请求等）：

```properties
# CPU 核心数 * 2 ~ CPU 核心数 * 4
turbo.plugin.parallelGateway.threadPool.corePoolSize=16
turbo.plugin.parallelGateway.threadPool.maxPoolSize=32
turbo.plugin.parallelGateway.threadPool.queueCapacity=200
```
