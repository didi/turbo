一、接口

1.创建流程（createFlow）

方法调用

CreateFlowResult createFlow(CreateFlowParam createFlowParam);

调用参数

| 参数名 | 参数类型 | 必填 | 参数说明 | 示例                 |
| -------- | -------- | ---- | ------------ | ---------------------- |
| tenant   | string   | 是  | 租户标识 | testTenant             |
| caller   | string   | 是  | 使用方标识 | testCaller             |
| operator | string   | 否  | 操作人    | liming                 |
| flowKey  | string   | 否  | 流程业务标识 | Pu9ZhD38               |
| flowName | string   | 否  | 模型名称 | TurboDemo              |
| remark   | string   | 否  | 备注       | 这个人很懒，什么都没写 |

返回结果

| 参数名    | 参数类型 | 参数说明       | 示例                               |
| ------------ | -------- | ------------------ | ------------------------------------ |
| errCode      | int      | 错误码，枚举见下方 | 1000                                 |
| errMsg       | string   | 错误信息       | Success                              |
| flowModuleId | string   | 模型唯一标识 | 1111abd9-2222-11eb-9c82-c66c762e3333 |

2.更新流程（updateFlow）

方法调用

UpdateFlowResult updateFlow(UpdateFlowParam updateFlowParam);

调用参数

| 参数名    | 参数类型 | 必填 | 参数说明 | 示例                               |
| ------------ | -------- | ---- | ------------ | ------------------------------------ |
| tenant       | string   | 是  | 租户标识 | testTenant                           |
| caller       | string   | 是  | 使用方标识 | testCaller                           |
| operator     | string   | 否  | 操作人    | liming                               |
| flowKey      | string   | 否  | 流程业务标识 | Pu9ZhD38                             |
| flowName     | string   | 否  | 模型名称 | TurboDemo                            |
| flowModuleId | string   | 是  | 模型唯一标识 | 1111abd9-2222-11eb-9c82-c66c762e3333 |
| flowModel    | string   | 是  | 模型内容 | 参考第二部分模型中的流程模型的Demo |
| remark       | string   | 否  | 备注       | 这个人很懒，什么都没写    |

返回结果

| 参数名 | 参数类型 | 参数说明       | 示例  |
| ------- | -------- | ------------------ | ------- |
| errCode | int      | 错误码，枚举见下方 | 1000    |
| errMsg  | string   | 错误信息       | Success |

3.部署流程（deployFlow）

方法调用

DeployFlowResult deployFlow(DeployFlowParam deployFlowParam);

方法描述

校验和部署流程。注意，只有部署成功的流程才能执行。

调用参数

| 参数名    | 参数类型 | 必填 | 参数说明 | 示例                               |
| ------------ | -------- | ---- | ------------ | ------------------------------------ |
| tenant       | string   | 是  | 租户标识 | testTenant                           |
| caller       | string   | 是  | 使用方标识 | testCaller                           |
| operator     | string   | 否  | 操作人    | liming                               |
| flowModuleId | string   | 是  | 模型唯一标识 | 1111abd9-2222-11eb-9c82-c66c762e3333 |


返回结果

| 参数名    | 参数类型 | 参数说明         | 示例                               |
| ------------ | -------- | -------------------- | ------------------------------------ |
| errCode      | int      | 错误码，枚举见下方 | 1000                                 |
| errMsg       | string   | 错误信息         | Success                              |
| flowModuleId | string   | 模型唯一标识   | 1111abd9-2222-11eb-9c82-c66c762e3333 |
| flowDeployId | string   | 模型一次部署唯一标识 | 9475abd9-3854-11eb-9c82-c66c762e7201 |

4.获取流程模型（getFlowModule）

方法调用

FlowModuleResult getFlowModule(GetFlowModuleParam getFlowModuleParam);

调用参数

| 参数名    | 参数类型 | 必填               | 参数说明                                                                              | 示例                               |
| ------------ | -------- | -------------------- | ----------------------------------------------------------------------------------------- | ------------------------------------ |
| flowDeployId | string   | 两个参数必须传入一个 | 模型一次部署唯一标识，如果同时传入2个参数，优先使用部署Id。根据部署Id在部署表中找到模型。 | 9475abd9-3854-11eb-9c82-c66c762e7201 |
| flowModuleId | string   | 两个参数必须传入一个 | 模型唯一标识，根据模型Id在定义表中找到模型。                         | 1111abd9-2222-11eb-9c82-c66c762e3333 |

返回结果

| 参数名    | 参数类型 | 参数说明       | 示例                                           |
| ------------ | -------- | ------------------ | ------------------------------------------------ |
| errCode      | int      | 错误码，枚举见下方 | 1000                                             |
| errMsg       | string   | 错误信息       | Success                                          |
| tenant       | string   | 租户标识       | testTenant                                       |
| caller       | string   | 使用方标识    | testCaller                                       |
| operator     | string   | 操作人          | liming                                           |
| flowModuleId | string   | 模型唯一标识 | 1111abd9-2222-11eb-9c82-c66c762e3333             |
| flowKey      | string   | 流程业务标识 | Pu9ZhD38                                         |
| flowName     | string   | 模型名称       | TurboDemo                                        |
| flowModel    | string   | 模型内容       | 参考第二部分模型中的流程模型的Demo |
| status       | int      | 流程模型状态 | 0：数据库默认值；1：编辑中；2：已部署；3，已下线 |
| remark       | string   | 备注             | 这个人很懒，什么都没写                |
| modifyTime   | date     | 模型修改时间 | Tue Dec 15 15:05:44 CST 2020                     |

5.流程执行（startProcess）

方法调用

StartProcessResult startProcess(StartProcessParam startProcessParam);

方法描述

创建流程实例，从开始节点开始执行，直到用户任务节点/同步单实例调用子流程节点挂起或者结束节点完成。

调用参数

| 参数名    | 参数类型       | 必填               | 参数说明                                                                                     | 示例                                                                                              |
| ------------ | ------------------ | -------------------- | ------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------- |
| flowDeployId | string             | 两个参数必须传入一个 | 模型一次部署唯一标识，如果同时传入2个参数，优先使用部署Id。根据部署Id在部署表中找到模型。 | 9475abd9-3854-11eb-9c82-c66c762e7201                                                                |
| flowModuleId | string             | 两个参数必须传入一个 | 模型唯一标识，根据模型Id在部署表中找到该流程最新部署的流程，如果找不到，引擎会返回相应错误信息。 | 1111abd9-2222-11eb-9c82-c66c762e3333                                                                |
| variables    | List<InstanceData> | 否                  | 流程执行过程中所需的数据，例如条件表达式                                     | List<InstanceData> variables = new ArrayList<>();variables.add(new InstanceData("orderId", "123")); |

返回结果

| 参数名                         | 参数类型       | 参数说明                                                   | 示例                                                                                                                   |
| --------------------------------- | ------------------ | -------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------ |
| errCode                           | int                | 错误码，枚举见下方                                    | 1002                                                                                                                     |
| errMsg                            | string             | 错误信息                                                   | Commit task suspend                                                                                                      |
| flowInstanceId                    | string             | 流程实例唯一标识，后面的各种操作都会需要到该参数。 | ce0bae96-b996-47fa-a620-fed637ca0e81                                                                                     |
| status                            | int                | 流程实例状态                                             | 0：数据库默认值；1：执行完成；2：执行中；3：已终止                                                |
| activeTaskInstance                | NodeInstance对象 | 活动节点实例信息，例如当前挂起的用户任务节点，完成时的结束节点 |                                                                                                                          |
| activeTaskInstance.nodeInstanceId | string             | 节点实例唯一标识                                       | 0ce7544e-0dee-4975-85de-5bc14ad90d94                                                                                     |
| activeTaskInstance.flowElementType| int             | 节点类型                                       | 8                                                                                     |
| activeTaskInstance.subNodeResultList | List<RuntimeResult>             | 子流程实例结果                                       | 0ce7544e-0dee-4975-85de-5bc14ad90d94                                                                                     |
| activeTaskInstance.status         | int                | 节点实例状态                                             | 0：数据库默认值；1：处理成功；2：处理中；3：处理失败；4：处理已撤销；                    |
| activeTaskInstance.modelKey       | string             | 节点唯一标识                                             | UserTask_0cx5f72                                                                                                         |
| activeTaskInstance.modelName      | string             | 节点名称                                                   | 用户节点                                                                                                             |
| activeTaskInstance.properties     | map                | 属性信息                                                   | 参考第二部分模型中的流程模型的Demo中的节点的属性信息                                             |
| activeTaskInstance.createTime     | date               | 节点实例创建时间                                       | Tue Dec 15 15:05:44 CST 2020                                                                                             |
| activeTaskInstance.modifyTime     | date               | 节点实例修改时间                                       | Tue Dec 15 15:05:44 CST 2020                                                                                             |
| flowModuleId                      | string             | 模型唯一标识                                             | 1111abd9-2222-11eb-9c82-c66c762e3333                                                                                     |
| flowDeployId                      | string             | 模型一次部署唯一标识                                 | 9475abd9-3854-11eb-9c82-c66c762e7201                                                                                     |
| variables                         | List<InstanceData> | 最新的数据                                                | 如果不存在排他网关节点刷新数据，那引擎返回的是原样数据。如果存在排他网关节点刷新数据，那引擎返回的是合并后的最新的数据。 |


6.提交任务（commitTask）

方法调用

CommitTaskResult commitTask(CommitTaskParam commitTaskParam);

方法描述

引擎从指定的用户任务节点/同步单实例调用子流程节点开始执行，直到用户任务节点/同步单实例调用子流程节点挂起或者结束节点完成。

调用参数

| 参数名      | 参数类型       | 必填 | 参数说明                                                            | 示例                                                                                              |
| -------------- | ------------------ | ---- | ----------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------- |
| flowInstanceId | string             | 是  | 流程实例唯一标识                                                | ce0bae96-b996-47fa-a620-fed637ca0e81                                                                |
| taskInstanceId | string             | 是  | 节点实例唯一标识，可以从上面的activeTaskInstance.nodeInstanceId中获取。 | 0ce7544e-0dee-4975-85de-5bc14ad90d94                                                                |
| variables      | List<InstanceData> | 否  | 流程执行过程中所需的数据，例如条件表达式            | List<InstanceData> variables = new ArrayList<>();variables.add(new InstanceData("orderId", "456")); |
| callActivityFlowModuleId | string | 否  | 用于在提交CallActivity节点时指定FlowModuleId            |  |

返回结果

| 参数名          | 参数类型       | 参数说明                                                   | 示例                                                                                                                                  |
| ------------------ | ------------------ | -------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------- |
| errCode            | int                | 错误码，枚举见下方                                    | 1002                                                                                                                                    |
| errMsg             | string             | 错误信息                                                   | Commit task suspend                                                                                                                     |
| flowInstanceId     | string             | 流程实例唯一标识                                       | ce0bae96-b996-47fa-a620-fed637ca0e81                                                                                                    |
| status             | int                | 流程实例状态                                             | 0：数据库默认值；1：执行完成；2：执行中；3：已终止                                                               |
| activeTaskInstance | NodeInstance对象 | 活动节点实例信息，例如当前挂起的用户任务节点，完成时的结束节点 | NodeInstance的结构见上面                                                                                                          |
| variables          | List<InstanceData> | 最新的数据                                                | 提交时如果存在相同的key的数据，那引擎会覆盖旧有数据，最后返回最新数据。如果存在排他网关节点刷新数据，那引擎返回的是合并后的最新的数据。 |

7.回滚任务（rollbackTask）

方法调用

RollbackTaskResult rollbackTask(RollbackTaskParam rollbackTaskParam);

方法描述

引擎从指定的用户任务节点开始回滚，直到用户任务节点挂起或者开始节点结束。

调用参数

| 参数名      | 参数类型 | 必填 | 参数说明                                                            | 示例                               |
| -------------- | -------- | ---- | ----------------------------------------------------------------------- | ------------------------------------ |
| flowInstanceId | string   | 是  | 流程实例唯一标识                                                | ce0bae96-b996-47fa-a620-fed637ca0e81 |
| taskInstanceId | string   | 是  | 节点实例唯一标识，可以从上面的activeTaskInstance.nodeInstanceId中获取。 | 0ce7544e-0dee-4975-85de-5bc14ad90d94 |

返回结果

| 参数名          | 参数类型       | 参数说明                                             | 示例                                             |
| ------------------ | ------------------ | -------------------------------------------------------- | -------------------------------------------------- |
| errCode            | int                | 错误码，枚举见下方                              | 1002                                               |
| errMsg             | string             | 错误信息                                             | Commit task suspend                                |
| flowInstanceId     | string             | 流程实例唯一标识                                 | ce0bae96-b996-47fa-a620-fed637ca0e81               |
| status             | int                | 活动节点实例信息，例如当前挂起的用户任务节点或者开始节点 | 0：数据库默认值；1：执行完成；2：执行中；3：已终止 |
| activeTaskInstance | NodeInstance对象 | 活动节点实例信息                                 | NodeInstance的结构见上面                     |
| variables          | List<InstanceData> | 当时执行到这个节点的数据                     | 节点的回滚伴随着节点实例数据的回滚 |

8.终止流程（terminateProcess）

方法调用

TerminateResult terminateProcess(String flowInstanceId); // 默认对子流程实例生效

TerminateResult terminateProcess(String flowInstanceId, boolean effectiveForSubFlowInstance); // 是否对子流程实例生效

方法描述

强制终止流程实例，如果当前流程实例状态是已完成，引擎什么也不做，否则引擎会将状态置为已终止。注意，一旦流程已完成或者已终止，引擎将不再允许提交和回滚。

调用参数

| 参数名      | 参数类型 | 必填 | 参数说明     | 示例                               |
| -------------- | -------- | ---- | ---------------- | ------------------------------------ |
| flowInstanceId | string   | 是  | 流程实例唯一标识 | ce0bae96-b996-47fa-a620-fed637ca0e81 |

返回结果

| 参数名      | 参数类型 | 参数说明       | 示例                                             |
| -------------- | -------- | ------------------ | -------------------------------------------------- |
| errCode        | int      | 错误码，枚举见下方 | 1000                                               |
| errMsg         | string   | 错误信息       | Success                                            |
| flowInstanceId | string   | 流程实例唯一标识 | ce0bae96-b996-47fa-a620-fed637ca0e81               |
| status         | int      | 流程实例状态 | 0：数据库默认值；1：执行完成；2：执行中；3：已终止 |

9.获取历史用户节点（getHistoryUserTaskList）

方法调用

NodeInstanceListResult getHistoryUserTaskList(String flowInstanceId); // 默认对子流程实例生效

NodeInstanceListResult getHistoryUserTaskList(String flowInstanceId, boolean effectiveForSubFlowInstance); // 是否对子流程实例生效

方法描述

获取指定的流程实例中执行过的用户任务节点，并且以倒序的方式返回。

调用参数

| 参数名      | 参数类型 | 必填 | 参数说明     | 示例                               |
| -------------- | -------- | ---- | ---------------- | ------------------------------------ |
| flowInstanceId | string   | 是  | 流程实例唯一标识 | ce0bae96-b996-47fa-a620-fed637ca0e81 |

返回结果

| 参数名        | 参数类型       | 参数说明       | 示例                   |
| ---------------- | ------------------ | ------------------ | ------------------------ |
| errCode          | int                | 错误码，枚举见下方 | 1000                     |
| errMsg           | string             | 错误信息       | Success                  |
| nodeInstanceList | List<NodeInstance> | 用户节点实例列表 | NodeInstance的结构见上面 |

10.获取历史所有节点（getHistoryElementList）

方法调用

ElementInstanceListResult getHistoryElementList(String flowInstanceId); // 默认对子流程实例生效

ElementInstanceListResult getHistoryElementList(String flowInstanceId, boolean effectiveForSubFlowInstance); // 是否对子流程实例生效

方法描述

获取指定的流程实例中执行过的所有节点，并且以正序的方式返回。因为引擎在执行过程中不存储顺序流节点，所以引擎会计算两个节点之间的顺序流节点，然后添加到正确的位置上。

调用参数

| 参数名      | 参数类型 | 必填 | 参数说明     | 示例                               |
| -------------- | -------- | ---- | ---------------- | ------------------------------------ |
| flowInstanceId | string   | 是  | 流程实例唯一标识 | ce0bae96-b996-47fa-a620-fed637ca0e81 |

返回结果

| 参数名                  | 参数类型          | 参数说明       | 示例                                                                |
| -------------------------- | --------------------- | ------------------ | --------------------------------------------------------------------- |
| errCode                    | int                   | 错误码，枚举见下方 | 1000                                                                  |
| errMsg                     | string                | 错误信息       | Success                                                               |
| elementInstanceList        | List<ElementInstance> | 用户节点实例列表 |                                                                       |
| ElementInstance.modelKey   | string                | 节点唯一标识 | UserTask_rtx1sde                                                      |
| ElementInstance.modelName  | string                | 节点名称       | 用户节点                                                          |
| ElementInstance.properties | map                   | 属性信息       | 参考第二部分模型中的流程模型的Demo中的节点的属性信息 |
| ElementInstance.status     | int                   | 节点实例状态 | 0：数据库默认值；1：处理成功；2：处理中；3：处理失败；4：处理已撤销； |

11.获取流程实例数据（getInstanceData）

方法调用

InstanceDataListResult getInstanceData(String flowInstanceId); // 默认对子流程实例生效

InstanceDataListResult getInstanceData(String flowInstanceId, boolean effectiveForSubFlowInstance); // 是否对子流程实例生效

方法描述

获取指定的流程实例的最新的数据信息。

调用参数

| 参数名      | 参数类型 | 必填 | 参数说明     | 示例                               |
| -------------- | -------- | ---- | ---------------- | ------------------------------------ |
| flowInstanceId | string   | 是  | 流程实例唯一标识 | ce0bae96-b996-47fa-a620-fed637ca0e81 |

返回结果

| 参数名 | 参数类型       | 参数说明       | 示例                   |
| --------- | ------------------ | ------------------ | ------------------------ |
| errCode   | int                | 错误码，枚举见下方 | 1000                     |
| errMsg    | string             | 错误信息       | Success                  |
| variables | List<InstanceData> | 数据列表       | InstanceData的结构见上面 |

12.获取节点实例信息（getNodeInstance）

方法调用

NodeInstanceResult getNodeInstance(String flowInstanceId, String nodeInstanceId); // 默认对子流程实例生效

NodeInstanceResult getNodeInstance(String flowInstanceId, String nodeInstanceId, boolean effectiveForSubFlowInstance); // 是否对子流程实例生效

调用参数

| 参数名      | 参数类型 | 必填 | 参数说明                                                            | 示例                               |
| -------------- | -------- | ---- | ----------------------------------------------------------------------- | ------------------------------------ |
| flowInstanceId | string   | 是  | 流程实例唯一标识                                                | ce0bae96-b996-47fa-a620-fed637ca0e81 |
| nodeInstanceId | string   | 是  | 节点实例唯一标识，可以从上面的activeTaskInstance.nodeInstanceId中获取。 | 0ce7544e-0dee-4975-85de-5bc14ad90d94 |

返回结果

| 参数名    | 参数类型 | 参数说明       | 示例                   |
| ------------ | ------------ | ------------------ | ------------------------ |
| errCode      | int          | 错误码，枚举见下方 | 1000                     |
| errMsg       | string       | 错误信息       | Success                  |
| nodeInstance | NodeInstance | 节点实例信息 | NodeInstance的结构见上面 |

13.获取流程实例信息（getFlowInstance）

方法调用

FlowInstanceResult getFlowInstance(String flowInstanceId);

调用参数

| 参数名      | 参数类型 | 必填 | 参数说明                                                            | 示例                               |
| -------------- | -------- | ---- | ----------------------------------------------------------------------- | ------------------------------------ |
| flowInstanceId | string   | 是  | 流程实例唯一标识                                                | ce0bae96-b996-47fa-a620-fed637ca0e81 |

返回结果

| 参数名    | 参数类型 | 参数说明       | 示例                   |
| ------------ | ------------ | ------------------ | ------------------------ |
| errCode      | int          | 错误码，枚举见下方 | 1000                     |
| errMsg       | string       | 错误信息       | Success                  |
| flowInstanceBO | FlowInstanceBO | 流程实例信息 |  |

四、错误码

1.非阻断性错误码，范围在1000~1999

| 错误码 | 错误码信息       | 中文描述 |
| ------ | --------------------- | ---------- |
| 1000   | Success               | 处理成功 |
| 1001   | Reentrant warning     | 重复处理 |
| 1002   | Commit task suspend   | 任务待提交 |
| 1003   | Rollback task suspend | 任务待撤销 |

2.通用业务错误码，范围在2000~2999

| 错误码 | 错误码信息 | 中文描述 |
| ------ | ------------- | -------- |
| 2001   | Invalid param | 参数错误 |
| 2002   | Flow nested level exceeded | 超出配置的流程嵌套层数 |
| 2003   | Flow nested dead loop | 流程嵌套死循环 |

3.流程定义错误码，范围在3000~3999

| 错误码 | 错误码信息                    | 中文描述                                           |
| ------ | ---------------------------------- | ------------------------------------------------------ |
| 3001   | Definition insert failed           | 数据库插入失败                                  |
| 3002   | Definition update failed           | 数据库更新失败                                  |
| 3101   | Flow not exist                     | 流程不存在                                        |
| 3102   | Flow not editing status            | 流程不是编辑状态                               |
| 3201   | Empty model                        | 模型为空                                           |
| 3202   | Zero or more than one start node   | 流程必须有且仅有一个开始节点             |
| 3203   | Element key not unique             | 流程元素key必须唯一                            |
| 3204   | No end node                        | 流程至少需要有一个结束节点                |
| 3205   | Not unicom                         | 该流程从开始节点不能到底每一个节点    |
| 3206   | Sequence belong to multi pair node | 边应该属于一个入口节点和一个出口节点 |
| 3207   | Ring wrong                         | 流程中环结构中必须至少包含一个用户节点 |
| 3208   | Gateway no outgoing                | 网关节点应该至少有一条出口                |
| 3209   | Empty sequence outgoing            | 网关节点条件分支除默认分支外均需要配置条件表达式 |
| 3210   | Too many default sequence          | 网关节点最多只能有一条默认分支          |
| 3211   | Unknown element key                | 不支持该类型                                     |
| 3212   | Too many incoming                  | 非条件判断节点以及起始节点外，其他节点有且仅有一个出口 |
| 3213   | Too many outgoing                  | 太多的出口分支                                  |
| 3214   | Element lack incoming              | 元素缺少入口                                     |
| 3215   | Element lack outgoing              | 元素缺少出口                                     |
| 3216   | Required element attributes        | 缺少节点必要的属性                                     |
| 3217   | Unknown element value              | 不识别的流程属性值                                     |

4.流程执行错误码，范围在4000~4999

| 错误码 | 错误码信息                                      | 中文描述                             |
| ------ | ---------------------------------------------------- | ---------------------------------------- |
| 4001   | Commit task failed                                   | 提交失败                             |
| 4002   | Rollback task failed                                 | 撤销失败                             |
| 4003   | Commit rejected, flow is terminate                   | 流程实例已终止, 无法继续提交 |
| 4004   | Rollback rejected, non-running flowInstance rollback | 非执行中流程实例, 无法撤销   |
| 4005   | No node to rollback                                  | 该实例没有可撤销的节点        |
| 4006   | No userTask to rollback                              | 已完成第一个用户节点的撤销, 无法继续撤销 |
| 4007   | Get flowDeployment failed                            | 获取不到流程部署信息           |
| 4008   | Get flowInstance failed                              | 获取不到流程实例信息           |
| 4009   | Get current node failed                              | 获取不到待处理的节点           |
| 4010   | Get nodeInstance failed                              | 获取不到节点实例信息           |
| 4011   | Get instanceData failed                              | 获取不到实例数据信息           |
| 4012   | Get hook config failed                               | 获取不到hook配置                   |
| 4013   | Get outgoing failed                                  | 找不到下一个待执行节点        |
| 4014   | Unsupported element type                             | 不支持的节点操作                 |
| 4015   | Miss data                                            | 表达式运算缺少数据              |
| 4016   | Save flowInstance failed                             | 保存流程实例失败                 |
| 4017   | Save instanceData failed                             | 保存实例数据失败                 |
| 4018   | Groovy calculate failed                              | 表达式执行失败                    |
| 4019   | Get CallActivity model failed                        | 获取调用子流程模型失败                 |
| 4020   | Do not receive subFlowInstanceId                     | 不接收提交子流程实例                    |

5.系统错误码，范围在5000~5999

| 错误码 | 错误码信息 | 中文描述 |
| ------ | ------------ | -------- |
| 5000   | System error | 系统错误 |
| 5001   | Failed       | 处理失败 |
