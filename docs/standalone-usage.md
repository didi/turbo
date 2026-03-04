# Turbo 独立使用指南（不依赖 Spring）

> **目标读者**：新接入方，希望直接使用 Turbo 工作流引擎而**不引入 Spring 框架**。

---

## 概述

自 `turbo-core` 模块引入后，Turbo 工作流引擎的核心逻辑已完全与 Spring 解耦。
新接入方可以在任意 Java 项目中使用 `turbo-core`，无需任何 Spring 依赖。

| 场景 | 推荐依赖 |
|------|---------|
| 纯 Java / 非 Spring 项目 | `turbo-core` |
| Spring 项目 | `engine`（包含 Spring 集成）|
| Spring Boot 项目 | `turbo-spring-boot-starter` |

---

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.didiglobal.turbo</groupId>
    <artifactId>turbo-core</artifactId>
    <version>1.3.1</version>
</dependency>
```

`turbo-core` 的依赖清单中**没有任何** `spring-*` / `org.springframework.*` 依赖。

---

### 2. 实现 7 个 DAO 接口

`turbo-core` 定义了 7 个持久化接口，由你选择任意存储方案（JDBC、JPA、MyBatis、内存等）实现：

| 接口 | 对应表 | 说明 |
|------|--------|------|
| `FlowDefinitionDAO` | `em_flow_definition` | 流程定义 |
| `FlowDeploymentDAO` | `em_flow_deployment` | 流程部署记录 |
| `ProcessInstanceDAO` | `ei_flow_instance` | 流程实例 |
| `NodeInstanceDAO` | `ei_node_instance` | 节点实例 |
| `InstanceDataDAO` | `ei_instance_data` | 实例数据（流程变量）|
| `FlowInstanceMappingDAO` | `ei_flow_instance_mapping` | 子流程映射（CallActivity）|
| `NodeInstanceLogDAO` | `ei_node_instance_log` | 节点执行日志 |

建表 SQL 参见：`turbo-core/src/main/resources/turbo.db.create/turbo.mysql.sql`

#### 示例：最简内存实现

```java
// 流程定义 DAO（内存实现示例，实际请对接数据库）
class MyFlowDefinitionDAO implements FlowDefinitionDAO {
    private final Map<String, FlowDefinitionPO> store = new ConcurrentHashMap<>();

    @Override
    public int insert(FlowDefinitionPO po) {
        store.put(po.getFlowModuleId(), po);
        return 1;
    }

    @Override
    public int updateByModuleId(FlowDefinitionPO po) {
        FlowDefinitionPO existing = store.get(po.getFlowModuleId());
        if (existing == null) return 0;
        if (po.getFlowModel() != null) existing.setFlowModel(po.getFlowModel());
        if (po.getStatus() != null)    existing.setStatus(po.getStatus());
        return 1;
    }

    @Override
    public FlowDefinitionPO selectByModuleId(String flowModuleId) {
        return store.get(flowModuleId);
    }
}
```

> 参考 `turbo-core/src/test/java/.../TurboEngineStandaloneTest.java` 中的完整内存 DAO 实现。

---

### 3. 构建引擎

使用 `TurboEngineBuilder` 将 DAO 注入引擎，无需任何 IoC 容器：

```java
ProcessEngine engine = TurboEngineBuilder.create()
    .flowDefinitionDAO(new MyFlowDefinitionDAO())
    .flowDeploymentDAO(new MyFlowDeploymentDAO())
    .processInstanceDAO(new MyProcessInstanceDAO())
    .nodeInstanceDAO(new MyNodeInstanceDAO())
    .instanceDataDAO(new MyInstanceDataDAO())
    .flowInstanceMappingDAO(new MyFlowInstanceMappingDAO())
    .nodeInstanceLogDAO(new MyNodeInstanceLogDAO())
    .build();
```

如果有任何必填 DAO 未设置，`build()` 会抛出带有详细说明的 `IllegalStateException`。

#### 可选配置

```java
ProcessEngine engine = TurboEngineBuilder.create()
    // ... 必填 DAOs ...
    // 自定义 ID 生成器（默认：UUID）
    .idGenerator(new MyIdGenerator())
    // 自定义表达式计算器（默认：Groovy）
    .expressionCalculator(new MyExpressionCalculator())
    // 自定义插件管理器
    .pluginManager(new MyPluginManager())
    // 业务配置（如 CallActivity 嵌套层级限制）
    .businessConfig(myBusinessConfig)
    .build();
```

---

### 4. 完整工作流示例

```java
// ── 步骤 1：创建流程定义 ──────────────────────────────────────
CreateFlowParam createParam = new CreateFlowParam("my-tenant", "my-caller");
createParam.setFlowName("采购审批流程");
CreateFlowResult created = engine.createFlow(createParam);
String flowModuleId = created.getFlowModuleId(); // 流程唯一标识

// ── 步骤 2：设置流程模型 ──────────────────────────────────────
// 流程模型是描述节点和连线的 JSON
// 节点类型：START_EVENT=2, END_EVENT=3, USER_TASK=4,
//           SEQUENCE_FLOW=1, EXCLUSIVE_GATEWAY=6, CALL_ACTIVITY=8
String flowModelJson = buildFlowModel(); // 见下方示例

UpdateFlowParam updateParam = new UpdateFlowParam("my-tenant", "my-caller");
updateParam.setFlowModuleId(flowModuleId);
updateParam.setFlowModel(flowModelJson);
engine.updateFlow(updateParam);

// ── 步骤 3：部署流程 ──────────────────────────────────────────
DeployFlowParam deployParam = new DeployFlowParam("my-tenant", "my-caller");
deployParam.setFlowModuleId(flowModuleId);
DeployFlowResult deployed = engine.deployFlow(deployParam);
String flowDeployId = deployed.getFlowDeployId(); // 部署唯一标识

// ── 步骤 4：启动流程实例 ──────────────────────────────────────
StartProcessParam startParam = new StartProcessParam();
startParam.setFlowDeployId(flowDeployId);
// 可以传入初始变量
// startParam.setVariables(List.of(new InstanceData("key", "value")));
StartProcessResult started = engine.startProcess(startParam);

String flowInstanceId = started.getFlowInstanceId();
NodeInstance activeTask  = started.getActiveTaskInstance();
// activeTask.getModelKey() == "userTask1"（流程停在第一个人工节点）

// ── 步骤 5：提交人工节点 ──────────────────────────────────────
CommitTaskParam commitParam = new CommitTaskParam();
commitParam.setFlowInstanceId(flowInstanceId);
commitParam.setTaskInstanceId(activeTask.getNodeInstanceId());
// 可以传入表单数据
// commitParam.setVariables(List.of(new InstanceData("approved", "true")));
CommitTaskResult committed = engine.commitTask(commitParam);
// 流程继续推进，直到下一个 UserTask 或 EndEvent

// ── 步骤 6：回滚节点（可选）────────────────────────────────────
RollbackTaskParam rollbackParam = new RollbackTaskParam();
rollbackParam.setFlowInstanceId(flowInstanceId);
rollbackParam.setTaskInstanceId(activeTask.getNodeInstanceId());
engine.rollbackTask(rollbackParam);

// ── 查询 API ─────────────────────────────────────────────────
// 查历史人工节点
NodeInstanceListResult history = engine.getHistoryUserTaskList(flowInstanceId);
// 查历史所有节点
ElementInstanceListResult elements = engine.getHistoryElementList(flowInstanceId);
// 查流程变量
InstanceDataListResult data = engine.getInstanceData(flowInstanceId);
```

---

### 5. 构建流程模型 JSON

流程模型是由 `FlowElement` 列表组成的 JSON。每个元素需要设置 `key`、`type`，以及 `outgoing`（节点）/ `incoming`+`outgoing`（连线）：

```java
/**
 * 构建简单流程：StartEvent → UserTask → EndEvent
 */
private static String buildFlowModel() {
    List<FlowElement> elements = new ArrayList<>();

    // StartEvent: outgoing=[seq1]
    FlowElement start = new FlowElement();
    start.setKey("startEvent1");
    start.setType(FlowElementType.START_EVENT);
    start.setOutgoing(List.of("seq1"));
    start.setProperties(new HashMap<>());
    elements.add(start);

    // SequenceFlow seq1: startEvent1 → userTask1
    FlowElement seq1 = new FlowElement();
    seq1.setKey("seq1");
    seq1.setType(FlowElementType.SEQUENCE_FLOW);
    seq1.setIncoming(List.of("startEvent1"));
    seq1.setOutgoing(List.of("userTask1"));
    seq1.setProperties(new HashMap<>());
    elements.add(seq1);

    // UserTask: incoming=[seq1], outgoing=[seq2]
    FlowElement userTask = new FlowElement();
    userTask.setKey("userTask1");
    userTask.setType(FlowElementType.USER_TASK);
    userTask.setIncoming(List.of("seq1"));
    userTask.setOutgoing(List.of("seq2"));
    userTask.setProperties(new HashMap<>());
    elements.add(userTask);

    // SequenceFlow seq2: userTask1 → endEvent1
    FlowElement seq2 = new FlowElement();
    seq2.setKey("seq2");
    seq2.setType(FlowElementType.SEQUENCE_FLOW);
    seq2.setIncoming(List.of("userTask1"));
    seq2.setOutgoing(List.of("endEvent1"));
    seq2.setProperties(new HashMap<>());
    elements.add(seq2);

    // EndEvent: incoming=[seq2]
    FlowElement end = new FlowElement();
    end.setKey("endEvent1");
    end.setType(FlowElementType.END_EVENT);
    end.setIncoming(List.of("seq2"));
    end.setProperties(new HashMap<>());
    elements.add(end);

    FlowModel model = new FlowModel();
    model.setFlowElementList(elements);
    return JSON.toJSONString(model);
}
```

#### 排他网关（条件分支）示例

```java
// ExclusiveGateway: 根据条件选择不同路径
// 在 SequenceFlow 的 properties 中设置条件表达式：
Map<String, Object> seqProps = new HashMap<>();
seqProps.put("condition", "${amount > 1000}");  // Groovy 表达式
FlowElement conditionalSeq = new FlowElement();
conditionalSeq.setKey("seq3");
conditionalSeq.setType(FlowElementType.SEQUENCE_FLOW);
conditionalSeq.setIncoming(List.of("gateway1"));
conditionalSeq.setOutgoing(List.of("userTask2"));
conditionalSeq.setProperties(seqProps);
```

---

## 错误码说明

| 错误码范围 | 含义 |
|-----------|------|
| 1000 | 成功（`SUCCESS`）|
| 1002 | 流程挂起在 UserTask（`COMMIT_SUSPEND`）|
| 1003 | 流程挂起在 UserTask 等待回滚（`ROLLBACK_SUSPEND`）|
| 2xxx | 参数错误 |
| 3xxx | 流程定义/部署错误 |
| 4xxx | 运行时错误 |

**判断是否成功**：

```java
// 1000~1999 均视为成功（包含 COMMIT_SUSPEND、ROLLBACK_SUSPEND）
boolean ok = ErrorEnum.isSuccess(result.getErrCode());
```

---

## 与 Spring 版本的区别

| 功能 | 纯 Java（turbo-core）| Spring Boot（turbo-spring-boot-starter）|
|------|---------------------|----------------------------------------|
| 引擎初始化 | `TurboEngineBuilder.create()...build()` | `@Autowired ProcessEngine engine` |
| DAO 实现 | 自行实现 7 个接口 | MyBatis-Plus 自动实现 |
| 数据源配置 | 在 DAO 实现中处理 | `spring.datasource.*` 配置 |
| 插件 | `TurboEngineBuilder.pluginManager(...)` | Spring Bean 自动注册 |
| 表达式 | 默认 Groovy，可替换 | 默认 Groovy，可替换 |

---

## 参考

- 完整内存 DAO 实现：[`TurboEngineStandaloneTest.java`](../turbo-core/src/test/java/com/didiglobal/turbo/engine/TurboEngineStandaloneTest.java)
- DAO 接口定义：`turbo-core/src/main/java/com/didiglobal/turbo/engine/dao/`
- 建表 SQL：`turbo-core/src/main/resources/turbo.db.create/turbo.mysql.sql`
- Spring 解耦影响报告：[spring-decouple-impact.md](./spring-decouple-impact.md)
