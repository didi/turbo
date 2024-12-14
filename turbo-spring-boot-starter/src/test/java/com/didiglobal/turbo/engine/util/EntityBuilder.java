package com.didiglobal.turbo.engine.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didiglobal.turbo.engine.bo.NodeInstanceBO;
import com.didiglobal.turbo.engine.common.*;
import com.didiglobal.turbo.engine.entity.*;
import com.didiglobal.turbo.engine.model.*;
import com.didiglobal.turbo.engine.param.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.*;

public class EntityBuilder {
    private static long suffix = System.currentTimeMillis();
    private static String flowName = "testFlowName_" + suffix;
    private static String flowKey = "testFlowKey_" + suffix;
    private static String flowModuleId = "testFlowModuleId_" + suffix;
    private static String flowDeployId = "testFlowDeployId_" + suffix;
    private static String flowInstanceId = "testFlowInstanceId_" + suffix;
    private static String nodeInstanceId = "testNodeInstanceId_" + suffix;
    private static String instanceDataId = "testInstanceDataId_" + suffix;
    private static String sourceNodeInstanceId = "testSourceNodeInstanceId_" + suffix;
    private static String nodeKey = "testNodeKey";
    private static String sourceNodeKey = "testSourceNodeKey";
    private static String operator = "testOperator";
    private static String remark = "testRemark";

    public static FlowDefinitionPO buildFlowDefinitionPO() {
        FlowDefinitionPO flowDefinitionPO = new FlowDefinitionPO();
        flowDefinitionPO.setFlowKey(flowKey);
        flowDefinitionPO.setFlowModuleId(flowModuleId);
        flowDefinitionPO.setFlowModel(JSON.toJSONString(buildFlowElementList()));
        flowDefinitionPO.setStatus(FlowDefinitionStatus.INIT);
        flowDefinitionPO.setCreateTime(new Date());
        flowDefinitionPO.setModifyTime(new Date());
        flowDefinitionPO.setOperator(operator);
        flowDefinitionPO.setRemark(remark);
        return flowDefinitionPO;
    }

    public static FlowDeploymentPO buildFlowDeploymentPO() {
        FlowDeploymentPO flowDeploymentPO = new FlowDeploymentPO();
        flowDeploymentPO.setFlowName(flowName);
        flowDeploymentPO.setFlowKey(flowKey);
        flowDeploymentPO.setFlowModuleId(flowModuleId);
        flowDeploymentPO.setFlowDeployId(flowDeployId);
        flowDeploymentPO.setFlowModel(JSON.toJSONString(buildFlowElementList()));
        flowDeploymentPO.setStatus(FlowDeploymentStatus.DEPLOYED);
        flowDeploymentPO.setCreateTime(new Date());
        flowDeploymentPO.setModifyTime(new Date());
        flowDeploymentPO.setOperator(operator);
        flowDeploymentPO.setRemark(remark);
        return flowDeploymentPO;
    }

    public static List<FlowElement> buildFlowElementList() {
        List<FlowElement> flowElementList = Lists.newArrayList();

        //startEvent1
        StartEvent startEvent = new StartEvent();
        startEvent.setKey("startEvent1");
        startEvent.setType(FlowElementType.START_EVENT);
        List<String> seOutgoings = new ArrayList<>();
        seOutgoings.add("sequenceFlow1");
        startEvent.setOutgoing(seOutgoings);
        flowElementList.add(startEvent);

        //sequenceFlow1
        SequenceFlow sequenceFlow1 = new SequenceFlow();
        sequenceFlow1.setKey("sequenceFlow1");
        sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings = new ArrayList<>();
        sfIncomings.add("startEvent1");
        sequenceFlow1.setIncoming(sfIncomings);
        List<String> sfOutgoings = new ArrayList<>();
        sfOutgoings.add("userTask1");
        sequenceFlow1.setOutgoing(sfOutgoings);
        flowElementList.add(sequenceFlow1);

        //userTask1
        UserTask userTask = new UserTask();
        userTask.setKey("userTask1");
        userTask.setType(FlowElementType.USER_TASK);

        List<String> utIncomings = new ArrayList<>();
        utIncomings.add("sequenceFlow1");
        userTask.setIncoming(utIncomings);

        List<String> utOutgoings = new ArrayList<>();
        utOutgoings.add("sequenceFlow2");
        userTask.setOutgoing(utOutgoings);

        flowElementList.add(userTask);

        //sequenceFlow2
        SequenceFlow sequenceFlow2 = new SequenceFlow();
        sequenceFlow2.setKey("sequenceFlow2");
        sequenceFlow2.setType(FlowElementType.SEQUENCE_FLOW);

        List<String> sfIncomings2 = new ArrayList<>();
        sfIncomings2.add("userTask1");
        sequenceFlow2.setIncoming(sfIncomings2);

        List<String> sfOutgoings2 = new ArrayList<>();
        sfOutgoings2.add("exclusiveGateway1");
        sequenceFlow2.setOutgoing(sfOutgoings2);

        flowElementList.add(sequenceFlow2);

        //exclusiveGateway1
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setKey("exclusiveGateway1");
        exclusiveGateway.setType(FlowElementType.EXCLUSIVE_GATEWAY);

        Map<String, Object> properties = Maps.newHashMap();
        properties.put(Constants.ELEMENT_PROPERTIES.HOOK_INFO_IDS, "[1,2]");
        exclusiveGateway.setProperties(properties);

        List<String> egIncomings = new ArrayList<>();
        egIncomings.add("sequenceFlow2");
        exclusiveGateway.setIncoming(egIncomings);

        List<String> egOutgoings = new ArrayList<>();
        egOutgoings.add("sequenceFlow3");
        egOutgoings.add("sequenceFlow4");
        exclusiveGateway.setOutgoing(egOutgoings);

        flowElementList.add(exclusiveGateway);

        //sequenceFlow3
        SequenceFlow sequenceFlow3 = new SequenceFlow();
        sequenceFlow3.setKey("sequenceFlow3");
        sequenceFlow3.setType(FlowElementType.SEQUENCE_FLOW);
        Map<String, Object> sFproperties3 = Maps.newHashMap();
        sFproperties3.put(Constants.ELEMENT_PROPERTIES.CONDITION, "a>1&&b==1");
        sequenceFlow3.setProperties(sFproperties3);

        List<String> sfIncomings3 = new ArrayList<>();
        sfIncomings3.add("exclusiveGateway1");
        sequenceFlow3.setIncoming(sfIncomings3);

        List<String> sfOutgoings3 = new ArrayList<>();
        sfOutgoings3.add("userTask2");
        sequenceFlow3.setOutgoing(sfOutgoings3);

        flowElementList.add(sequenceFlow3);

        //sequenceFlow4
        SequenceFlow sequenceFlow4 = new SequenceFlow();
        sequenceFlow4.setKey("sequenceFlow4");
        sequenceFlow4.setType(FlowElementType.SEQUENCE_FLOW);

        Map<String, Object> sFproperties4 = Maps.newHashMap();
        sFproperties4.put(Constants.ELEMENT_PROPERTIES.DEFAULT_CONDITION, "true");
        sequenceFlow4.setProperties(sFproperties4);


        List<String> sfIncomings4 = new ArrayList<>();
        sfIncomings4.add("exclusiveGateway1");
        sequenceFlow4.setIncoming(sfIncomings4);

        List<String> sfOutgoings4 = new ArrayList<>();
        sfOutgoings4.add("userTask3");
        sequenceFlow4.setOutgoing(sfOutgoings4);

        flowElementList.add(sequenceFlow4);

        //userTask2
        UserTask userTask2 = new UserTask();
        userTask2.setKey("userTask2");
        userTask2.setType(FlowElementType.USER_TASK);

        List<String> utIncomings2 = new ArrayList<>();
        utIncomings2.add("sequenceFlow3");
        userTask2.setIncoming(utIncomings2);

        List<String> utOutgoings2 = new ArrayList<>();
        utOutgoings2.add("sequenceFlow5");
        userTask2.setOutgoing(utOutgoings2);

        flowElementList.add(userTask2);

        //userTask3
        UserTask userTask3 = new UserTask();
        userTask3.setKey("userTask3");
        userTask3.setType(FlowElementType.USER_TASK);

        List<String> utIncomings3 = new ArrayList<>();
        utIncomings3.add("sequenceFlow4");
        userTask3.setIncoming(utIncomings3);

        List<String> utOutgoings3 = new ArrayList<>();
        utOutgoings3.add("sequenceFlow6");
        userTask3.setOutgoing(utOutgoings3);

        flowElementList.add(userTask3);

        //sequenceFlow5
        SequenceFlow sequenceFlow5 = new SequenceFlow();
        sequenceFlow5.setKey("sequenceFlow5");
        sequenceFlow5.setType(FlowElementType.SEQUENCE_FLOW);

        List<String> sfIncomings5 = new ArrayList<>();
        sfIncomings5.add("userTask2");
        sequenceFlow5.setIncoming(sfIncomings5);

        List<String> sfOutgoings5 = new ArrayList<>();
        sfOutgoings5.add("endEvent1");
        sequenceFlow5.setOutgoing(sfOutgoings5);

        flowElementList.add(sequenceFlow5);

        //sequenceFlow6
        SequenceFlow sequenceFlow6 = new SequenceFlow();
        sequenceFlow6.setKey("sequenceFlow6");
        sequenceFlow6.setType(FlowElementType.SEQUENCE_FLOW);

        List<String> sfIncomings6 = new ArrayList<>();
        sfIncomings6.add("userTask3");
        sequenceFlow6.setIncoming(sfIncomings6);

        List<String> sfOutgoings6 = new ArrayList<>();
        sfOutgoings6.add("endEvent2");
        sequenceFlow6.setOutgoing(sfOutgoings6);

        flowElementList.add(sequenceFlow6);

        //endEvent1
        EndEvent endEven1 = new EndEvent();
        endEven1.setKey("endEvent1");
        endEven1.setType(FlowElementType.END_EVENT);

        List<String> eeIncomings1 = new ArrayList<>();
        eeIncomings1.add("sequenceFlow5");
        endEven1.setIncoming(eeIncomings1);

        flowElementList.add(endEven1);

        //endEvent2
        EndEvent endEvent2 = new EndEvent();
        endEvent2.setKey("endEvent2");
        endEvent2.setType(FlowElementType.END_EVENT);

        List<String> eeIncomings2 = new ArrayList<>();
        eeIncomings2.add("sequenceFlow6");
        endEvent2.setIncoming(eeIncomings2);

        flowElementList.add(endEvent2);
        return flowElementList;
    }

    public static FlowElement buildStartEvent() {
        StartEvent startEvent = new StartEvent();
        startEvent.setKey("startEvent1");
        startEvent.setType(FlowElementType.START_EVENT);
        List<String> seOutgoings = new ArrayList<>();
        seOutgoings.add("sequenceFlow1");
        startEvent.setOutgoing(seOutgoings);
        return startEvent;
    }
    /*public static FlowElement buildStartEventInvalid() {
        StartEvent startEvent = new StartEvent();
        startEvent.setKey("startEvent1");
        startEvent.setType(FlowElementType.START_EVENT);
        List<String> seOutgoings = new ArrayList<>();
        seOutgoings.add("sequenceFlow1");
        startEvent.setOutgoing(seOutgoings);
        List<String> setIncomings = new ArrayList<>();
        setIncomings.add("sequenceFlow2");
        startEvent.setIncoming(seOutgoings);
        return startEvent;
    }*/

    public static FlowElement buildEndEvent() {
        EndEvent endEvent = new EndEvent();
        endEvent.setKey("endEvent1");
        endEvent.setType(FlowElementType.END_EVENT);
        List<String> eeIncomings1 = new ArrayList<>();
        eeIncomings1.add("sequenceFlow");
        endEvent.setIncoming(eeIncomings1);
        return endEvent;
    }

    public static FlowElement buildSequenceFlow() {
        FlowElement sequenceFlow = new SequenceFlow();
        sequenceFlow.setKey("sequenceFlow5");
        sequenceFlow.setType(FlowElementType.SEQUENCE_FLOW);

        List<String> sfIncomings = new ArrayList<>();
        sfIncomings.add("userTask2");
        sequenceFlow.setIncoming(sfIncomings);

        List<String> sfOutgoings = new ArrayList<>();
        sfOutgoings.add("endEvent1");
        sequenceFlow.setOutgoing(sfOutgoings);
        return sequenceFlow;
    }

    public static ExclusiveGateway buildExclusiveGateway() {
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();

        exclusiveGateway.setKey("exclusiveGateway1");
        exclusiveGateway.setType(FlowElementType.EXCLUSIVE_GATEWAY);

        List<String> egIncomings = new ArrayList<>();
        egIncomings.add("sequenceFlow1");
        exclusiveGateway.setIncoming(egIncomings);

        List<String> egOutgoings = new ArrayList<>();
        egOutgoings.add("sequenceFlow2");
        egOutgoings.add("sequenceFlow3");
        exclusiveGateway.setOutgoing(egOutgoings);
        return exclusiveGateway;
    }

    public static FlowElement buildSequenceFlow2() {
        FlowElement sequenceFlow = new SequenceFlow();
        sequenceFlow.setKey("sequenceFlow2");
        sequenceFlow.setType(FlowElementType.SEQUENCE_FLOW);

        List<String> sfIncomings = new ArrayList<>();
        sfIncomings.add("exclusiveGateway1");
        sequenceFlow.setIncoming(sfIncomings);

        List<String> sfOutgoings = new ArrayList<>();
        sfOutgoings.add("endEvent1");
        sequenceFlow.setOutgoing(sfOutgoings);
        Map<String, Object> map = new HashMap<>();
        map.put("defaultConditions", "false");
        map.put("conditionsequenceflow", "${(orderId>1)}");
        sequenceFlow.setProperties(map);
        return sequenceFlow;
    }

    public static FlowElement buildSequenceFlow3() {
        FlowElement sequenceFlow = new SequenceFlow();
        sequenceFlow.setKey("sequenceFlow3");
        sequenceFlow.setType(FlowElementType.SEQUENCE_FLOW);

        List<String> sfIncomings = new ArrayList<>();
        sfIncomings.add("exclusiveGateway1");
        sequenceFlow.setIncoming(sfIncomings);

        List<String> sfOutgoings = new ArrayList<>();
        sfOutgoings.add("endEvent2");
        sequenceFlow.setOutgoing(sfOutgoings);
        Map<String, Object> map = new HashMap<>();
        map.put("defaultConditions", "false");
        map.put("conditionsequenceflow", "${(orderId<1)}");
        sequenceFlow.setProperties(map);
        return sequenceFlow;
    }

    public static FlowElement buildUserTask() {
        FlowElement userTask = new UserTask();
        userTask.setKey("userTask");
        userTask.setType(FlowElementType.USER_TASK);

        List<String> utIncomings = new ArrayList<>();
        utIncomings.add("sequenceFlow");
        userTask.setIncoming(utIncomings);

        List<String> utOutgoings = new ArrayList<>();
        utOutgoings.add("sequenceFlow1");
        userTask.setOutgoing(utOutgoings);
        return userTask;
    }




    public static FlowInstancePO buildFlowInstancePO() {
        FlowInstancePO flowInstancePO = new FlowInstancePO();
        flowInstancePO.setFlowModuleId(flowModuleId);
        flowInstancePO.setFlowDeployId(flowDeployId);
        flowInstancePO.setFlowInstanceId(flowInstanceId);
        flowInstancePO.setStatus(FlowInstanceStatus.RUNNING);
        flowInstancePO.setCreateTime(new Date());
        flowInstancePO.setModifyTime(new Date());
        flowInstancePO.setCaller("caller");
        flowInstancePO.setTenant("tenant");
        return flowInstancePO;
    }

    public static FlowInstancePO buildDynamicFlowInstancePO() {
        FlowInstancePO flowInstancePO = EntityBuilder.buildFlowInstancePO();
        flowInstancePO.setFlowInstanceId("testFlowInstanceId_" + System.currentTimeMillis() + new Random().nextInt());
        return flowInstancePO;
    }

    public static NodeInstancePO buildNodeInstancePO() {
        NodeInstancePO nodeInstancePO = new NodeInstancePO();
        nodeInstancePO.setFlowDeployId(flowDeployId);
        nodeInstancePO.setFlowInstanceId(flowInstanceId);
        nodeInstancePO.setNodeInstanceId(nodeInstanceId);
        nodeInstancePO.setInstanceDataId(instanceDataId);
        nodeInstancePO.setNodeKey(nodeKey);
        nodeInstancePO.setSourceNodeInstanceId(sourceNodeInstanceId);
        nodeInstancePO.setSourceNodeKey(sourceNodeKey);
        nodeInstancePO.setStatus(NodeInstanceStatus.ACTIVE);
        nodeInstancePO.setCreateTime(new Date());
        nodeInstancePO.setModifyTime(new Date());
        nodeInstancePO.setCaller("caller");
        nodeInstancePO.setTenant("tenant");
        return nodeInstancePO;
    }

    public static NodeInstancePO buildDynamicNodeInstancePO() {
        NodeInstancePO nodeInstancePO = buildNodeInstancePO();
        nodeInstancePO.setNodeInstanceId("testNodeInstanceId_" + UUID.randomUUID().toString());
        nodeInstancePO.setSourceNodeInstanceId("testSourceNodeInstanceId_" + UUID.randomUUID().toString());
        return nodeInstancePO;
    }

    public static NodeInstanceLogPO buildNodeInstanceLogPO() {
        NodeInstanceLogPO nodeInstanceLogPO = new NodeInstanceLogPO();
        nodeInstanceLogPO.setFlowInstanceId(flowInstanceId);
        nodeInstanceLogPO.setNodeInstanceId(nodeInstanceId);
        nodeInstanceLogPO.setInstanceDataId(instanceDataId);
        nodeInstanceLogPO.setNodeKey(nodeKey);
        nodeInstanceLogPO.setType(NodeInstanceType.EXECUTE);
        nodeInstanceLogPO.setStatus(NodeInstanceStatus.ACTIVE);
        nodeInstanceLogPO.setCreateTime(new Date());
        nodeInstanceLogPO.setCaller("caller");
        nodeInstanceLogPO.setTenant("tenant");
        return nodeInstanceLogPO;
    }

    public static NodeInstanceLogPO buildNodeInstanceLogPO(String flowInstanceId) {
        NodeInstanceLogPO nodeInstanceLogPO = new NodeInstanceLogPO();
        nodeInstanceLogPO.setFlowInstanceId(flowInstanceId);
        nodeInstanceLogPO.setNodeInstanceId(nodeInstanceId);
        nodeInstanceLogPO.setInstanceDataId(instanceDataId);
        nodeInstanceLogPO.setNodeKey(nodeKey);
        nodeInstanceLogPO.setType(NodeInstanceType.EXECUTE);
        nodeInstanceLogPO.setStatus(NodeInstanceStatus.ACTIVE);
        nodeInstanceLogPO.setCreateTime(new Date());
        nodeInstanceLogPO.setCaller("caller");
        nodeInstanceLogPO.setTenant("tenant");
        return nodeInstanceLogPO;
    }

    public static InstanceDataPO buildInstanceDataPO() {
        InstanceDataPO instanceDataPO = new InstanceDataPO();
        instanceDataPO.setFlowInstanceId(flowInstanceId);
        instanceDataPO.setNodeInstanceId(nodeInstanceId);
        instanceDataPO.setFlowDeployId(flowDeployId);
        instanceDataPO.setFlowModuleId(flowModuleId);
        instanceDataPO.setInstanceDataId(instanceDataId);
        instanceDataPO.setNodeKey(nodeKey);
        List<InstanceData> instanceDataList = buildInstanceDataList();
        instanceDataPO.setInstanceData(JSON.toJSONString(instanceDataList));
        instanceDataPO.setType(InstanceDataType.EXECUTE);
        instanceDataPO.setCreateTime(new Date());
        instanceDataPO.setCaller("caller");
        instanceDataPO.setTenant("tenant");
        return instanceDataPO;
    }

    public static InstanceDataPO buildDynamicInstanceDataPO() {
        InstanceDataPO instanceDataPO = buildInstanceDataPO();
        instanceDataPO.setInstanceDataId("testInstanceDataId_" + System.currentTimeMillis() + new Random().nextInt());
        return instanceDataPO;
    }

    public static CreateFlowParam buildCreateFlowParam() {
        CreateFlowParam createFlowParam = new CreateFlowParam("testTenant", "testCaller");
        createFlowParam.setOperator(operator);
        createFlowParam.setFlowKey(flowKey);
        createFlowParam.setFlowName(flowName);
        createFlowParam.setRemark(remark);
        return createFlowParam;
    }

    public static UpdateFlowParam buildUpdateFlowParam() {
        UpdateFlowParam updateFlowParam = new UpdateFlowParam("testTenant", "testCaller");
        updateFlowParam.setOperator(operator);
        updateFlowParam.setFlowKey(flowKey);
        updateFlowParam.setFlowName(flowName);
        updateFlowParam.setRemark(remark);
        updateFlowParam.setFlowModel(buildModelStringAccess());
        return updateFlowParam;
    }
    public static DeployFlowParam buildDeployFlowParm() {
        DeployFlowParam deployFlowParam = new DeployFlowParam("testTenant", "testCaller");
        deployFlowParam.setFlowModuleId(flowModuleId);
        return deployFlowParam;
    }


    private static List<InstanceData> buildInstanceDataList() {
        InstanceData instanceData1 = new InstanceData("key1", "value1");
        InstanceData instanceData2 = new InstanceData("key2", "value2");
        InstanceData instanceData3 = new InstanceData("key3", "value3");
        List<InstanceData> instanceDataList = Lists.newArrayList();
        instanceDataList.add(instanceData1);
        instanceDataList.add(instanceData2);
        instanceDataList.add(instanceData3);
        return instanceDataList;
    }

    public static String buildModelStringAccess() {
        FlowModel flowModel = new FlowModel();
        List<FlowElement> flowElementList = Lists.newArrayList();
        //startEvent1
        StartEvent startEvent = new StartEvent();
        startEvent.setKey("startEvent1");
        startEvent.setType(FlowElementType.START_EVENT);
        List<String> seOutgoings = new ArrayList<>();
        seOutgoings.add("sequenceFlow1");
        startEvent.setOutgoing(seOutgoings);
        flowElementList.add(startEvent);

        //sequenceFlow1
        SequenceFlow sequenceFlow1 = new SequenceFlow();
        sequenceFlow1.setKey("sequenceFlow1");
        sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings = new ArrayList<>();
        sfIncomings.add("startEvent1");
        sequenceFlow1.setIncoming(sfIncomings);
        List<String> sfOutgoings = new ArrayList<>();
        sfOutgoings.add("userTask1");
        sequenceFlow1.setOutgoing(sfOutgoings);
        flowElementList.add(sequenceFlow1);

        //userTask1
        UserTask userTask = new UserTask();
        userTask.setKey("userTask1");
        userTask.setType(FlowElementType.USER_TASK);

        List<String> utIncomings = new ArrayList<>();
        utIncomings.add("sequenceFlow1");
        userTask.setIncoming(utIncomings);

        List<String> utOutgoings = new ArrayList<>();
        utOutgoings.add("sequenceFlow2");
        userTask.setOutgoing(utOutgoings);

        flowElementList.add(userTask);

        //sequenceFlow2
        SequenceFlow sequenceFlow2 = new SequenceFlow();
        sequenceFlow2.setKey("sequenceFlow2");
        sequenceFlow2.setType(FlowElementType.SEQUENCE_FLOW);

        List<String> sfIncomings2 = new ArrayList<>();
        sfIncomings2.add("userTask1");
        sequenceFlow2.setIncoming(sfIncomings2);

        List<String> sfOutgoings2 = new ArrayList<>();
        sfOutgoings2.add("exclusiveGateway1");
        sequenceFlow2.setOutgoing(sfOutgoings2);

        flowElementList.add(sequenceFlow2);

        //exclusiveGateway1
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setKey("exclusiveGateway1");
        exclusiveGateway.setType(FlowElementType.EXCLUSIVE_GATEWAY);

        Map<String, Object> properties = Maps.newHashMap();
        properties.put(Constants.ELEMENT_PROPERTIES.HOOK_INFO_IDS, "[1,2]");
        exclusiveGateway.setProperties(properties);

        List<String> egIncomings = new ArrayList<>();
        egIncomings.add("sequenceFlow2");
        exclusiveGateway.setIncoming(egIncomings);

        List<String> egOutgoings = new ArrayList<>();
        egOutgoings.add("sequenceFlow3");
        egOutgoings.add("sequenceFlow4");
        exclusiveGateway.setOutgoing(egOutgoings);

        flowElementList.add(exclusiveGateway);

        //sequenceFlow3
        SequenceFlow sequenceFlow3 = new SequenceFlow();
        sequenceFlow3.setKey("sequenceFlow3");
        sequenceFlow3.setType(FlowElementType.SEQUENCE_FLOW);
        Map<String, Object> sFproperties3 = Maps.newHashMap();
        sFproperties3.put(Constants.ELEMENT_PROPERTIES.CONDITION, "a>1&&b==1");
        sequenceFlow3.setProperties(sFproperties3);

        List<String> sfIncomings3 = new ArrayList<>();
        sfIncomings3.add("exclusiveGateway1");
        sequenceFlow3.setIncoming(sfIncomings3);

        List<String> sfOutgoings3 = new ArrayList<>();
        sfOutgoings3.add("userTask2");
        sequenceFlow3.setOutgoing(sfOutgoings3);

        flowElementList.add(sequenceFlow3);

        //sequenceFlow4
        SequenceFlow sequenceFlow4 = new SequenceFlow();
        sequenceFlow4.setKey("sequenceFlow4");
        sequenceFlow4.setType(FlowElementType.SEQUENCE_FLOW);

        Map<String, Object> sFproperties4 = Maps.newHashMap();
        sFproperties4.put(Constants.ELEMENT_PROPERTIES.DEFAULT_CONDITION, "true");
        sequenceFlow4.setProperties(sFproperties4);


        List<String> sfIncomings4 = new ArrayList<>();
        sfIncomings4.add("exclusiveGateway1");
        sequenceFlow4.setIncoming(sfIncomings4);

        List<String> sfOutgoings4 = new ArrayList<>();
        sfOutgoings4.add("userTask3");
        sequenceFlow4.setOutgoing(sfOutgoings4);

        flowElementList.add(sequenceFlow4);

        //userTask2
        UserTask userTask2 = new UserTask();
        userTask2.setKey("userTask2");
        userTask2.setType(FlowElementType.USER_TASK);

        List<String> utIncomings2 = new ArrayList<>();
        utIncomings2.add("sequenceFlow3");
        userTask2.setIncoming(utIncomings2);

        List<String> utOutgoings2 = new ArrayList<>();
        utOutgoings2.add("sequenceFlow5");
        userTask2.setOutgoing(utOutgoings2);

        flowElementList.add(userTask2);

        //userTask3
        UserTask userTask3 = new UserTask();
        userTask3.setKey("userTask3");
        userTask3.setType(FlowElementType.USER_TASK);

        List<String> utIncomings3 = new ArrayList<>();
        utIncomings3.add("sequenceFlow4");
        userTask3.setIncoming(utIncomings3);

        List<String> utOutgoings3 = new ArrayList<>();
        utOutgoings3.add("sequenceFlow6");
        userTask3.setOutgoing(utOutgoings3);

        flowElementList.add(userTask3);

        //sequenceFlow5
        SequenceFlow sequenceFlow5 = new SequenceFlow();
        sequenceFlow5.setKey("sequenceFlow5");
        sequenceFlow5.setType(FlowElementType.SEQUENCE_FLOW);

        List<String> sfIncomings5 = new ArrayList<>();
        sfIncomings5.add("userTask2");
        sequenceFlow5.setIncoming(sfIncomings5);

        List<String> sfOutgoings5 = new ArrayList<>();
        sfOutgoings5.add("endEvent1");
        sequenceFlow5.setOutgoing(sfOutgoings5);

        flowElementList.add(sequenceFlow5);

        //sequenceFlow6
        SequenceFlow sequenceFlow6 = new SequenceFlow();
        sequenceFlow6.setKey("sequenceFlow6");
        sequenceFlow6.setType(FlowElementType.SEQUENCE_FLOW);

        List<String> sfIncomings6 = new ArrayList<>();
        sfIncomings6.add("userTask3");
        sequenceFlow6.setIncoming(sfIncomings6);

        List<String> sfOutgoings6 = new ArrayList<>();
        sfOutgoings6.add("endEvent2");
        sequenceFlow6.setOutgoing(sfOutgoings6);

        flowElementList.add(sequenceFlow6);

        //endEvent1
        EndEvent endEven1 = new EndEvent();
        endEven1.setKey("endEvent1");
        endEven1.setType(FlowElementType.END_EVENT);

        List<String> eeIncomings1 = new ArrayList<>();
        eeIncomings1.add("sequenceFlow5");
        endEven1.setIncoming(eeIncomings1);

        flowElementList.add(endEven1);

        //endEvent2
        EndEvent endEvent2 = new EndEvent();
        endEvent2.setKey("endEvent2");
        endEvent2.setType(FlowElementType.END_EVENT);

        List<String> eeIncomings2 = new ArrayList<>();
        eeIncomings2.add("sequenceFlow6");
        endEvent2.setIncoming(eeIncomings2);

        flowElementList.add(endEvent2);
        flowModel.setFlowElementList(flowElementList);
        String flowModelStr = JSON.toJSONString(flowModel);
        return flowModelStr;
    }

    public static String buildModelString() {
        FlowModel flowModel = new FlowModel();
        List<FlowElement> flowElementList = new ArrayList<>();

        FlowElement startNode = new FlowElement();
        {
            startNode.setKey("startEvent1");
            startNode.setType(2);
            List<String> outgoing = new ArrayList<>();
            outgoing.add("SequenceFlow_1tsh7p9");
            startNode.setOutgoing(outgoing);
        }
        flowElementList.add(startNode);


        FlowElement endNode = new FlowElement();
        {
            endNode.setKey("EndEvent_0bul6nv");
            endNode.setType(3);
            List<String> incoming = new ArrayList<>();
            incoming.add("SequenceFlow_0ciset9");
            endNode.setIncoming(incoming);
        }
        flowElementList.add(endNode);


        FlowElement userTask1 = new FlowElement();
        {
            endNode.setKey("UserTask_08pxw7r");
            endNode.setType(4);
            List<String> incoming = new ArrayList<>();
            incoming.add("SequenceFlow_1tsh7p9");
            List<String> outgoing = new ArrayList<>();
            outgoing.add("SequenceFlow_0til2dx");
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", "1");
            map.put("outPcFormurl", "7903");
            userTask1.setIncoming(incoming);
            userTask1.setOutgoing(outgoing);
            userTask1.setProperties(map);
        }
        flowElementList.add(userTask1);

        FlowElement userTask2 = new FlowElement();
        {
            endNode.setKey("UserTask_0yp8yei");
            endNode.setType(4);
            List<String> incoming = new ArrayList<>();
            incoming.add("SequenceFlow_0til2dx");
            List<String> outgoing = new ArrayList<>();
            outgoing.add("SequenceFlow_0ciset9");
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", "展示");
            map.put("outPcFormurl", "7909");
            userTask1.setIncoming(incoming);
            userTask1.setOutgoing(outgoing);
            userTask1.setProperties(map);
        }
        flowElementList.add(userTask2);

        FlowElement sequenceFlow1 = new FlowElement();
        {
            endNode.setKey("SequenceFlow_1tsh7p9");
            endNode.setType(1);
            List<String> incoming = new ArrayList<>();
            incoming.add("startEvent1");
            List<String> outgoing = new ArrayList<>();
            outgoing.add("UserTask_08pxw7r");
            HashMap<String, Object> map = new HashMap<>();
            map.put("conditionsequenceflow", "");
            map.put("optimusFormulaGroups", "");
            userTask1.setIncoming(incoming);
            userTask1.setOutgoing(outgoing);
            userTask1.setProperties(map);
        }
        flowElementList.add(sequenceFlow1);

        FlowElement sequenceFlow2 = new FlowElement();
        {
            endNode.setKey("SequenceFlow_0til2dx");
            endNode.setType(1);
            List<String> incoming = new ArrayList<>();
            incoming.add("UserTask_08pxw7r");
            List<String> outgoing = new ArrayList<>();
            outgoing.add("UserTask_0yp8yei");
            HashMap<String, Object> map = new HashMap<>();
            map.put("conditionsequenceflow", "");
            map.put("optimusFormulaGroups", "");
            userTask1.setIncoming(incoming);
            userTask1.setOutgoing(outgoing);
            userTask1.setProperties(map);
        }
        flowElementList.add(sequenceFlow2);

        FlowElement sequenceFlow3 = new FlowElement();
        {
            endNode.setKey("SequenceFlow_0ciset9");
            endNode.setType(1);
            List<String> incoming = new ArrayList<>();
            incoming.add("UserTask_0yp8yei");
            List<String> outgoing = new ArrayList<>();
            outgoing.add("EndEvent_0bul6nv");
            HashMap<String, Object> map = new HashMap<>();
            map.put("conditionsequenceflow", "");
            map.put("optimusFormulaGroups", "");
            userTask1.setIncoming(incoming);
            userTask1.setOutgoing(outgoing);
            userTask1.setProperties(map);
        }
        flowElementList.add(sequenceFlow3);
        flowModel.setFlowElementList(flowElementList);
        String flowModelStr = JSON.toJSONString(flowModel);
        return flowModelStr;
    }

    public static StartProcessParam buildStartProcessParam(String flowDeployId) {
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId(flowDeployId);
        return startProcessParam;
    }

    public static CommitTaskParam buildCommitTaskParam(String flowInstanceId, String nodeInstanceId) {
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(flowInstanceId);
        commitTaskParam.setTaskInstanceId(nodeInstanceId);
        return commitTaskParam;
    }

    public static RollbackTaskParam buildRollbackTaskParam(String flowInstanceId, String nodeInstanceId) {
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(flowInstanceId);
        rollbackTaskParam.setTaskInstanceId(nodeInstanceId);
        return rollbackTaskParam;
    }

    public static RuntimeContext buildRuntimeContext() {
        RuntimeContext runtimeContext = new RuntimeContext();
        runtimeContext.setFlowInstanceId(flowInstanceId);
        runtimeContext.setFlowDeployId(flowDeployId);
        runtimeContext.setFlowModuleId(flowModuleId);
        List<FlowElement> flowElementList = EntityBuilder.buildFlowElementList();

        FlowModel flowModel = new FlowModel();
        flowModel.setFlowElementList(flowElementList);
        runtimeContext.setFlowElementMap(FlowModelUtil.getFlowElementMap(JSONObject.toJSONString(flowModel)));

        runtimeContext.setNodeInstanceList(Lists.newArrayList());
        runtimeContext.setCurrentNodeInstance(new NodeInstanceBO());
        runtimeContext.setSuspendNodeInstance(new NodeInstanceBO());
        runtimeContext.setInstanceDataId("");
        runtimeContext.setInstanceDataMap(InstanceDataUtil.getInstanceDataMap(buildInstanceDataList()));
        runtimeContext.setFlowInstanceStatus(FlowInstanceStatus.RUNNING);
        runtimeContext.setProcessStatus(ProcessStatus.DEFAULT);
        return runtimeContext;
    }

    // For runtime unit tests [RuntimeProcessorTest] to use, don't change it
    public static FlowDeploymentPO buildSpecialFlowDeploymentPO() {
        FlowDeploymentPO flowDeploymentPO = new FlowDeploymentPO();
        flowDeploymentPO.setFlowName(flowName);
        flowDeploymentPO.setFlowKey(flowKey);
        flowDeploymentPO.setFlowModuleId("flowModuleId");
        flowDeploymentPO.setFlowDeployId("flowDeployId");
        flowDeploymentPO.setFlowModel(JSON.toJSONString(buildSpecialFlowModel()));
        flowDeploymentPO.setStatus(FlowDeploymentStatus.DEPLOYED);
        flowDeploymentPO.setCreateTime(new Date());
        flowDeploymentPO.setModifyTime(new Date());
        flowDeploymentPO.setOperator(operator);
        flowDeploymentPO.setRemark(remark);
        return flowDeploymentPO;
    }

    public static FlowModel buildSpecialFlowModel() {
        List<FlowElement> flowElementList = Lists.newArrayList();
        {
            StartEvent startEvent = new StartEvent();
            startEvent.setKey("StartEvent_1r83q1z");
            startEvent.setType(FlowElementType.START_EVENT);
            List<String> outgoings = new ArrayList<>();
            outgoings.add("SequenceFlow_0vykylt");
            startEvent.setOutgoing(outgoings);
            flowElementList.add(startEvent);
        }
        {
            EndEvent endEvent = new EndEvent();
            endEvent.setKey("EndEvent_0z30kyv");
            endEvent.setType(FlowElementType.END_EVENT);
            List<String> incomings = new ArrayList<>();
            incomings.add("SequenceFlow_1sfugjz");
            endEvent.setIncoming(incomings);
            flowElementList.add(endEvent);
        }
        {
            EndEvent endEvent = new EndEvent();
            endEvent.setKey("EndEvent_0c82i4n");
            endEvent.setType(FlowElementType.END_EVENT);
            List<String> incomings = new ArrayList<>();
            incomings.add("SequenceFlow_1lc9xoo");
            endEvent.setIncoming(incomings);
            flowElementList.add(endEvent);
        }
        {
            EndEvent endEvent = new EndEvent();
            endEvent.setKey("EndEvent_0s4vsxw");
            endEvent.setType(FlowElementType.END_EVENT);
            List<String> incomings = new ArrayList<>();
            incomings.add("SequenceFlow_05niqg6");
            endEvent.setIncoming(incomings);
            flowElementList.add(endEvent);
        }

//        {
//            EndEvent endEvent = new EndEvent();
//            endEvent.setKey("EndEvent_1qi4pti");
//            endEvent.setType(FlowElementType.END_EVENT);
//            List<String> incomings = new ArrayList<>();
//            incomings.add("SequenceFlow_0qgt4pg");
//            endEvent.setIncoming(incomings);
//            flowElementList.add(endEvent);
//        }

        {
            EndEvent endEvent = new EndEvent();
            endEvent.setKey("EndEvent_0ysd9la");
            endEvent.setType(FlowElementType.END_EVENT);
            List<String> incomings = new ArrayList<>();
            incomings.add("SequenceFlow_0dttfqs");
            endEvent.setIncoming(incomings);
            flowElementList.add(endEvent);
        }

        {
            ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
            exclusiveGateway.setKey("ExclusiveGateway_0yq2l0s");
            exclusiveGateway.setType(FlowElementType.EXCLUSIVE_GATEWAY);

            List<String> egIncomings = new ArrayList<>();
            egIncomings.add("SequenceFlow_1qonehk");
            exclusiveGateway.setIncoming(egIncomings);

            List<String> egOutgoings = new ArrayList<>();
            egOutgoings.add("SequenceFlow_15rfdft");
            egOutgoings.add("SequenceFlow_1lc9xoo");
            exclusiveGateway.setOutgoing(egOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("hookInfoIds", "");
            exclusiveGateway.setProperties(properties);

            flowElementList.add(exclusiveGateway);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_0uld0u9");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_15rfdft");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_1sfugjz");
            utOutgoings.add("SequenceFlow_1o5y5z7");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_0vykylt");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("StartEvent_1r83q1z");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("BranchUserTask_0scrl8d");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }

        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_1sfugjz");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_0uld0u9");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("EndEvent_0z30kyv");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "");
            properties.put("conditionsequenceflow", "orderStatus.equals(\"1\")");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }

        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_1o5y5z7");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_0uld0u9");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_1eglyg7");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "");
            properties.put("conditionsequenceflow", "orderStatus.equals(\"2\")");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }

        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_1eglyg7");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_1o5y5z7");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_0dttfqs");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }

        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_0dttfqs");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_1eglyg7");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("EndEvent_0ysd9la");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }

        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_15rfdft");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ExclusiveGateway_0yq2l0s");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_0uld0u9");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "orderId.equals(\"123\")");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_1qonehk");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("BranchUserTask_0scrl8d");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ExclusiveGateway_0yq2l0s");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "danxuankuang_ytgyk==0");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_05niqg6");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("BranchUserTask_0scrl8d");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("EndEvent_0s4vsxw");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "danxuankuang_ytgyk==1");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_1lc9xoo");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ExclusiveGateway_0yq2l0s");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("EndEvent_0c82i4n");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "orderId.equals(\"456\")");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("BranchUserTask_0scrl8d");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_0vykylt");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_1qonehk");
            utOutgoings.add("SequenceFlow_05niqg6");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        FlowModel flowModel = new FlowModel();
        flowModel.setFlowElementList(flowElementList);
        return flowModel;
    }
}
