## 1.功能描述

调用活动，也被称为调用子流程，不同于内嵌子流程，调用子流程拥有独立的流程模型，
执行时会产生新的流程实例，拥有自己的动态数据存储并且与调用父流程进行数据交互。

## 2.使用场景

当一个流程图非常复杂时，可以使用调用子流程来进行流程拆解

当某个片段流程重复的出现在众多流程中时，可以使用调用子流程来进行流程拆分

## 3.示例场景

下面我们举一个流程间重复的场景来说明

在客诉场景，客服在处理某些问题时需要提前得到用户授权方可处理问题，就会导致很多的客服流程都会存在需要用户点击授权的逻辑

这个时候，我们可以把该片段提取出一个流程，然后在主流程中进行引用，后续如果授权逻辑进行升级更新，我们只需要改1份即可，有效的降低了维护成本

![avatar](https://dpubstatic.udache.com/static/dpubimg/DbhxfXlqpAeXO16zUWpfa_c_a_o_s.png)

## 4.示例代码

[CallActivityDemoRunner](demo/src/main/java/com/didiglobal/turbo/demo/runner/CallActivityDemoRunner.java)

## 5.如何扩展

本期实现了同步单实例调用子流程，如果想要实现其他类型的调用子流程，可以继承 AbstractCallActivityExecutor 来实现
