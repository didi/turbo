
## 1.简介
LogicFlow 与 Turbo接口配合使用，用于流程配置及管理，根据前端配置流程图生成固定格式数据，这种格式可以被Turbo执行侧识别并被驱动执行


## 2 时序图
![在这里插入图片描述](http://img-ys011.didistatic.com/static/didi_opensource/do1_n9zjMcJLM5XPxXv8Hu69?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9saWRvbmcxNjY1LmJsb2cuY3Nkbi5uZXQ=,size_16,color_FFFFFF,t_70)
## 3 代码样例

### 3.1 使用LogicFlow
由于Logicflow默认的数据格式和Turbo默认的数据格式存在一定差异。所以我们需要使用TurboAdapter来实现将Logicflow的数据格式与Turbo数据格式的相互转换。TurboAdapter已经被封装为一个插件，所以直接当做插件使用即可

```
import LogicFlow from '@logicflow/core'
import { BpmnElement } from '@logicflow/extension'
import '@logicflow/core/dist/style/index.css'
import '@logicflow/extension/lib/style/index.css'
import turboAdapter from './turboAdapter'

this.lf = new LogicFlow({
  // ...
  plugins: [
    BpmnElement,
    turboAdapter,
  ]
})

```
如上源码所示：TurboAdapter默认将Turbo数据转换为bpmn节点，在实际项目中，可以完全自定义节点，而不是使用bpmn节点。具体用法请参考LogicFlow自定义节点(http://logic-flow.org/guide/basic/node.html)
插件代码以及文档地址：https://github.com/Logic-Flow/turbo-client


## 3.2 后端接口
后端提供样例接口包含创建流程接口，保存流程模型接口，发布流程接口，查询单个流程接口，查询流程列表接口，使用方可以根据自身业务特点合理编排使用。代码参考 
[FlowController](../demo/src/main/java/com/didiglobal/turbo/demo/controller/FlowController.java) 
[FlowControllerTest](../demo/src/main/test/java/com/didiglobal/turbo/demo/FlowControllerTest.java)

### 3.3 服务启动

a: 后端服务打包并启动

打包命令  mvn install;
启动命令  nohup java -jar demo-1.0.0-SNAPSHOT.jar > test.txt &;

b: 后端服务启动完成后，将前端代码中.env.development中的VITE_SERVER_ADDR地址改成后端服务地址;

c: 前端代码安装依赖并启动

安装依赖命令 npm install ;
启动命令 npm run dev;

d: 最后根据日志中提示地址访问即可



## 4 配置流程
整体配置流程为  新建流程---> 编辑流程图---> 保存流程信息--->发布流程

### 4.1 新建流程
![在这里插入图片描述](http://img-ys011.didistatic.com/static/didi_opensource/do1_EbjKEbWviwQsVceVatrP?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9saWRvbmcxNjY1LmJsb2cuY3Nkbi5uZXQ=,size_16,color_FFFFFF,t_70)

### 4.2 编辑流程图
![在这里插入图片描述](http://img-ys011.didistatic.com/static/didi_opensource/do1_DCMEWFfvHV8rX2mKP8mV?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9saWRvbmcxNjY1LmJsb2cuY3Nkbi5uZXQ=,size_16,color_FFFFFF,t_70)
用户任务节点（使用方执行任务的节点，比如需要用户提交信息）需要使用方实现。如果流程图中用到网关节点，则根据业务需要添加groovy表达式来决定流程走向，表达式中参数需要使用方传递

### 4.3 保存流程信息
![在这里插入图片描述](http://img-ys011.didistatic.com/static/didi_opensource/do1_W25efwUYGlFRwEJ6EQdb?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9saWRvbmcxNjY1LmJsb2cuY3Nkbi5uZXQ=,size_16,color_FFFFFF,t_70)

### 4.4 发布流程
![在这里插入图片描述](http://img-ys011.didistatic.com/static/didi_opensource/do1_pgrJUKQu93n4MjVjZMT1?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9saWRvbmcxNjY1LmJsb2cuY3Nkbi5uZXQ=,size_16,color_FFFFFF,t_70)


上述4步操作完成以后，流程图处于已发布状态（列表中可以根据流程名称查询流程信息），配置侧操作完成，可以驱动执行，具体样例参考
[LeaveServiceImpl](../demo/src/main/java/com/didiglobal/turbo/demo/service/LeaveServiceImpl.java)
[AfterSaleServiceImpl](../demo/src/main/java/com/didiglobal/turbo/demo/service/AfterSaleServiceImpl.java)


![在这里插入图片描述](http://img-ys011.didistatic.com/static/didi_opensource/do1_DebAsQlNb4nLbeSaM7kj?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9saWRvbmcxNjY1LmJsb2cuY3Nkbi5uZXQ=,size_16,color_FFFFFF,t_70)

