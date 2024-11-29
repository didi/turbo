package com.didiglobal.turbo.plugin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.didiglobal.turbo.engine.common.FlowDeploymentStatus;
import com.didiglobal.turbo.engine.common.FlowElementType;
import com.didiglobal.turbo.engine.common.NodeInstanceStatus;
import com.didiglobal.turbo.engine.common.NodeInstanceType;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.model.EndEvent;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.model.FlowModel;
import com.didiglobal.turbo.engine.model.SequenceFlow;
import com.didiglobal.turbo.engine.model.StartEvent;
import com.didiglobal.turbo.engine.model.UserTask;
import com.didiglobal.turbo.plugin.common.Constants;
import com.didiglobal.turbo.plugin.common.ExtendFlowElementType;
import com.didiglobal.turbo.plugin.common.MergeStrategy;
import com.didiglobal.turbo.plugin.model.InclusiveGateway;
import com.didiglobal.turbo.plugin.model.ParallelGateway;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EntityBuilder {
    private static long suffix = System.currentTimeMillis();
    private static String flowName = "testFlowName_" + suffix;
    private static String flowKey = "testFlowKey_" + suffix;
    private static String flowDeployId = "testFlowDeployId_" + suffix;
    private static String flowInstanceId = "testFlowInstanceId_" + suffix;
    private static String nodeInstanceId = "testNodeInstanceId_" + suffix;
    private static String instanceDataId = "testInstanceDataId_" + suffix;
    private static String sourceNodeInstanceId = "testSourceNodeInstanceId_" + suffix;
    private static String nodeKey = "testNodeKey";
    private static String sourceNodeKey = "testSourceNodeKey";
    private static String operator = "testOperator";
    private static String remark = "testRemark";
    public static NodeInstancePO buildParallelNodeInstancePO() {
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
        nodeInstancePO.setNodeType(ExtendFlowElementType.PARALLEL_GATEWAY);
        Map<String, Object> nodeInstanceProperties = new HashMap<>();
        nodeInstanceProperties.put("executeId", "123");
        nodeInstancePO.setProperties(nodeInstanceProperties);
        return nodeInstancePO;
    }

    public static NodeInstancePO buildDynamicParallelNodeInstancePO() {
        NodeInstancePO nodeInstancePO = buildParallelNodeInstancePO();
        nodeInstancePO.setNodeInstanceId("testNodeInstanceId_" + UUID.randomUUID().toString());
        nodeInstancePO.setSourceNodeInstanceId("testSourceNodeInstanceId_" + UUID.randomUUID().toString());
        nodeInstancePO.put("executeId", UUID.randomUUID().toString());
        return nodeInstancePO;
    }

    public static NodeInstanceLogPO buildParallelNodeInstanceLogPO() {
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
        nodeInstanceLogPO.put("executeId", "585858");
        return nodeInstanceLogPO;
    }

    // For runtime unit tests [RuntimeProcessorTest] to use, don't change it
    public static FlowDeploymentPO buildParallelFlowDeploymentPO() {
        FlowDeploymentPO flowDeploymentPO = new FlowDeploymentPO();
        flowDeploymentPO.setFlowName(flowName);
        flowDeploymentPO.setFlowKey(flowKey);
        flowDeploymentPO.setFlowModuleId("flowModuleId_parallel");
        flowDeploymentPO.setFlowDeployId("flowDeployId_parallel");
        flowDeploymentPO.setFlowModel(JSON.toJSONString(buildParallelFlowModel()));
        flowDeploymentPO.setStatus(FlowDeploymentStatus.DEPLOYED);
        flowDeploymentPO.setCreateTime(new Date());
        flowDeploymentPO.setModifyTime(new Date());
        flowDeploymentPO.setOperator(operator);
        flowDeploymentPO.setRemark(remark);
        return flowDeploymentPO;
    }

    public static FlowDeploymentPO buildInclusiveFlowDeploymentPO() {
        FlowDeploymentPO flowDeploymentPO = new FlowDeploymentPO();
        flowDeploymentPO.setFlowName(flowName);
        flowDeploymentPO.setFlowKey(flowKey);
        flowDeploymentPO.setFlowModuleId("flowModuleId_inclusive");
        flowDeploymentPO.setFlowDeployId("flowDeployId)_inclusive");
        flowDeploymentPO.setFlowModel(JSON.toJSONString(buildInclusiveFlowModel()));
        flowDeploymentPO.setStatus(FlowDeploymentStatus.DEPLOYED);
        flowDeploymentPO.setCreateTime(new Date());
        flowDeploymentPO.setModifyTime(new Date());
        flowDeploymentPO.setOperator(operator);
        flowDeploymentPO.setRemark(remark);
        return flowDeploymentPO;
    }

    public static FlowDeploymentPO buildParallelFlowDeploymentPOWithMergeOne() {
        FlowDeploymentPO flowDeploymentPO = new FlowDeploymentPO();
        flowDeploymentPO.setFlowName(flowName);
        flowDeploymentPO.setFlowKey(flowKey);
        flowDeploymentPO.setFlowModuleId("flowModuleId_mergeOne");
        flowDeploymentPO.setFlowDeployId("flowDeployId_mergeOne");
        flowDeploymentPO.setFlowModel(JSON.toJSONString(buildParallelFlowModelWithMergeOne()));
        flowDeploymentPO.setStatus(FlowDeploymentStatus.DEPLOYED);
        flowDeploymentPO.setCreateTime(new Date());
        flowDeploymentPO.setModifyTime(new Date());
        flowDeploymentPO.setOperator(operator);
        flowDeploymentPO.setRemark(remark);
        return flowDeploymentPO;
    }

    /**
     *                                                                                                    --> UserTask_1ram9jm --> UserTask_32ed01b
     *                                                                                                   |                                         |
     *  StartEvent_2s70149 --> ParallelGateway_38ad233 --> UserTask_0iv55sh --> ParallelGateway_1djgrgp --> UserTask_2npcbgp --> UserTask_01tuns9 --> ParallelGateway_3a1nn9f --> ParallelGateway_10lo44j --> EndEvent_2c8j53d
     *                                                 |                                                                                                                      |
     *                                                 |                                                                                                                      |
     *                                                  -------------------------------------------------> UserTask_0m7qih6 --------------------------------------------------
     */
    public static FlowModel buildParallelFlowModel() {
        List<FlowElement> flowElementList = Lists.newArrayList();
        {
            StartEvent startEvent = new StartEvent();
            startEvent.setKey("StartEvent_2s70149");
            startEvent.setType(FlowElementType.START_EVENT);
            List<String> outgoings = new ArrayList<>();
            outgoings.add("SequenceFlow_2gugjee");
            startEvent.setOutgoing(outgoings);
            flowElementList.add(startEvent);
        }
        {
            EndEvent endEvent = new EndEvent();
            endEvent.setKey("EndEvent_2c8j53d");
            endEvent.setType(FlowElementType.END_EVENT);
            List<String> incomings = new ArrayList<>();
            incomings.add("SequenceFlow_3uhg8uj");
            endEvent.setIncoming(incomings);
            flowElementList.add(endEvent);
        }
        {
            ParallelGateway parallelGateway = new ParallelGateway();
            parallelGateway.setKey("ParallelGateway_38ad233");
            parallelGateway.setType(ExtendFlowElementType.PARALLEL_GATEWAY);

            List<String> egIncomings = new ArrayList<>();
            egIncomings.add("SequenceFlow_2gugjee");
            parallelGateway.setIncoming(egIncomings);

            List<String> egOutgoings = new ArrayList<>();
            egOutgoings.add("SequenceFlow_12rbl6u");
            egOutgoings.add("SequenceFlow_3ih7eta");
            parallelGateway.setOutgoing(egOutgoings);

            Map<String, Object> properties = new HashMap<>();
            Map<String, String> forkJoinMatch = new HashMap<>();
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK, "ParallelGateway_38ad233");
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.JOIN, "ParallelGateway_10lo44j");
            properties.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK_JOIN_MATCH, JSONArray.toJSON(forkJoinMatch));
            parallelGateway.setProperties(properties);

            flowElementList.add(parallelGateway);
        }
        {
            ParallelGateway parallelGateway = new ParallelGateway();
            parallelGateway.setKey("ParallelGateway_1djgrgp");
            parallelGateway.setType(ExtendFlowElementType.PARALLEL_GATEWAY);

            List<String> egIncomings = new ArrayList<>();
            egIncomings.add("SequenceFlow_0h92s81");
            parallelGateway.setIncoming(egIncomings);

            List<String> egOutgoings = new ArrayList<>();
            egOutgoings.add("SequenceFlow_36g4hqc");
            egOutgoings.add("SequenceFlow_191a52e");
            parallelGateway.setOutgoing(egOutgoings);

            Map<String, Object> properties = new HashMap<>();
            Map<String, String> forkJoinMatch = new HashMap<>();
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK, "ParallelGateway_1djgrgp");
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.JOIN, "ParallelGateway_3a1nn9f");
            properties.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK_JOIN_MATCH, JSONArray.toJSON(forkJoinMatch));
            parallelGateway.setProperties(properties);

            flowElementList.add(parallelGateway);
        }
        {
            ParallelGateway parallelGateway = new ParallelGateway();
            parallelGateway.setKey("ParallelGateway_3a1nn9f");
            parallelGateway.setType(ExtendFlowElementType.PARALLEL_GATEWAY);

            List<String> egIncomings = new ArrayList<>();
            egIncomings.add("SequenceFlow_1h65e8t");
            egIncomings.add("SequenceFlow_25kdv36");
            parallelGateway.setIncoming(egIncomings);

            List<String> egOutgoings = new ArrayList<>();
            egOutgoings.add("SequenceFlow_3jkd63g");
            parallelGateway.setOutgoing(egOutgoings);

            Map<String, Object> properties = new HashMap<>();
            Map<String, String> forkJoinMatch = new HashMap<>();
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK, "ParallelGateway_1djgrgp");
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.JOIN, "ParallelGateway_3a1nn9f");
            properties.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK_JOIN_MATCH, JSONArray.toJSON(forkJoinMatch));
            parallelGateway.setProperties(properties);

            flowElementList.add(parallelGateway);
        }
        {
            ParallelGateway parallelGateway = new ParallelGateway();
            parallelGateway.setKey("ParallelGateway_10lo44j");
            parallelGateway.setType(ExtendFlowElementType.PARALLEL_GATEWAY);

            List<String> egIncomings = new ArrayList<>();
            egIncomings.add("SequenceFlow_3jkd63g");
            egIncomings.add("SequenceFlow_3bgdrp0");
            parallelGateway.setIncoming(egIncomings);

            List<String> egOutgoings = new ArrayList<>();
            egOutgoings.add("SequenceFlow_3uhg8uj");
            parallelGateway.setOutgoing(egOutgoings);

            Map<String, Object> properties = new HashMap<>();
            Map<String, String> forkJoinMatch = new HashMap<>();
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK, "ParallelGateway_38ad233");
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.JOIN, "ParallelGateway_10lo44j");
            properties.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK_JOIN_MATCH, JSONArray.toJSON(forkJoinMatch));
            parallelGateway.setProperties(properties);

            flowElementList.add(parallelGateway);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_0iv55sh");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_12rbl6u");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_0h92s81");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_0m7qih6");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_3ih7eta");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_3bgdrp0");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_2npcbgp");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_36g4hqc");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_27lme4l");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_01tuns9");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_27lme4l");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_1h65e8t");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_1ram9jm");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_191a52e");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_3a7oj2r");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_32ed01b");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_3a7oj2r");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_25kdv36");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_2gugjee");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("StartEvent_2s70149");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ParallelGateway_38ad233");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_12rbl6u");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ParallelGateway_38ad233");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_0iv55sh");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_3ih7eta");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ParallelGateway_38ad233");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_0m7qih6");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_0h92s81");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_0iv55sh");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ParallelGateway_1djgrgp");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_36g4hqc");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ParallelGateway_1djgrgp");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_2npcbgp");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_191a52e");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ParallelGateway_1djgrgp");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_1ram9jm");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_27lme4l");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_2npcbgp");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_01tuns9");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_3a7oj2r");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_1ram9jm");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_32ed01b");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_3jkd63g");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ParallelGateway_3a1nn9f");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ParallelGateway_10lo44j");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_3bgdrp0");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_0m7qih6");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ParallelGateway_10lo44j");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_3uhg8uj");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ParallelGateway_10lo44j");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("EndEvent_2c8j53d");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_1h65e8t");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_01tuns9");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ParallelGateway_3a1nn9f");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_25kdv36");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_32ed01b");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ParallelGateway_3a1nn9f");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        FlowModel flowModel = new FlowModel();
        flowModel.setFlowElementList(flowElementList);
        return flowModel;
    }

    /**
     *                                                                                                    --> UserTask_1ram9jm --> UserTask_01tuns9
     *                                                                                                   |                                         |
     *  StartEvent_2s70149 --> ParallelGateway_38ad233 --> UserTask_0iv55sh --> InclusiveGateway_1djgrgp --> UserTask_2npcbgp --> UserTask_32ed01b --> InclusiveGateway_3a1nn9f --> ParallelGateway_10lo44j --> EndEvent_2c8j53d
     *                                                 |                                                                                                                        |
     *                                                 |                                                                                                                        |
     *                                                  -------------------------------------------------> UserTask_0m7qih6 ----------------------------------------------------
     */
    public static FlowModel buildInclusiveFlowModel() {
        List<FlowElement> flowElementList = Lists.newArrayList();
        {
            StartEvent startEvent = new StartEvent();
            startEvent.setKey("StartEvent_2s70149");
            startEvent.setType(FlowElementType.START_EVENT);
            List<String> outgoings = new ArrayList<>();
            outgoings.add("SequenceFlow_2gugjee");
            startEvent.setOutgoing(outgoings);
            flowElementList.add(startEvent);
        }
        {
            EndEvent endEvent = new EndEvent();
            endEvent.setKey("EndEvent_2c8j53d");
            endEvent.setType(FlowElementType.END_EVENT);
            List<String> incomings = new ArrayList<>();
            incomings.add("SequenceFlow_3uhg8uj");
            endEvent.setIncoming(incomings);
            flowElementList.add(endEvent);
        }
        {
            ParallelGateway parallelGateway = new ParallelGateway();
            parallelGateway.setKey("ParallelGateway_38ad233");
            parallelGateway.setType(ExtendFlowElementType.PARALLEL_GATEWAY);

            List<String> egIncomings = new ArrayList<>();
            egIncomings.add("SequenceFlow_2gugjee");
            parallelGateway.setIncoming(egIncomings);

            List<String> egOutgoings = new ArrayList<>();
            egOutgoings.add("SequenceFlow_12rbl6u");
            egOutgoings.add("SequenceFlow_3ih7eta");
            parallelGateway.setOutgoing(egOutgoings);

            Map<String, Object> properties = new HashMap<>();
            Map<String, String> forkJoinMatch = new HashMap<>();
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK, "ParallelGateway_38ad233");
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.JOIN, "ParallelGateway_10lo44j");
            properties.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK_JOIN_MATCH, JSONArray.toJSON(forkJoinMatch));
            parallelGateway.setProperties(properties);

            flowElementList.add(parallelGateway);
        }
        {
            InclusiveGateway inclusiveGateway = new InclusiveGateway();
            inclusiveGateway.setKey("InclusiveGateway_1djgrgp");
            inclusiveGateway.setType(ExtendFlowElementType.INCLUSIVE_GATEWAY);

            List<String> egIncomings = new ArrayList<>();
            egIncomings.add("SequenceFlow_0h92s81");
            inclusiveGateway.setIncoming(egIncomings);

            List<String> egOutgoings = new ArrayList<>();
            egOutgoings.add("SequenceFlow_36g4hqc");
            egOutgoings.add("SequenceFlow_191a52e");
            inclusiveGateway.setOutgoing(egOutgoings);

            Map<String, Object> properties = new HashMap<>();
            Map<String, String> forkJoinMatch = new HashMap<>();
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK, "InclusiveGateway_1djgrgp");
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.JOIN, "InclusiveGateway_3a1nn9f");
            properties.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK_JOIN_MATCH, JSONArray.toJSON(forkJoinMatch));
            inclusiveGateway.setProperties(properties);

            flowElementList.add(inclusiveGateway);
        }
        {
            InclusiveGateway inclusiveGateway = new InclusiveGateway();
            inclusiveGateway.setKey("InclusiveGateway_3a1nn9f");
            inclusiveGateway.setType(ExtendFlowElementType.INCLUSIVE_GATEWAY);

            List<String> egIncomings = new ArrayList<>();
            egIncomings.add("SequenceFlow_1h65e8t");
            egIncomings.add("SequenceFlow_25kdv36");
            inclusiveGateway.setIncoming(egIncomings);

            List<String> egOutgoings = new ArrayList<>();
            egOutgoings.add("SequenceFlow_3jkd63g");
            inclusiveGateway.setOutgoing(egOutgoings);

            Map<String, Object> properties = new HashMap<>();
            Map<String, String> forkJoinMatch = new HashMap<>();
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK, "InclusiveGateway_1djgrgp");
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.JOIN, "InclusiveGateway_3a1nn9f");
            properties.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK_JOIN_MATCH, JSONArray.toJSON(forkJoinMatch));
            inclusiveGateway.setProperties(properties);

            flowElementList.add(inclusiveGateway);
        }
        {
            ParallelGateway parallelGateway = new ParallelGateway();
            parallelGateway.setKey("ParallelGateway_10lo44j");
            parallelGateway.setType(ExtendFlowElementType.PARALLEL_GATEWAY);

            List<String> egIncomings = new ArrayList<>();
            egIncomings.add("SequenceFlow_3jkd63g");
            egIncomings.add("SequenceFlow_3bgdrp0");
            parallelGateway.setIncoming(egIncomings);

            List<String> egOutgoings = new ArrayList<>();
            egOutgoings.add("SequenceFlow_3uhg8uj");
            parallelGateway.setOutgoing(egOutgoings);

            Map<String, Object> properties = new HashMap<>();
            Map<String, String> forkJoinMatch = new HashMap<>();
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK, "ParallelGateway_38ad233");
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.JOIN, "ParallelGateway_10lo44j");
            properties.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK_JOIN_MATCH, JSONArray.toJSON(forkJoinMatch));
            parallelGateway.setProperties(properties);

            flowElementList.add(parallelGateway);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_0iv55sh");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_12rbl6u");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_0h92s81");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_0m7qih6");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_3ih7eta");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_3bgdrp0");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_2npcbgp");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_36g4hqc");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_27lme4l");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_01tuns9");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_27lme4l");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_1h65e8t");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_1ram9jm");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_191a52e");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_3a7oj2r");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_32ed01b");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_3a7oj2r");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_25kdv36");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_2gugjee");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("StartEvent_2s70149");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ParallelGateway_38ad233");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_12rbl6u");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ParallelGateway_38ad233");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_0iv55sh");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_3ih7eta");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ParallelGateway_38ad233");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_0m7qih6");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_0h92s81");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_0iv55sh");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("InclusiveGateway_1djgrgp");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_36g4hqc");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("InclusiveGateway_1djgrgp");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_2npcbgp");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "${(a>=10)}");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_191a52e");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("InclusiveGateway_1djgrgp");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_1ram9jm");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "${(a<10)}");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_27lme4l");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_2npcbgp");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_01tuns9");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_3a7oj2r");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_1ram9jm");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_32ed01b");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_3jkd63g");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("InclusiveGateway_3a1nn9f");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ParallelGateway_10lo44j");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_3bgdrp0");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_0m7qih6");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ParallelGateway_10lo44j");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_3uhg8uj");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ParallelGateway_10lo44j");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("EndEvent_2c8j53d");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_1h65e8t");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_01tuns9");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("InclusiveGateway_3a1nn9f");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_25kdv36");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_32ed01b");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("InclusiveGateway_3a1nn9f");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        FlowModel flowModel = new FlowModel();
        flowModel.setFlowElementList(flowElementList);
        return flowModel;
    }

    /**
     *                                                                                                    --> UserTask_1ram9jm --> UserTask_32ed01b
     *                                                                                                   |                                         |
     *  StartEvent_2s70149 --> ParallelGateway_38ad233 --> UserTask_0iv55sh --> ParallelGateway_1djgrgp --> UserTask_2npcbgp --> UserTask_01tuns9 --> ParallelGateway_3a1nn9f --> ParallelGateway_10lo44j --> EndEvent_2c8j53d
     *                                                 |                                                                                                                      |
     *                                                 |                                                                                                                      |
     *                                                  -------------------------------------------------> UserTask_0m7qih6 --------------------------------------------------
     */
    public static FlowModel buildParallelFlowModelWithMergeOne() {
        List<FlowElement> flowElementList = Lists.newArrayList();
        {
            StartEvent startEvent = new StartEvent();
            startEvent.setKey("StartEvent_2s70149");
            startEvent.setType(FlowElementType.START_EVENT);
            List<String> outgoings = new ArrayList<>();
            outgoings.add("SequenceFlow_2gugjee");
            startEvent.setOutgoing(outgoings);
            flowElementList.add(startEvent);
        }
        {
            EndEvent endEvent = new EndEvent();
            endEvent.setKey("EndEvent_2c8j53d");
            endEvent.setType(FlowElementType.END_EVENT);
            List<String> incomings = new ArrayList<>();
            incomings.add("SequenceFlow_3uhg8uj");
            endEvent.setIncoming(incomings);
            flowElementList.add(endEvent);
        }
        {
            ParallelGateway parallelGateway = new ParallelGateway();
            parallelGateway.setKey("ParallelGateway_38ad233");
            parallelGateway.setType(ExtendFlowElementType.PARALLEL_GATEWAY);

            List<String> egIncomings = new ArrayList<>();
            egIncomings.add("SequenceFlow_2gugjee");
            parallelGateway.setIncoming(egIncomings);

            List<String> egOutgoings = new ArrayList<>();
            egOutgoings.add("SequenceFlow_12rbl6u");
            egOutgoings.add("SequenceFlow_3ih7eta");
            parallelGateway.setOutgoing(egOutgoings);

            Map<String, Object> properties = new HashMap<>();
            Map<String, String> forkJoinMatch = new HashMap<>();
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK, "ParallelGateway_38ad233");
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.JOIN, "ParallelGateway_10lo44j");
            properties.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK_JOIN_MATCH, JSONArray.toJSON(forkJoinMatch));
            parallelGateway.setProperties(properties);

            flowElementList.add(parallelGateway);
        }
        {
            ParallelGateway parallelGateway = new ParallelGateway();
            parallelGateway.setKey("ParallelGateway_1djgrgp");
            parallelGateway.setType(ExtendFlowElementType.PARALLEL_GATEWAY);

            List<String> egIncomings = new ArrayList<>();
            egIncomings.add("SequenceFlow_0h92s81");
            parallelGateway.setIncoming(egIncomings);

            List<String> egOutgoings = new ArrayList<>();
            egOutgoings.add("SequenceFlow_36g4hqc");
            egOutgoings.add("SequenceFlow_191a52e");
            parallelGateway.setOutgoing(egOutgoings);

            Map<String, Object> properties = new HashMap<>();
            Map<String, String> forkJoinMatch = new HashMap<>();
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK, "ParallelGateway_1djgrgp");
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.JOIN, "ParallelGateway_3a1nn9f");
            properties.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK_JOIN_MATCH, JSONArray.toJSON(forkJoinMatch));
            parallelGateway.setProperties(properties);

            flowElementList.add(parallelGateway);
        }
        {
            ParallelGateway parallelGateway = new ParallelGateway();
            parallelGateway.setKey("ParallelGateway_3a1nn9f");
            parallelGateway.setType(ExtendFlowElementType.PARALLEL_GATEWAY);

            List<String> egIncomings = new ArrayList<>();
            egIncomings.add("SequenceFlow_1h65e8t");
            egIncomings.add("SequenceFlow_25kdv36");
            parallelGateway.setIncoming(egIncomings);

            List<String> egOutgoings = new ArrayList<>();
            egOutgoings.add("SequenceFlow_3jkd63g");
            parallelGateway.setOutgoing(egOutgoings);

            Map<String, Object> properties = new HashMap<>();
            Map<String, String> forkJoinMatch = new HashMap<>();
            forkJoinMatch.put(Constants.ELEMENT_PROPERTIES.FORK, "ParallelGateway_1djgrgp");
            forkJoinMatch.put(Constants.ELEMENT_PROPERTIES.JOIN, "ParallelGateway_3a1nn9f");
            properties.put(Constants.ELEMENT_PROPERTIES.FORK_JOIN_MATCH, JSONArray.toJSON(forkJoinMatch));
            properties.put(Constants.ELEMENT_PROPERTIES.BRANCH_MERGE, MergeStrategy.BRANCH_MERGE.ANY_ONE);
            properties.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.DATA_MERGE, MergeStrategy.DATA_MERGE.NONE);
            parallelGateway.setProperties(properties);

            flowElementList.add(parallelGateway);
        }
        {
            ParallelGateway parallelGateway = new ParallelGateway();
            parallelGateway.setKey("ParallelGateway_10lo44j");
            parallelGateway.setType(ExtendFlowElementType.PARALLEL_GATEWAY);

            List<String> egIncomings = new ArrayList<>();
            egIncomings.add("SequenceFlow_3jkd63g");
            egIncomings.add("SequenceFlow_3bgdrp0");
            parallelGateway.setIncoming(egIncomings);

            List<String> egOutgoings = new ArrayList<>();
            egOutgoings.add("SequenceFlow_3uhg8uj");
            parallelGateway.setOutgoing(egOutgoings);

            Map<String, Object> properties = new HashMap<>();
            Map<String, String> forkJoinMatch = new HashMap<>();
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK, "ParallelGateway_38ad233");
            forkJoinMatch.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.JOIN, "ParallelGateway_10lo44j");
            properties.put(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.FORK_JOIN_MATCH, JSONArray.toJSON(forkJoinMatch));
            parallelGateway.setProperties(properties);

            flowElementList.add(parallelGateway);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_0iv55sh");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_12rbl6u");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_0h92s81");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_0m7qih6");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_3ih7eta");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_3bgdrp0");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_2npcbgp");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_36g4hqc");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_27lme4l");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_01tuns9");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_27lme4l");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_1h65e8t");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_1ram9jm");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_191a52e");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_3a7oj2r");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_32ed01b");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_3a7oj2r");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_25kdv36");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_2gugjee");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("StartEvent_2s70149");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ParallelGateway_38ad233");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_12rbl6u");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ParallelGateway_38ad233");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_0iv55sh");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_3ih7eta");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ParallelGateway_38ad233");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_0m7qih6");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_0h92s81");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_0iv55sh");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ParallelGateway_1djgrgp");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_36g4hqc");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ParallelGateway_1djgrgp");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_2npcbgp");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_191a52e");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ParallelGateway_1djgrgp");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_1ram9jm");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_27lme4l");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_2npcbgp");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_01tuns9");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_3a7oj2r");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_1ram9jm");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_32ed01b");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_3jkd63g");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ParallelGateway_3a1nn9f");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ParallelGateway_10lo44j");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_3bgdrp0");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_0m7qih6");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ParallelGateway_10lo44j");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_3uhg8uj");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ParallelGateway_10lo44j");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("EndEvent_2c8j53d");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_1h65e8t");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_01tuns9");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ParallelGateway_3a1nn9f");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_25kdv36");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_32ed01b");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ParallelGateway_3a1nn9f");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        FlowModel flowModel = new FlowModel();
        flowModel.setFlowElementList(flowElementList);
        return flowModel;
    }
}
