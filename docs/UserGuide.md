# 一、介绍

## 1.简介

Turbo是Didi公司开发的轻量级的Java实现的流程引擎

## 2.特性

提供“定义流程，并根据流程定义，执行流程”的核心能力

轻量级的库表操作

支持流程回滚操作

灵活支持定制化需求

# 二、模型

## 1.流程模型（FlowModel）

流程定义的内容，可以完整描述一个流程的定义，由FlowElement列表的json组成。下面是一个Demo的例子：

```
{
    "flowElementList": [
        {
            "incoming": [],
            "outgoing": [
                "SequenceFlow_19wptt1"
            ],
            "type": 2,
            "properties": {
                "name": ""
            },
            "key": "StartEvent_045vzhs"
        },
        {
            "incoming": [
                "SequenceFlow_0e9rl75"
            ],
            "outgoing": [],
            "type": 3,
            "properties": {
                "name": ""
            },
            "key": "EndEvent_15qqjlv"
        },
        {
            "incoming": [
                "SequenceFlow_19wptt1"
            ],
            "outgoing": [
                "SequenceFlow_0e9rl75"
            ],
            "type": 4,
            "properties": {
                "name": ""
            },
            "key": "UserTask_0cx5f72"
        },
        {
            "incoming": [
                "StartEvent_045vzhs"
            ],
            "outgoing": [
                "UserTask_0cx5f72"
            ],
            "type": 1,
            "properties": {
                "conditionsequenceflow": "",
                "defaultConditions": "false"
            },
            "key": "SequenceFlow_19wptt1"
        },
        {
            "incoming": [
                "UserTask_0cx5f72"
            ],
            "outgoing": [
                "EndEvent_15qqjlv"
            ],
            "type": 1,
            "properties": {
                "conditionsequenceflow": ""
            },
            "key": "SequenceFlow_0e9rl75"
        }
    ]
}
```

## 2.流程元素（FlowElement）

### 2.1 节点（FlowNode）

#### 2.1.1 事件节点（EventNode）

#### 2.1.1.1 开始节点（StartEvent）

开始节点指示流程从何处开始执行。

**构造**

```
StartEvent startEvent = new StartEvent();
// 设置唯一键
startEvent.setKey("StartEvent_1r83q1z");
startEvent.setType(FlowElementType.START_EVENT);
// 设置出口
List<String> outgoings = new ArrayList<>();
outgoings.add("SequenceFlow_0vykylt");
startEvent.setOutgoing(outgoings);
```

**属性**

| 属性 | 可选值 | 默认值 | 描述       | 示例 |
| ---- | ------ | ------ | ------------ | ---- |
| name |        |        | 节点元素名称 |      |

**约束规则**

开始节点不能有入口，出口只能有一个。

#### 2.1.1.2 结束节点（EndEvent）

结束节点指示流程从何处开始完成。

**构造**

```
EndEvent endEvent = new EndEvent();
// 设置唯一键
endEvent.setKey("EndEvent_0z30kyv");
endEvent.setType(FlowElementType.END_EVENT);
// 设置入口
List<String> incomings = new ArrayList<>();
incomings.add("SequenceFlow_1sfugjz");
endEvent.setIncoming(incomings);
```

**属性**

| 属性 | 可选值 | 默认值 | 描述       | 示例 |
| ---- | ------ | ------ | ------------ | ---- |
| name |        |        | 节点元素名称 |      |

**约束规则**

结束节点入口不能为空，不能存在出口。

#### 2.1.2 网关节点（GatewayNode）

网关节点与顺序流配合使用，用于描述顺序流的执行策略。

#### 2.1.2.1 排他网关（ExclusiveGateway）

**行为特性**

①排他网关节点支持一个出口或者多个出口。区别于用户节点，如果设置一个出口，引擎会去执行条件表达式来选择分支。如果设置多个出口，引擎会根据条件表达式来选择分支。如果配置了默认分支，当条件表达式都不成立时，引擎会选择默认分支。

⑤网关节点上可以配置“希望”刷新的数据的key的列表，并提供刷新数据的接口，默认实现为Api调用，可以按需要自定义扩展实现。当流程执行到网关节点时会先判断是否有待刷新的数据，如果存在调用该接口刷新获取指定key的数据，并与当前的全局数据合并，作为驱动流程继续执行的数据，从而实现在网关节点判断分支之前刷新数据的目的。

**构造**

```
ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
// 设置唯一键
exclusiveGateway.setKey("ExclusiveGateway_0yq2l0s");
exclusiveGateway.setType(FlowElementType.EXCLUSIVE_GATEWAY);
// 设置入口
List<String> egIncomings = new ArrayList<>();
egIncomings.add("SequenceFlow_1qonehk");
exclusiveGateway.setIncoming(egIncomings);
// 设置出口
List<String> egOutgoings = new ArrayList<>();
egOutgoings.add("SequenceFlow_15rfdft");
egOutgoings.add("SequenceFlow_1lc9xoo");
exclusiveGateway.setOutgoing(egOutgoings);
// 设置属性
Map<String, Object> properties = new HashMap<>();
// 在这里我们希望刷新订单状态，汽车状态，人员状态三个信息
properties.put("hookInfoIds", "orderStatus,carStatus,personStatus");
exclusiveGateway.setProperties(properties);
```

**属性**

| 属性      | 可选值 | 默认值 | 描述       | 示例                             |
| ----------- | ------ | ------ | ------------ | ---------------------------------- |
| name        |        |        | 节点元素名称 | 刷新状态节点                 |
| hookInfoIds | 自定义 |        | 用于刷新数据 | orderStatus,carStatus,personStatus |

**刷新数据**

| 属性   | 说明                                                                                                              |
| -------- | ------------------------------------------------------------------------------------------------------------------- |
| URL      | 在配置文件中配置hook.url，例如：http://127.0.0.1:8080/data/refresh，你需要搭建一个web容器来提供数据获取的服务 |
| Timeout  | 在配置文件中配置hook.timeout，例如：3000，就是3s超时时间                                         |
| 请求类型 | POST                                                                                                                |
| 请求参数 | {"flowInstanceId":"3705cea2-3dbe-11eb-83fa-02ef0f06e414","hookInfoParam":"orderStatus,carStatus,personStatus"}      |
| 响应参数 | {"status": 0,"data": {"orderStatus": "1","carStatus": "2","personStatus": "3"},"error": null,"detailMessage": null} |
| 备注   | 只有在status为0的时候，引擎才会认为此次请求成功，进行数据合并。                         |

**约束规则**

①排他网关节点入口必须大于等于1个，出口必须大于等于1个。

②如果排他网关节点的出口的顺序流配置了条件表达式，那么值不能为空，否则会返回相关错误信息。

③配置的默认分支不能超过1个。

#### 2.1.3 活动节点（ActivityNode）

#### 2.1.3.1 任务（Task）

#### 2.1.3.1.1 用户任务节点（UserTask）

**行为特性**

①用户节点表示使用方执行任务的节点。当流程执行到用户节点时，引擎就会挂起此次流程实例，等待使用方提交后继续执行。

②用户节点支持一个出口或者多个出口。如果设置一个出口，引擎会忽略条件表达式，直接往后执行。如果设置多个出口，引擎会根据条件表达式来选择分支。

**构造**

```
UserTask userTask = new UserTask();
// 设置唯一键
userTask.setKey("UserTask_1eglyg7");
userTask.setType(FlowElementType.USER_TASK);
// 设置入口
List<String> utIncomings = new ArrayList<>();
utIncomings.add("SequenceFlow_1o5y5z7");
userTask.setIncoming(utIncomings);
// 设置出口
List<String> utOutgoings = new ArrayList<>();
utOutgoings.add("SequenceFlow_0dttfqs");
userTask.setOutgoing(utOutgoings);
```

**属性**

| 属性 | 可选值 | 默认值 | 描述       | 示例 |
| ---- | ------ | ------ | ------------ | ---- |
| name |        |        | 节点元素名称 |      |

**约束规则**

用户节点入口必须大于等于1个，出口必须大于等于1个。

#### 2.1.3.2 内嵌子流程（SubProcess）

内嵌子流程本期暂未实现。

#### 2.1.3.3 调用子流程（CallActivity）

**行为特性**

①调用子流程会产生一个新的流程实例，父流程实例存在映射关系

②调用子流程拥有独立的数据存储，数据传递有两个方向，父传子和子传父

③调用子流程从执行方式来看，分为同步和异步

④调用子流程从创建实例数量来看，分为单实例和多实例

**构造**

```
CallActivity callActivity = new CallActivity();
// 设置唯一键
callActivity.setKey("CallActivity_0ofi5hg");
callActivity.setType(FlowElementType.CALL_ACTIVITY);
// 设置入口
List<String> caIncomings1 = new ArrayList<>();
caIncomings1.add("SequenceFlow_1udf5vg");
callActivity.setIncoming(caIncomings1);
// 设置出口
List<String> caOutgoings1 = new ArrayList<>();
caOutgoings1.add("SequenceFlow_06uq82c");
callActivity.setOutgoing(caOutgoings1);
// 设置属性
Map<String, Object> caProperties = new HashMap<>();
caProperties.put("callActivityExecuteType", Constants.CALL_ACTIVITY_EXECUTE_TYPE.SYNC); // 同步
caProperties.put("callActivityInstanceType", Constants.CALL_ACTIVITY_INSTANCE_TYPE.SINGLE); // 单实例
caProperties.put("callActivityFlowModuleId", ""); // 填充引用的流程模型id
caProperties.put("callActivityCustomId", ""); // 如果想要动态指定流程模型，则可以自定义属性，然后业务方计算出流程模型id向下提交
caProperties.put("callActivityInParamType", Constants.CALL_ACTIVITY_PARAM_TYPE.FULL); // 父传子的数据传递规则，默认全传递
caProperties.put("callActivityOutParamType", Constants.CALL_ACTIVITY_PARAM_TYPE.FULL); // 子传父的数据传递规则，默认全传递
callActivity.setProperties(caProperties);
```

**属性**

| 属性                | 可选值  | 默认值 | 描述         | 规则                                                                     |
| --------------------- | ---------- | ------ | -------------- | -------------------------------------------------------------------------- |
| name                  |            |        | 节点元素名称 |                                                                            |
| callActivityExecuteType  | sync/async |   | 调用子流程执行方式 | 调用子流程节点可以同步执行或者异步执行 |
| callActivityInstanceType | single/multiple |        | 调用子流程实例类型 | 调用子流程节点可以创建单个实例，也可以创建多个实例 |
| callActivityFlowModuleId |            |        | 调用子流程引用的流程模型id | 默认引擎执行到调用子流程节点会先上挂起，既支持配置指定执行的子流程模型，也可以支持动态执行侧指定子流程模型 |
| callActivityInParamType | none/part/full | full       | 父传子的数据传递类型 | 默认引擎会全部传递数据，既可以不传递，也可以部分传递，也可以全部传递 |
| callActivityInParam |            |        | 父传子的数据传递参数 | 见下方数据传递规则介绍 |
| callActivityOutParamType | none/part/full | full       | 子传父的数据传递类型 | 同父传子的数据传递类型 |
| callActivityOutParam |            |        | 子传父的数据传递参数 | 见下方数据传递规则介绍 |

**数据传递规则子属性**

当父子流程实例交互时，需要指定数据传递规则。一般方向分为两种，一个是父传子，一个是子传父。传值规则既支持从来源的上下文中获取，也可以自定义固定值传递。

举例：当父传子时，则父是来源 source，子是目标 target。

| 属性                | 可选值  | 默认值 | 描述         | 规则                                                                     |
| --------------------- | ---------- | ------ | -------------- | -------------------------------------------------------------------------- |
| sourceType | context/fixed |        | 数据来源类型 | 支持数据上下文中获取，支持固定值传入 |
| sourceKey  |  |   | 数据来源键 | 当数据来源类型是上下文中获取，本字段有意义 |
| sourceValue  |  |   | 数据来源值 | 当数据来源类型是固定值，本字段有意义 |
| targetKey |  |   | 数据目标键 | 将数据来源的内容赋值给新的数据key |

**约束规则**

调用子流程节点入口必须大于等于1个，出口只能等于1个。

### 2.2.顺序流（SequenceFlow）

记录节点之间的执行顺序，可以执行条件conditions，conditions在与网关节点Gateway和用户节点UserTask配合使用时生效，由Gateway或者UserTask决定conditions的执行策略。

**构造**

```
SequenceFlow sequenceFlow1 = new SequenceFlow();
// 设置唯一键
sequenceFlow1.setKey("SequenceFlow_1lc9xoo");
sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
// 设置入口
List<String> sfIncomings = new ArrayList<>();
sfIncomings.add("ExclusiveGateway_0yq2l0s");
sequenceFlow1.setIncoming(sfIncomings);
// 设置出口
List<String> sfOutgoings = new ArrayList<>();
sfOutgoings.add("EndEvent_0c82i4n");
sequenceFlow1.setOutgoing(sfOutgoings);
// 设置属性
Map<String, Object> properties = new HashMap<>();
properties.put("defaultConditions", "false");
properties.put("conditionsequenceflow", "orderId.equals(\"456\")");
sequenceFlow1.setProperties(properties);
```

**属性**

| 属性                | 可选值  | 默认值 | 描述         | 规则                                                                     |
| --------------------- | ---------- | ------ | -------------- | -------------------------------------------------------------------------- |
| name                  |            |        | 节点元素名称 |                                                                            |
| defaultConditions     | true/false | false  | 是否是默认分支 | 当所有的条件节点都不满足后，如果设置了默认分支，引擎会选择默认分支继续执行 |
| conditionsequenceflow |            |        | 条件表达式 | 默认引擎会采用Groovy来执行条件表达式，计算出true或false |

**条件表达式**

如果配置的条件表达式执行后不是返回true或者false，引擎会抛出异常。以下是示例条件表达式：

| 示例条件表达式                          | 返回值  | 描述                                              |
| ---------------------------------------------- | ---------- | --------------------------------------------------- |
| orderId.equals("456")                          | true/false | 如果没有传入orderId的值，引擎会返回缺失属性错误提示 |
| count>0                                        | true/false |                                                     |
| 0>count && count<10                            | true/false |                                                     |
| orderId.equals("123") &#124;&#124; orderId.equals("456") | true/false |                                                     |

**约束规则**

顺序流只能有一个入口和一个出口。
