# Turbo 简介

Turbo是一款Java实现的轻量级流程引擎

#

# 特性

1. 提供“定义流程，并根据流程定义，执行流程”的核心能力

2. 轻量级的库表操作

3. 支持流程回滚操作

#

# 关键模型

### 1 流程 (Flow)

定义了起点、终点以及起点到终点需要执行的活动、执行路径、执行策略。

### 2 流程实例 (FlowInstance)

一个流程可能会被多次执行，比如同一个场景的审批流是一个流程，每次有人提交审批这个流程都会被执行一次。流程每执行一次，对应一个流程实例。

### 3 流程元素 (FlowElement)

*考虑兼容性问题，流程元素设计参考了BPMN规范。

构成流程中的各种元素通称为流程元素 (FlowElement)，包括节点 (FlowNode)和顺序流 (SequenceFlow)。

#### 3.1 节点 (FlowNode)

##### 3.1.1 事件节点 (EventNode)

例如：
* 开始节点 (StartEvent)：标识流程的开始；
* 结束节点 (EndEvent)：标识流程的结束；

##### 3.1.2 活动节点 (ActivityNode)

例如：
* 任务 (Task)：需要处理的节点，例如：
    * 用户任务节点 (UserTask)：使用方执行任务的节点，比如需要用户提交信息；
    * 系统任务节点 (ServiceTask)：系统内部自行执行任务的节点；
* 子流程 (SubProcess)；将流程作为另一个流程的节点来处理；

##### 3.1.3 网关节点 (Gateway)

与SequenceFlow配合使用，用于描述SequenceFlow的执行策略。

例如：
* 排他网关 (ExclusiveGateway)：同一时刻的同一个实例中，根据指定输入，有且只有一条路径(SequenceFlow)被命中；

#### 3.2 顺序流 (SequenceFlow)

记录节点之间的执行顺序，可以配置执行的条件conditions（比如用户点击了“同意”作为输入），conditions只有在与网关节点Gateway配合使用时生效，由Gateway决定conditions的执行策略。

#

# 核心能力

我们提供了以下核心能力:

1. 流程定义：保存流程基本信息和流程图模型

2. 流程部署：校验和部署流程模型，记录流程模型快照。部署后流程可以执行

3. 流程执行：从开始节点开始执行，到用户节点挂起或结束节点结束

4. 流程提交：提交指定的用户任务，到下一个用户节点挂起或者结束节点结束

5. 流程回滚：从当前用户节点开始回滚，回滚至上一个用户节点或者开始节点

6. 执行轨迹追溯：查看流程实例的执行轨迹，可用于快照

# 快速开始

### 1. 配置必要信息

执行[建表语句](engine/src/main/resources/turbo.db.create/turbo.mysql.sql)，在属性文件中配置属性信息

```
# 必要属性
spring.datasource.dynamic.primary=engine
spring.datasource.dynamic.datasource.engine.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.dynamic.datasource.engine.username=username
spring.datasource.dynamic.datasource.engine.password=password
spring.datasource.dynamic.datasource.engine.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.dynamic.datasource.engine.url=jdbc:mysql://127.0.0.1:3306/db_engine

# 非必要属性
hook.url=http://127.0.0.1:8031/data/refresh
hook.timeout=3000

```

### 2. 根据demo开始你的Turbo之旅吧

# demo介绍

demo模型

![avatar](https://dpubstatic.udache.com/static/dpubimg/Uw78A6b_UY/demo.png)

步骤1

> 调用引擎的接口`starProcess`，Demo实现方法是`startProcessToUserTask1`，流程从开始节点开始执行，执行到用户节点1挂起并且返回，如下图1

```
StartProcessParam startProcessParam = EntityBuilder.buildStartProcessParam();
StartProcessResult startProcessResult = processEngine.startProcess(startProcessParam);
```

![avatar](https://dpubstatic.udache.com/static/dpubimg/n_fGkY_BM1/startProcessToUserTask1.png)

步骤2

> 调用引擎的接口`commitTask`，Demo实现方法是`commitToUserTask2`方法，我们开始提交，并且传入想要流入到用户节点2的参数，这时执行到了用户节点2挂起并且返回，如下图2

```
CommitTaskParam commitTaskParam = EntityBuilder.buildCommitTaskParam();
CommitTaskResult commitTaskResult = processEngine.commitTask(commitTaskParam);
```

![avatar](https://dpubstatic.udache.com/static/dpubimg/cj9ZSG0YMF/commitToUserTask2.png)

步骤3

> 调用引擎的接口`rollbackTask`，Demo实现方法是`rollbackToUserTask1`方法，我们开始回滚，引擎会在最后一个完成的用户节点挂起，也就是用户节点1，如下图3

```
RollbackTaskParam rollbackTaskParam = EntityBuilder.buildRollbackTaskParam();
RollbackTaskResult rollbackTaskResult = processEngine.rollbackTask(rollbackTaskParam);
```

![avatar](https://dpubstatic.udache.com/static/dpubimg/qGLvbqCiLg/rollbackToUserTask1.png)

步骤4

> 调用引擎的接口`commitTask`，Demo实现方法是`commitToUserTask3`方法，我们进行了提交，并且传入想要流入到用户节点3的参数，执行到了用户节点3挂起并且返回，如下图4

```
CommitTaskParam commitTaskParam = EntityBuilder.buildCommitTaskParam();
CommitTaskResult commitTaskResult = processEngine.commitTask(commitTaskParam);
```

![avatar](https://dpubstatic.udache.com/static/dpubimg/4NHWAJqYO7/commitToUserTask3.png)

步骤5

> 调用引擎的接口`commitTask`，Demo实现方法是`commitToEndEvent2`方法，我们进行提交，在结束节点2完成，如下图5

```
CommitTaskParam commitTaskParam = EntityBuilder.buildCommitTaskParam();
CommitTaskResult commitTaskResult = processEngine.commitTask(commitTaskParam);
```

![avatar](https://dpubstatic.udache.com/static/dpubimg/hixl2O6URT/commitToEndEvent2.png)
