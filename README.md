# Turbo 简介

Turbo是一款Java实现的轻量级流程引擎

# 特性

1. 提供“定义流程，并根据流程定义，执行流程”的核心能力

2. 轻量级的库表操作

3. 支持流程回滚操作 

# 核心能力

我们提供了以下核心能力:

1. 流程定义：保存流程基本信息和流程图模型

2. 流程部署：校验和部署流程模型，记录流程模型快照。部署后流程可以执行

3. 流程执行：从开始节点开始执行，到用户节点挂起或结束节点结束

4. 流程提交：提交指定的用户任务，到下一个用户节点挂起或者结束节点结束

5. 流程回滚：从当前用户节点开始回滚，回滚至上一个用户节点或者开始节点

6. 执行轨迹追溯：查看流程实例的执行轨迹，可用于快照  

# 为什么选择Turbo

### 1. 什么时候使用Turbo

业务支持**模块化**拆分、**有序**执行，并且有**多变**的编排诉求时，可以考虑采用Turbo作为底层引擎，同时可以配合使用 [logicflow](https://github.com/didi/LogicFlow) 进行可视化配置。

<a name="demo1">**案例1：某团购售后流程**</a>

用户A在订单列表中选择订单，判断订单状态，如果状态为未发货，则直接跳转至退款申请页，如果状态为待收货则提示不支持售后，跳转至物流信息页，如果状态为已收货，则跳转至售后页填写售后信息并提交。

![](http://img-ys011.didistatic.com/static/didi_opensource/do1_QgkUdghWFBdTaSVLWvnV)

<a name="demo2">**案例2：请假审批流程**</a>

员工A输入请假天数，判断请假天数是否大于等于3天，是的话由间接领导审批，否的话则由直属领导批准。

![](http://img-ys011.didistatic.com/static/didi_opensource/do1_fr7DzABANKQ3CLFtVtCB)

### 2. Turbo有什么不同

谈到流程引擎，当前市面上大部分是**Activiti**、**Flowable**、**Camunda**等面向OA场景，功能强大且有比较完整的生态的工作流引擎（平台），同时因为OA复杂的场景，库表关联操作非常多，但是对于其它业务场景，引擎运维以及学习成本较高，性能不可避免有一定损失，不适用于C端场景。  

同时还有部分专注于纯内存执行、无状态的流程引擎，比如阿里的**Compileflow**，这类引擎中断后不可重入，不适用于人机交互场景，适用于执行业务规则。举例：N个人去ktv唱歌，每人唱首歌，ktv消费原价为30元/人，如果总价超过300打九折，小于300按原价付款。

Turbo的定位是兼容BPMN2.0的轻量级流程引擎（而非平台），支持可重入交互，主要负责提供稳定而高效的核心能力：**流程定义**、**流程驱动**，而节点的具体执行由接入方实现，可以快速搭建面向各种场景的流程编排类系统或产品，接入简单，支持灵活扩展，引擎扩展能力通过插件或组件的形式进行补充，支持按需使用，大大降低了用户的运维以及学习成本。

### 3. 开源流程引擎对比

<table>
    <tr>
        <td></td>
        <td></td>
        <td>Activiti</td>
        <td>Camunda</td>
        <td>Compileflow</td>
        <td>turbo</td>
    </tr>
    <tr>
        <td></td>
        <td>核心表量</td>
        <td>28</td>
        <td>22</td>
        <td>0</td>
        <td>5</td>
    </tr>
    <tr>
        <td  rowspan = "3"> 特性 </td>
        <td>中断可重入</td>
        <td>√</td>
        <td>√</td>
        <td>×</td>
        <td>√</td>
    </tr>
    <tr>
        <td>支持回滚</td>
        <td>×</td>
        <td>√</td>
        <td>×</td>
        <td>√</td>
    </tr>
    <tr>
        <td>运行模式</td>
        <td>独立运行和内嵌</td>    
        <td>独立运行和内嵌</td>
        <td>内嵌</td>
        <td>内嵌</td>
    </tr>
    <tr>
        <td rowspan = "3">兼容性</td>
        <td>流程格式</td>
        <td>BPMN2.0、XPDL、PDL</td>
        <td>BPMN2.0、XPDL、PDL</td>
        <td>BPMN2.0</td>
        <td>BPMN2.0</td>
    </tr>
    <tr>
        <td>支持脚本</td>
        <td>JUEL、groovy</td>    
        <td>python、ruby、groovy、JUEL</td>
        <td>QlExpress</td>
        <td>groovy</td>
    </tr>
    <tr>
        <td>支持数据库</td>
        <td>Oracle、SQL Server、MySQL</td>    
        <td>Oracle、SQL Server、MySQL、postgre</td>
        <td>无</td>
        <td>MySQL</td>
    </tr>
</table>

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

# 快速开始

## 1.运行环境

1. JDK1.8
2. mysql

## 2.开发环境

1. JDK1.8
2. mysql
3. maven 3.1+
4. IntelliJ IDEA

## 3. 配置必要信息

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

## 4. 根据demo开始你的Turbo之旅吧

根据上文提到的turbo支持的特性，给出了两个例子，其中整体的流程如下图所示：

![](http://img-ys011.didistatic.com/static/didi_opensource/do1_nR4XrLhyuruDqj9pQtlJ)

其中与业务相关的是流程的定义和流程的执行，跟着下面的两个例子来看流程引擎的使用：

Demo1：<a href="#demo1">团购售后</a>

代码：[AfterSaleServiceImpl](demo/src/main/java/com/didiglobal/turbo/demo/service/AfterSaleServiceImpl.java)

Demo2：<a href="#demo2">请假流程</a>

代码：[LeaveServiceImpl](demo/src/main/java/com/didiglobal/turbo/demo/service/LeaveServiceImpl.java)

注：例子使用两个service是为了封装sop，在实际开发中sop是有前端页面传递进入，并非是必须再次开发。

## 5. turbo与logicFlow交互demo

使用logicFlow与turbo接口交互实现流程图创建，编辑，保存，发布功能，发布完成流程图即可参考第四部分文档，执行流程

后端代码：[LogicFlowController](demo/src/main/java/com/didiglobal/turbo/demo/logicflow/LogicFlowController.java)

前端代码：https://github.com/Logic-Flow/turbo-client

使用文档：[LogicFlowGuide](docs/LogicFlowGuide.md)

代码提供基本用法示例，使用方可以根据自己的业务场景酌情使用

