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

### 虚拟线程模式（JDK 21+ 推荐）⚡

JDK 21 引入了虚拟线程（Virtual Threads），对于并行网关这种 I/O 密集型场景，**强烈推荐使用虚拟线程**。

**application.properties 示例：**

```properties
# 启用虚拟线程模式（推荐）
turbo.plugin.parallelGateway.threadPool.useVirtualThreads=true
# 线程名称前缀（可选）
turbo.plugin.parallelGateway.threadPool.threadNamePrefix=parallel-gateway-
# 超时时间（可选，单位：毫秒）
turbo.plugin.parallelGateway.threadPool.timeout=0
```

**虚拟线程优势：**
- ✅ 轻量级：每个虚拟线程只占用几 KB 内存（平台线程需要 1-2 MB）
- ✅ 高并发：可以轻松支持数千甚至数万个并行分支
- ✅ 零配置：无需调优核心线程数、最大线程数、队列容量等参数
- ✅ 适合 I/O 密集型：数据库操作、等待用户任务等场景性能显著提升

**application.yml 示例：**

```yaml
turbo:
  plugin:
    parallelGateway:
      threadPool:
        useVirtualThreads: true
        threadNamePrefix: parallel-gateway-
        timeout: 0
```

### 平台线程模式（兼容模式）

如果使用 JDK 21 以下版本，或需要传统线程池的精细控制，可以使用平台线程模式。

**application.properties 示例：**

```properties
# 平台线程池配置
turbo.plugin.parallelGateway.threadPool.useVirtualThreads=false
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
        useVirtualThreads: false
        corePoolSize: 10
        maxPoolSize: 20
        queueCapacity: 100
        keepAliveSeconds: 60
        threadNamePrefix: parallel-gateway-
        timeout: 0
```

### 配置项说明

| 配置项 | 说明 | 默认值 | 适用模式 |
|--------|------|--------|----------|
| `useVirtualThreads` | 是否使用虚拟线程 | false | 全部 |
| `threadNamePrefix` | 线程名称前缀 | parallel-gateway- | 全部 |
| `timeout` | 执行超时时间（毫秒） | 0（不超时） | 全部 |
| `corePoolSize` | 核心线程数 | 10 | 仅平台线程 |
| `maxPoolSize` | 最大线程数 | 20 | 仅平台线程 |
| `queueCapacity` | 队列容量 | 100 | 仅平台线程 |
| `keepAliveSeconds` | 线程空闲时间（秒） | 60 | 仅平台线程 |

### 模式选择建议

#### 虚拟线程模式（推荐）
**适用场景：**
- ✅ JDK 21+ 环境
- ✅ 高并发场景（大量并发流程实例）
- ✅ 大规模并行（每个流程有很多并行分支）
- ✅ I/O 密集型任务（数据库操作、等待用户输入等）

**简单配置：**
```properties
turbo.plugin.parallelGateway.threadPool.useVirtualThreads=true
```

#### 平台线程模式
**适用场景：**
- ✅ JDK 21 以下环境
- ✅ CPU 密集型计算任务
- ✅ 需要精细控制线程池行为

**配置示例：**

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

### 使用虚拟线程（推荐）

```java
import com.didiglobal.turbo.plugin.executor.AsynTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VirtualThreadConfig {
    
    @Bean
    public AsynTaskExecutor executorService() {
        AsynTaskExecutor taskExecutor = new AsynTaskExecutor();
        
        // 启用虚拟线程
        taskExecutor.setVirtualThreads(true);
        taskExecutor.setThreadNamePrefix("vt-parallel-");
        taskExecutor.setTimeout(30000L); // 30秒超时
        
        // 初始化
        taskExecutor.initialize();
        
        return taskExecutor;
    }
}
```

### 使用平台线程（传统方式）

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

## 虚拟线程 vs 平台线程性能对比

| 对比项 | 虚拟线程 | 平台线程 |
|--------|---------|---------|
| **内存占用** | ~1 KB/线程 | 1-2 MB/线程 |
| **创建成本** | 极低 | 较高 |
| **上下文切换** | 极快 | 较慢 |
| **最大并发数** | 数百万+ | 数千（受内存限制） |
| **适用场景** | I/O 密集型 | CPU 密集型 |
| **配置复杂度** | 简单 | 需要调优 |

### 虚拟线程性能优势场景

**适合并行网关的原因：**
1. ✅ 并行分支主要是等待操作（用户任务挂起、数据库查询）
2. ✅ 可能有大量并发流程实例同时执行
3. ✅ 嵌套并行网关可能产生大量并行分支
4. ✅ 无需担心线程池耗尽问题

**性能提升示例：**
- 100 个并发流程，每个流程 10 个并行分支 = 1000 个并行任务
  - 平台线程：需要 1000 MB+ 内存，可能线程池满
  - 虚拟线程：~1 MB 内存，轻松应对

## 监控和调优

### 平台线程模式监控

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

### 平台线程调优建议

1. **核心线程数设置**：
   - 计算密集型：CPU 核心数 + 1
   - I/O 密集型（数据库操作等）：CPU 核心数 * 2 ~ CPU 核心数 * 4
   - 并行网关场景：建议 >= 最大并行分支数

2. **队列容量设置**：
   - 任务提交速度 > 处理速度：增大队列容量
   - 希望快速失败：减小队列容量

3. **超时时间设置**：
   - 分支执行时间可控：设置合理的超时时间
   - 超时后会抛出 `PARALLEL_EXECUTE_TIMEOUT` 异常

### 虚拟线程调优建议

虚拟线程模式下基本**无需调优**，只需关注：
- ⚠️ 确保底层 I/O 操作不阻塞（如使用异步数据库驱动）
- ⚠️ 避免在虚拟线程中使用 synchronized（会固定到平台线程）
- ✅ 设置合理的超时时间避免任务无限等待

## 常见问题

### Q1: 应该使用虚拟线程还是平台线程？
**A**: JDK 21+ 环境下，**强烈推荐使用虚拟线程**。并行网关的分支任务主要是 I/O 等待（数据库操作、用户任务挂起），虚拟线程在这种场景下性能远超平台线程，且无需调优。

### Q2: 虚拟线程有什么限制吗？
**A**: 
- 需要 JDK 21 及以上版本
- 避免在虚拟线程中使用 `synchronized` 同步锁（建议使用 `ReentrantLock`）
- 如果底层使用了 pinning 操作（如某些旧的数据库驱动），可能影响性能

### Q3: 为什么默认不启用虚拟线程？
**A**: 为了保持向后兼容性，默认使用平台线程模式。对于 JDK 21+ 用户，建议手动启用虚拟线程以获得更好的性能。

### Q4: 平台线程模式下线程池满了会怎样？
**A**: 默认使用 `AbortPolicy` 策略，会抛出 `RejectedExecutionException`。建议调整线程池大小或使用自定义拒绝策略。

### Q5: 虚拟线程模式下还需要配置线程池参数吗？
**A**: 不需要。虚拟线程模式下，`corePoolSize`、`maxPoolSize`、`queueCapacity` 等参数会被忽略，只需配置 `useVirtualThreads=true` 即可。

## 使用场景示例

### 场景 1：嵌套并行网关（推荐虚拟线程）

**流程**：3 个一级分支，其中 1 个分支包含 2 个二级分支

**虚拟线程配置（推荐）：**
```properties
turbo.plugin.parallelGateway.threadPool.useVirtualThreads=true
```

**平台线程配置：**
```properties
# 至少需要 5 个线程（3 + 2）
turbo.plugin.parallelGateway.threadPool.corePoolSize=5
turbo.plugin.parallelGateway.threadPool.maxPoolSize=10
```

### 场景 2：高并发环境（强烈推荐虚拟线程）

**流程**：100 个并发流程实例，每个流程有 10 个并行分支

**虚拟线程配置（推荐）：**
```properties
turbo.plugin.parallelGateway.threadPool.useVirtualThreads=true
turbo.plugin.parallelGateway.threadPool.timeout=10000
```

**平台线程配置：**
```properties
# 至少需要 1000 个线程（100 * 10），资源消耗大
turbo.plugin.parallelGateway.threadPool.corePoolSize=100
turbo.plugin.parallelGateway.threadPool.maxPoolSize=200
turbo.plugin.parallelGateway.threadPool.queueCapacity=1000
```

### 场景 3：启用超时保护

```properties
# 虚拟线程模式
turbo.plugin.parallelGateway.threadPool.useVirtualThreads=true
turbo.plugin.parallelGateway.threadPool.timeout=10000

# 或平台线程模式
turbo.plugin.parallelGateway.threadPool.timeout=10000
```

