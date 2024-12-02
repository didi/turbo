## 并行网关&包容网关
### 1. 概述
本插件为Turbo提供“并行网关”和“包容网关”的多分支并行处理能力，使开发者可以在工作流中灵活处理分支流程。

**🌟🌟🌟注意**：并行网关与包容网关均不支持跨网关的节点回滚操作
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
* Turbo 1.2.0+
### 4. 插件配置（plugin.properties）
* 数据库连接配置
```properties
# JDBC config
turbo.plugin.jdbc.url=jdbc:mysql://127.0.0.1:3306/t_engine?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&autoReconnect=true
turbo.plugin.jdbc.username=username
turbo.plugin.jdbc.password=password
turbo.plugin.jdbc.driver=com.mysql.jdbc.Driver
turbo.plugin.jdbc.maximumPoolSize=10
```
* 并行网关节点与包容网关节点配置
```properties
# 自定义设置并行网关与包容网关NodeType。并行网关默认为9，包容网关默认为10。如非覆盖Turbo原有执行器插件，请不要设置为1-8
turbo.plugin.element_type.ParallelGatewayElementPlugin=9
turbo.plugin.element_type.InclusiveGatewayElementPlugin=10
# 并行网关与包容网关的开关配置。默认为true开启
turbo.plugin.support.ParallelGatewayElementPlugin=true
turbo.plugin.support.InclusiveGatewayElementPlugin=true
# 并行分支执行超时时间，单位：毫秒
turbo.plugin.parallelGateway.threadPool.timeout=3000
```
### 5. 插件使用
#### 5.1 分支汇聚策略
并行网关与包容网关都支持指定分支汇聚策略，目前支持的策略有：
* JoinAll（默认）：所有分支任务完成后到达汇聚节点，继续向下执行。
* AnyOne：任意一个分支任务完成后到达汇聚节点，继续向下执行。
* Custom：自定义策略，需继承`com.didiglobal.turbo.plugin.executor.BranchMergeCustom`类，重写`joinFirst`、`joinMerge`方法，并在该类上添加`@Primary`注解。
#### 5.2 数据汇聚策略
并行网关与包容网关都支持指定分支数据合并策略，目前支持的策略有：
* All（默认）: 将所有分支的数据合并到一个Map中，并作为参数传递给下游节点。需要注意的是，如果key相同的情况下，后到达的分支数据会覆盖之前到达的分支数据。
* None: 不进行数据合并，使用分支fork时的数据作为参数传递给下游节点。
* Custom: 自定义策略，需继承`com.didiglobal.turbo.plugin.executor.DataMergeCustom`类，重写`merge`方法，并在该类上添加`@Primary`注解。
#### 5.3 并行网关节点示例
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
#### 5.4 包容网关节点示例
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