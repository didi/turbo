package com.didiglobal.turbo.demo.util;

import com.alibaba.fastjson.JSON;
import com.didiglobal.turbo.engine.common.FlowElementType;
import com.didiglobal.turbo.engine.model.*;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityBuilder {

    private EntityBuilder() {

    }

    /**
     * 这里的数据一般都是，展示页面（前端）提供的，这里后台构建。
     * @return
     *
     *                                               <3
     *                                               --->  用户节点（直属领导审批）--->
     * 开始节点 ---> 用户节点（输入请假天数）--->   排他网关                                 结束节点
     *                                               --->  用户节点（间接领导审批）--->
     *                                               >=3
     */
    public static String buildLeaveFlowModelStr() {
        return JSON.toJSONString(buildLeaveFlowModelEntity());
    }
    /**
     * 这里的数据一般都是，展示页面（前端）提供的，这里后台构建。
     * @return
     *
     *                                                           未发货
     *                                                           --->   用户节点（申请取消）     --->
     *                                                           未收到货
     * 开始节点 ->  用户节点（输入订单相关信息) --->排他节点(判断订单状态)  --->   用户节点（展示物流信息）  --->   结束节点
     *                                                           已收到货
     *                                                           --->    用户节点（填写售后原因） --->
     */
    public static String buildAfterSaleFlowModelStr() {
        return JSON.toJSONString(buildAfterSaleFlowModelEntity());
    }

    private static Object buildAfterSaleFlowModelEntity() {
        List<FlowElement> flowElementList = Lists.newArrayList();
        //start node
        StartEvent startEvent1 = new StartEvent();
        startEvent1.setKey("StartEvent_0ofi5hg");
        startEvent1.setType(FlowElementType.START_EVENT);
        List<String> outgoings = new ArrayList<>();
        outgoings.add("SequenceFlow_1udf5vg");
        startEvent1.setOutgoing(outgoings);
        flowElementList.add(startEvent1);
        //user node
        UserTask userTask1 = new UserTask();
        userTask1.setKey("UserTask_1625vn7");
        userTask1.setType(FlowElementType.USER_TASK);
        List<String> utIncomings = new ArrayList<>();
        utIncomings.add("SequenceFlow_1udf5vg");
        userTask1.setIncoming(utIncomings);
        List<String> utOutgoings = new ArrayList<>();
        utOutgoings.add("SequenceFlow_06uq82c");
        userTask1.setOutgoing(utOutgoings);
        flowElementList.add(userTask1);
        //Exclusive node
        ExclusiveGateway exclusiveGateway1 = new ExclusiveGateway();
        exclusiveGateway1.setKey("ExclusiveGateway_1l0d11b");
        exclusiveGateway1.setType(FlowElementType.EXCLUSIVE_GATEWAY);
        List<String> egIncomings = new ArrayList<>();
        egIncomings.add("SequenceFlow_06uq82c");
        exclusiveGateway1.setIncoming(egIncomings);
        List<String> egOutgoings = new ArrayList<>();
        egOutgoings.add("SequenceFlow_15vyyaj");
        egOutgoings.add("SequenceFlow_168uou3");
        egOutgoings.add("SequenceFlow_168uou4");
        exclusiveGateway1.setOutgoing(egOutgoings);
        Map<String, Object> properties = new HashMap<>();
        properties.put("hookInfoIds", "");
        exclusiveGateway1.setProperties(properties);
        flowElementList.add(exclusiveGateway1);

        //未收到货
        UserTask userTask2 = new UserTask();
        userTask2.setKey("UserTask_0j0wc1o");
        userTask2.setType(FlowElementType.USER_TASK);
        List<String> utIncomings2 = new ArrayList<>();
        utIncomings2.add("SequenceFlow_15vyyaj");
        userTask2.setIncoming(utIncomings2);
        List<String> utOutgoings2 = new ArrayList<>();
        utOutgoings2.add("SequenceFlow_18y740t");//结束
        userTask2.setOutgoing(utOutgoings2);
        flowElementList.add(userTask2);
        //已发货
        UserTask userTask3 = new UserTask();
        userTask3.setKey("UserTask_05t37q8");
        userTask3.setType(FlowElementType.USER_TASK);
        List<String> utIncomings3 = new ArrayList<>();
        utIncomings3.add("SequenceFlow_168uou3");
        userTask3.setIncoming(utIncomings3);
        List<String> utOutgoings3 = new ArrayList<>();
        utOutgoings3.add("SequenceFlow_18y740t");
        userTask3.setOutgoing(utOutgoings3);
        flowElementList.add(userTask3);
        //已收到货
        UserTask userTask4 = new UserTask();
        userTask4.setKey("UserTask_05p38q9");
        userTask4.setType(FlowElementType.USER_TASK);
        List<String> utIncomings4 = new ArrayList<>();
        utIncomings4.add("SequenceFlow_168uou4");
        userTask4.setIncoming(utIncomings4);
        List<String> utOutgoings4 = new ArrayList<>();
        utOutgoings4.add("SequenceFlow_18y740t");
        userTask4.setOutgoing(utOutgoings4);
        flowElementList.add(userTask4);

        EndEvent endEvent1 = new EndEvent();
        endEvent1.setKey("EndEvent_1m02l29");
        endEvent1.setType(FlowElementType.END_EVENT);
        List<String> incomings = new ArrayList<>();
        incomings.add("SequenceFlow_18y740t");
        incomings.add("SequenceFlow_086u2jq");
        incomings.add("SequenceFlow_086u3jq");
        endEvent1.setIncoming(incomings);
        flowElementList.add(endEvent1);


        // sequence flow

        SequenceFlow sequenceFlow1 = new SequenceFlow();
        sequenceFlow1.setKey("SequenceFlow_1udf5vg");
        sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings1 = new ArrayList<>();
        sfIncomings1.add("StartEvent_0ofi5hg");
        sequenceFlow1.setIncoming(sfIncomings1);
        List<String> sfOutgoings1 = new ArrayList<>();
        sfOutgoings1.add("UserTask_1625vn7");
        sequenceFlow1.setOutgoing(sfOutgoings1);
        Map<String, Object> properties1 = new HashMap<>();
        properties1.put("defaultConditions", "false");
        properties1.put("conditionsequenceflow", "");
        sequenceFlow1.setProperties(properties1);
        flowElementList.add(sequenceFlow1);

        SequenceFlow sequenceFlow2 = new SequenceFlow();
        sequenceFlow2.setKey("SequenceFlow_06uq82c");
        sequenceFlow2.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings2 = new ArrayList<>();
        sfIncomings2.add("UserTask_1625vn7");
        sequenceFlow2.setIncoming(sfIncomings2);
        List<String> sfOutgoings2 = new ArrayList<>();
        sfOutgoings2.add("ExclusiveGateway_1l0d11b");
        sequenceFlow2.setOutgoing(sfOutgoings2);
        Map<String, Object> properties2 = new HashMap<>();
        properties2.put("defaultConditions", "false");
        properties2.put("conditionsequenceflow", "");
        sequenceFlow2.setProperties(properties2);
        flowElementList.add(sequenceFlow2);

        SequenceFlow sequenceFlow3 = new SequenceFlow();
        sequenceFlow3.setKey("SequenceFlow_18y740t");
        sequenceFlow3.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings3 = new ArrayList<>();
        sfIncomings3.add("UserTask_0j0wc1o");
        sequenceFlow3.setIncoming(sfIncomings3);
        List<String> sfOutgoings3 = new ArrayList<>();
        sfOutgoings3.add("EndEvent_1m02l29");
        sequenceFlow3.setOutgoing(sfOutgoings3);
        Map<String, Object> properties3 = new HashMap<>();
        properties3.put("defaultConditions", "false");
        properties3.put("conditionsequenceflow", "");
        sequenceFlow3.setProperties(properties3);
        flowElementList.add(sequenceFlow3);

        SequenceFlow sequenceFlow4 = new SequenceFlow();
        sequenceFlow4.setKey("SequenceFlow_086u2jq");
        sequenceFlow4.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings4 = new ArrayList<>();
        sfIncomings4.add("UserTask_05t37q8");
        sequenceFlow4.setIncoming(sfIncomings4);
        List<String> sfOutgoings4 = new ArrayList<>();
        sfOutgoings4.add("EndEvent_1m02l29");
        sequenceFlow4.setOutgoing(sfOutgoings4);
        Map<String, Object> properties4 = new HashMap<>();
        properties4.put("defaultConditions", "false");
        properties4.put("conditionsequenceflow", "");
        sequenceFlow4.setProperties(properties4);
        flowElementList.add(sequenceFlow4);

        SequenceFlow sequenceFlow5 = new SequenceFlow();
        sequenceFlow5.setKey("SequenceFlow_086u3jq");
        sequenceFlow5.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings5 = new ArrayList<>();
        sfIncomings5.add("UserTask_05p38q9");
        sequenceFlow5.setIncoming(sfIncomings5);
        List<String> sfOutgoings5 = new ArrayList<>();
        sfOutgoings5.add("EndEvent_1m02l29");
        sequenceFlow5.setOutgoing(sfOutgoings5);
        Map<String, Object> properties5 = new HashMap<>();
        properties5.put("defaultConditions", "false");
        properties5.put("conditionsequenceflow", "");
        sequenceFlow5.setProperties(properties5);
        flowElementList.add(sequenceFlow5);

        SequenceFlow sequenceFlow6 = new SequenceFlow();
        sequenceFlow6.setKey("SequenceFlow_15vyyaj");
        sequenceFlow6.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings6 = new ArrayList<>();
        sfIncomings6.add("ExclusiveGateway_1l0d11b");
        sequenceFlow6.setIncoming(sfIncomings6);
        List<String> sfOutgoings6 = new ArrayList<>();
        sfOutgoings6.add("UserTask_0j0wc1o");
        sequenceFlow6.setOutgoing(sfOutgoings6);
        Map<String, Object> properties6 = new HashMap<>();
        properties6.put("defaultConditions", "false");
        properties6.put("conditionsequenceflow", "status.equals(\"0\")");
        sequenceFlow6.setProperties(properties6);
        flowElementList.add(sequenceFlow6);

        SequenceFlow sequenceFlow7 = new SequenceFlow();
        sequenceFlow7.setKey("SequenceFlow_168uou3");
        sequenceFlow7.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings7 = new ArrayList<>();
        sfIncomings7.add("ExclusiveGateway_1l0d11b");
        sequenceFlow7.setIncoming(sfIncomings7);
        List<String> sfOutgoings7 = new ArrayList<>();
        sfOutgoings7.add("UserTask_05t37q8");
        sequenceFlow7.setOutgoing(sfOutgoings7);
        Map<String, Object> properties7 = new HashMap<>();
        properties7.put("defaultConditions", "false");
        properties7.put("conditionsequenceflow", "status.equals(\"1\")");
        sequenceFlow7.setProperties(properties7);
        flowElementList.add(sequenceFlow7);

        SequenceFlow sequenceFlow8 = new SequenceFlow();
        sequenceFlow8.setKey("SequenceFlow_168uou4");
        sequenceFlow8.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings8 = new ArrayList<>();
        sfIncomings8.add("ExclusiveGateway_1l0d11b");
        sequenceFlow8.setIncoming(sfIncomings8);
        List<String> sfOutgoings8 = new ArrayList<>();
        sfOutgoings8.add("UserTask_05p38q9");
        sequenceFlow8.setOutgoing(sfOutgoings8);
        Map<String, Object> properties8 = new HashMap<>();
        properties8.put("defaultConditions", "false");
        properties8.put("conditionsequenceflow", "status.equals(\"2\")");
        sequenceFlow8.setProperties(properties8);
        flowElementList.add(sequenceFlow8);

        FlowModel flowModel = new FlowModel();
        flowModel.setFlowElementList(flowElementList);
        return flowModel;
    }

    public static FlowModel buildLeaveFlowModelEntity() {
        List<FlowElement> flowElementList = Lists.newArrayList();
        //start node
        StartEvent startEvent1 = new StartEvent();
        startEvent1.setKey("StartEvent_0ofi5hg");
        startEvent1.setType(FlowElementType.START_EVENT);
        List<String> outgoings = new ArrayList<>();
        outgoings.add("SequenceFlow_1udf5vg");
        startEvent1.setOutgoing(outgoings);
        flowElementList.add(startEvent1);
        //user node
        UserTask userTask1 = new UserTask();
        userTask1.setKey("UserTask_1625vn7");
        userTask1.setType(FlowElementType.USER_TASK);
        List<String> utIncomings = new ArrayList<>();
        utIncomings.add("SequenceFlow_1udf5vg");
        userTask1.setIncoming(utIncomings);
        List<String> utOutgoings = new ArrayList<>();
        utOutgoings.add("SequenceFlow_06uq82c");
        userTask1.setOutgoing(utOutgoings);
        flowElementList.add(userTask1);
        //Exclusive node
        ExclusiveGateway exclusiveGateway1 = new ExclusiveGateway();
        exclusiveGateway1.setKey("ExclusiveGateway_1l0d11b");
        exclusiveGateway1.setType(FlowElementType.EXCLUSIVE_GATEWAY);
        List<String> egIncomings = new ArrayList<>();
        egIncomings.add("SequenceFlow_06uq82c");
        exclusiveGateway1.setIncoming(egIncomings);
        List<String> egOutgoings = new ArrayList<>();
        egOutgoings.add("SequenceFlow_15vyyaj");
        egOutgoings.add("SequenceFlow_168uou3");
        exclusiveGateway1.setOutgoing(egOutgoings);
        Map<String, Object> properties = new HashMap<>();
        properties.put("hookInfoIds", "");
        exclusiveGateway1.setProperties(properties);
        flowElementList.add(exclusiveGateway1);

        UserTask userTask2 = new UserTask();
        userTask2.setKey("UserTask_0j0wc1o");
        userTask2.setType(FlowElementType.USER_TASK);
        List<String> utIncomings2 = new ArrayList<>();
        utIncomings2.add("SequenceFlow_15vyyaj");
        userTask2.setIncoming(utIncomings2);
        List<String> utOutgoings2 = new ArrayList<>();
        utOutgoings2.add("SequenceFlow_18y740t");
        userTask2.setOutgoing(utOutgoings2);
        flowElementList.add(userTask2);

        UserTask userTask3 = new UserTask();
        userTask3.setKey("UserTask_05t37q8");
        userTask3.setType(FlowElementType.USER_TASK);
        List<String> utIncomings3 = new ArrayList<>();
        utIncomings3.add("SequenceFlow_168uou3");
        userTask3.setIncoming(utIncomings3);
        List<String> utOutgoings3 = new ArrayList<>();
        utOutgoings3.add("SequenceFlow_086u2jq");
        userTask3.setOutgoing(utOutgoings3);
        flowElementList.add(userTask3);

        EndEvent endEvent1 = new EndEvent();
        endEvent1.setKey("EndEvent_1m02l29");
        endEvent1.setType(FlowElementType.END_EVENT);
        List<String> incomings = new ArrayList<>();
        incomings.add("SequenceFlow_18y740t");
        incomings.add("SequenceFlow_086u2jq");
        endEvent1.setIncoming(incomings);
        flowElementList.add(endEvent1);

        // sequence flow

        SequenceFlow sequenceFlow1 = new SequenceFlow();
        sequenceFlow1.setKey("SequenceFlow_1udf5vg");
        sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings1 = new ArrayList<>();
        sfIncomings1.add("StartEvent_0ofi5hg");
        sequenceFlow1.setIncoming(sfIncomings1);
        List<String> sfOutgoings1 = new ArrayList<>();
        sfOutgoings1.add("UserTask_1625vn7");
        sequenceFlow1.setOutgoing(sfOutgoings1);
        Map<String, Object> properties1 = new HashMap<>();
        properties1.put("defaultConditions", "false");
        properties1.put("conditionsequenceflow", "");
        sequenceFlow1.setProperties(properties1);
        flowElementList.add(sequenceFlow1);

        SequenceFlow sequenceFlow2 = new SequenceFlow();
        sequenceFlow2.setKey("SequenceFlow_06uq82c");
        sequenceFlow2.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings2 = new ArrayList<>();
        sfIncomings2.add("UserTask_1625vn7");
        sequenceFlow2.setIncoming(sfIncomings2);
        List<String> sfOutgoings2 = new ArrayList<>();
        sfOutgoings2.add("ExclusiveGateway_1l0d11b");
        sequenceFlow2.setOutgoing(sfOutgoings2);
        Map<String, Object> properties2 = new HashMap<>();
        properties2.put("defaultConditions", "false");
        properties2.put("conditionsequenceflow", "");
        sequenceFlow2.setProperties(properties2);
        flowElementList.add(sequenceFlow2);

        SequenceFlow sequenceFlow3 = new SequenceFlow();
        sequenceFlow3.setKey("SequenceFlow_18y740t");
        sequenceFlow3.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings3 = new ArrayList<>();
        sfIncomings3.add("UserTask_0j0wc1o");
        sequenceFlow3.setIncoming(sfIncomings3);
        List<String> sfOutgoings3 = new ArrayList<>();
        sfOutgoings3.add("EndEvent_1m02l29");
        sequenceFlow3.setOutgoing(sfOutgoings3);
        Map<String, Object> properties3 = new HashMap<>();
        properties3.put("defaultConditions", "false");
        properties3.put("conditionsequenceflow", "");
        sequenceFlow3.setProperties(properties3);
        flowElementList.add(sequenceFlow3);

        SequenceFlow sequenceFlow4 = new SequenceFlow();
        sequenceFlow4.setKey("SequenceFlow_086u2jq");
        sequenceFlow4.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings4 = new ArrayList<>();
        sfIncomings4.add("UserTask_05t37q8");
        sequenceFlow4.setIncoming(sfIncomings4);
        List<String> sfOutgoings4 = new ArrayList<>();
        sfOutgoings4.add("EndEvent_1m02l29");
        sequenceFlow4.setOutgoing(sfOutgoings4);
        Map<String, Object> properties4 = new HashMap<>();
        properties4.put("defaultConditions", "false");
        properties4.put("conditionsequenceflow", "");
        sequenceFlow4.setProperties(properties4);
        flowElementList.add(sequenceFlow4);

        SequenceFlow sequenceFlow5 = new SequenceFlow();
        sequenceFlow5.setKey("SequenceFlow_15vyyaj");
        sequenceFlow5.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings5 = new ArrayList<>();
        sfIncomings5.add("ExclusiveGateway_1l0d11b");
        sequenceFlow5.setIncoming(sfIncomings5);
        List<String> sfOutgoings5 = new ArrayList<>();
        sfOutgoings5.add("UserTask_0j0wc1o");
        sequenceFlow5.setOutgoing(sfOutgoings5);
        Map<String, Object> properties5 = new HashMap<>();
        properties5.put("defaultConditions", "false");
        properties5.put("conditionsequenceflow", "n<=3");
        sequenceFlow5.setProperties(properties5);
        flowElementList.add(sequenceFlow5);

        SequenceFlow sequenceFlow6 = new SequenceFlow();
        sequenceFlow6.setKey("SequenceFlow_168uou3");
        sequenceFlow6.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings6 = new ArrayList<>();
        sfIncomings6.add("ExclusiveGateway_1l0d11b");
        sequenceFlow6.setIncoming(sfIncomings6);
        List<String> sfOutgoings6 = new ArrayList<>();
        sfOutgoings6.add("UserTask_05t37q8");
        sequenceFlow6.setOutgoing(sfOutgoings6);
        Map<String, Object> properties6 = new HashMap<>();
        properties6.put("defaultConditions", "false");
        properties6.put("conditionsequenceflow", "n>3");
        sequenceFlow6.setProperties(properties6);
        flowElementList.add(sequenceFlow6);

        FlowModel flowModel = new FlowModel();
        flowModel.setFlowElementList(flowElementList);
        return flowModel;
    }
}
