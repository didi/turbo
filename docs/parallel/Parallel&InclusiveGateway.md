## 并行网关&包容网关插件

> **当前版本**: 1.0.1  
> **发布日期**: 2025-11-17  
> **最低要求**: Turbo Engine 1.2.1+

### 📚 文档导航
- 📖 [线程池配置指南](./ThreadPoolConfiguration.md) - 详细的线程池配置和调优说明
- 🔒 [并发安全机制](./ConcurrencySafety.md) - 分支合并锁机制和扩展指南
- ⬅️ [返回主文档](../../README.md)

---

### 1. 概述
本插件为Turbo提供"并行网关"和"包容网关"的多分支并行处理能力，使开发者可以在工作流中灵活处理分支流程。

**核心特性**：
- ✅ 支持多分支并行执行
- ✅ 支持嵌套并行网关
- ✅ 支持灵活的分支汇聚策略
- ✅ 支持自定义数据合并策略
- ✅ 支持线程池配置和优雅停机
- ✅ 支持并发安全机制（防止分支合并时的数据覆盖问题）

**🌟🌟🌟重要提示**：并行网关与包容网关均不支持跨网关的节点回滚操作

### 2. 功能介绍
#### 2.1 并行网关
* 支持在流程节点处创建多个并行任务。
* 所有分支任务完成后，流程继续向下执行。
* 应用场景：同时启动多个独立任务，如审批、数据处理。

#### 2.2 包容网关
* 支持有选择性地激活部分分支任务。
* 允许多个分支执行完毕后合并，未执行的分支不影响主流程。
* 应用场景：根据条件选择性地执行某些任务，如特定条件下的审批链。

### 3. 插件依赖
* Turbo 1.2.1+

### 4. 快速开始

#### 4.1 添加依赖

```xml
<dependency>
    <groupId>com.didiglobal.turbo</groupId>
    <artifactId>parallel-plugin</artifactId>
    <version>1.0.1</version>
</dependency>
<dependency>
    <groupId>com.didiglobal.turbo</groupId>
    <artifactId>engine</artifactId>
    <version>1.2.1</version>
</dependency>
```

#### 4.2 基础配置

在 `application.properties` 或 `plugin.properties` 中添加以下配置：

```properties
# 数据库连接配置
turbo.plugin.jdbc.url=jdbc:mysql://127.0.0.1:3306/t_engine?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&autoReconnect=true
turbo.plugin.jdbc.username=username
turbo.plugin.jdbc.password=password
turbo.plugin.jdbc.driver=com.mysql.jdbc.Driver
turbo.plugin.jdbc.maximumPoolSize=10

# 节点类型配置（可选）
# 自定义设置并行网关与包容网关NodeType。并行网关默认为9，包容网关默认为10。如非覆盖Turbo原有执行器插件，请不要设置为1-8
turbo.plugin.element_type.ParallelGatewayElementPlugin=9
turbo.plugin.element_type.InclusiveGatewayElementPlugin=10

# 功能开关配置（可选）
# 并行网关与包容网关的开关配置。默认为true开启
turbo.plugin.support.ParallelGatewayElementPlugin=true
turbo.plugin.support.InclusiveGatewayElementPlugin=true
```

#### 4.3 线程池配置（可选）

并行网关使用线程池来执行并行分支任务。插件提供了合理的默认配置，一般情况下无需调整。

**默认配置（平台线程）：**
- 核心线程数：10
- 最大线程数：20
- 队列容量：100
- 超时时间：0（不超时）

**自定义配置：**
```properties
# 平台线程池配置
turbo.plugin.parallelGateway.threadPool.corePoolSize=10
turbo.plugin.parallelGateway.threadPool.maxPoolSize=20
turbo.plugin.parallelGateway.threadPool.queueCapacity=100
turbo.plugin.parallelGateway.threadPool.keepAliveSeconds=60
turbo.plugin.parallelGateway.threadPool.threadNamePrefix=parallel-gateway-
turbo.plugin.parallelGateway.threadPool.timeout=3000
```

**📖 详细的线程池配置和调优建议，请参考：[线程池配置指南](./ThreadPoolConfiguration.md)**

#### 4.4 并发锁配置（可选）

并行网关在分支合并时使用锁机制来防止并发分支覆盖问题。插件提供了默认的单机锁实现，一般情况下无需配置。

**默认配置（单机锁）：**
- 使用 `ReentrantLock` 实现，适用于单机部署
- 自动重试机制：获取锁失败时等待后重试
- 默认重试间隔：50ms
- 默认最大重试次数：10 次

**自定义配置：**
```properties
# 重试间隔（毫秒）
turbo.plugin.parallelGateway.lock.retryIntervalMs=50
# 最大重试次数
turbo.plugin.parallelGateway.lock.maxRetryTimes=10
```

**分布式锁扩展（Redis 示例）：**
```java
@Configuration
public class CustomLockConfig {
    @Bean
    public ParallelMergeLock parallelMergeLock() {
        return new RedisParallelMergeLock(); // 用户自定义实现
    }
}
```

**📖 详细的并发安全机制、锁扩展指南和最佳实践，请参考：[并发安全机制](./ConcurrencySafety.md)**

### 5. 插件使用

#### 5.1 并发安全机制（1.0.1 新增）

从 1.0.1 版本开始，parallel-plugin 提供了并发安全机制，防止分支合并时的数据覆盖问题。

**工作原理**：
- 当分支到达汇聚节点时，首先尝试获取锁
- 如果获取锁失败，会等待一段时间后自动重试
- 获取锁成功后，执行分支合并逻辑
- 合并完成后自动释放锁

**默认配置**：
- 使用单机锁（`ReentrantLock`），适用于单机部署
- 自动重试机制：默认重试间隔 50ms，最多重试 10 次

**多机部署**：
如果您的应用部署在多台服务器上，需要实现自定义的分布式锁（如 Redis）。详细说明请参考：[并发安全机制](./ConcurrencySafety.md)

#### 5.2 嵌套并行网关（1.0.1 新增）

从 1.2.1 版本开始，parallel-plugin 支持嵌套并行网关，即在并行网关的某个分支内部再创建并行网关。

**使用场景示例**：
```
主流程并行分支：
  ├─ 分支1：审批流程（内部嵌套并行网关）
  │   ├─ 内部分支1-1：财务审批
  │   └─ 内部分支1-2：法务审批
  ├─ 分支2：数据处理
  └─ 分支3：通知发送

所有分支（包括嵌套的子分支）完成后，主流程继续执行。
```

**流程结构**：
```
                                  |---> 二级ParallelFork ---> Task1-1 --|
                 |---> ExclusiveGW ----|                                  |---> 二级ParallelJoin --|
StartEvent --->  |                     |---> 二级ParallelFork ---> Task1-2 --|                       |
  一级ParallelFork|---> ExclusiveGW ---> Task2 ---------------------------------------------------|---> 一级ParallelJoin ---> EndEvent
                 |---> ExclusiveGW ---> Task3 ---------------------------------------------------|
```

**重要说明**：
- ⚠️ 嵌套并行网关需要合理配置线程池参数，确保线程数 >= 最大并行分支总数
- ⚠️ 建议嵌套层级不超过 3 层，避免复杂度过高
- ✅ 嵌套并行网关的汇聚逻辑与单层并行网关一致
- ✅ 支持在嵌套并行网关中使用包容网关

**线程池配置建议**：

如果使用平台线程，核心线程数应该 >= 最大并行分支总数。例如上述场景：
- 一级并行分支：3 个
- 二级并行分支：2 个
- 最大并行总数：3 + 2 = 5

```properties
turbo.plugin.parallelGateway.threadPool.corePoolSize=5
turbo.plugin.parallelGateway.threadPool.maxPoolSize=10
turbo.plugin.parallelGateway.threadPool.queueCapacity=50
```

💡 **性能提示**：在高并发场景下，建议根据实际业务需求合理设置线程池参数，避免线程资源耗尽。

更多线程池配置详情，请参考：[线程池配置指南](./ThreadPoolConfiguration.md)

#### 5.3 分支汇聚策略
并行网关与包容网关都支持指定分支汇聚策略，目前支持的策略有：
* JoinAll（默认）：所有分支任务完成后到达汇聚节点，继续向下执行。
* AnyOne：任意一个分支任务完成后到达汇聚节点，继续向下执行。
* Custom：自定义策略，需继承`com.didiglobal.turbo.plugin.executor.BranchMergeCustom`类，重写`joinFirst`、`joinMerge`方法，并在该类上添加`@Primary`注解。

#### 5.4 数据汇聚策略
并行网关与包容网关都支持指定分支数据合并策略，目前支持的策略有：
* All（默认）: 将所有分支的数据合并到一个Map中，并作为参数传递给下游节点。需要注意的是，如果key相同的情况下，后到达的分支数据会覆盖之前到达的分支数据。
* None: 不进行数据合并，使用分支fork时的数据作为参数传递给下游节点。
* Custom: 自定义策略，需继承`com.didiglobal.turbo.plugin.executor.DataMergeCustom`类，重写`merge`方法，并在该类上添加`@Primary`注解。

#### 5.5 并行网关节点示例
```java
{
    ParallelGateway parallelGateway = new ParallelGateway();
    // 设置节点key, 节点唯一标识
    parallelGateway.setKey("ParallelGateway_38ad233");
    // 设置节点类型, 默认为9
    parallelGateway.setType(ExtendFlowElementType.PARALLEL_GATEWAY);

    List<String> egIncomings = new ArrayList<>();
    egIncomings.add("SequenceFlow_2gugjee");
    parallelGateway.setIncoming(egIncomings);

    // 设置多个分支出口
    List<String> egOutgoings = new ArrayList<>();
    egOutgoings.add("SequenceFlow_12rbl6u");
    egOutgoings.add("SequenceFlow_3ih7eta");
    parallelGateway.setOutgoing(egOutgoings);

    Map<String, Object> properties = new HashMap<>();
    Map<String, String> forkJoinMatch = new HashMap<>();
    // 记录分支Fork节点
    forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK, "ParallelGateway_38ad233");
    // 记录分支Join节点
    forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.JOIN, "ParallelGateway_10lo44j");
    properties.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK_JOIN_MATCH, JSONArray.toJSON(forkJoinMatch));
    parallelGateway.setProperties(properties);
}
```

#### 5.6 包容网关节点示例
```java
{
    InclusiveGateway inclusiveGateway = new InclusiveGateway();
    // 设置节点key, 节点唯一标识
    inclusiveGateway.setKey("InclusiveGateway_3a1nn9f");
    // 设置节点类型, 默认为10
    inclusiveGateway.setType(ExtendFlowElementType.INCLUSIVE_GATEWAY);

    // 多个分支入口
    List<String> egIncomings = new ArrayList<>();
    egIncomings.add("SequenceFlow_1h65e8t");
    egIncomings.add("SequenceFlow_25kdv36");
    inclusiveGateway.setIncoming(egIncomings);

    List<String> egOutgoings = new ArrayList<>();
    egOutgoings.add("SequenceFlow_3jkd63g");
    inclusiveGateway.setOutgoing(egOutgoings);

    Map<String, Object> properties = new HashMap<>();
    Map<String, String> forkJoinMatch = new HashMap<>();
    // 记录分支Fork节点
    forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK, "InclusiveGateway_1djgrgp");
    // 记录分支Join节点
    forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.JOIN, "InclusiveGateway_3a1nn9f");
    properties.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK_JOIN_MATCH, JSONArray.toJSON(forkJoinMatch));
    // 设置分支汇聚策略(在汇聚节点设置)
    properties.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.BRANCH_MERGE, MergeStrategy.BRANCH_MERGE.ANY_ONE);
    // 设置分支数据合并策略(在汇聚节点设置)
    properties.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.DATA_MERGE, MergeStrategy.DATA_MERGE.NONE);
    inclusiveGateway.setProperties(properties);
}
```
