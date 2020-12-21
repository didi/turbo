package com.xiaoju.uemc.turbo.demo.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xiaoju.uemc.turbo.engine.common.FlowElementType;
import com.xiaoju.uemc.turbo.engine.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityBuilder {

    public static String buildFlowModelStr() {
        return JSON.toJSONString(buildFlowModelEntity());
    }

    public static FlowModel buildFlowModelEntity() {
        List<FlowElement> flowElementList = Lists.newArrayList();
        {
            StartEvent startEvent = new StartEvent();
            startEvent.setKey("StartEvent_0ofi5hg");
            startEvent.setType(FlowElementType.START_EVENT);
            List<String> outgoings = new ArrayList<>();
            outgoings.add("SequenceFlow_1udf5vg");
            startEvent.setOutgoing(outgoings);
            flowElementList.add(startEvent);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_1625vn7");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_1udf5vg");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_06uq82c");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
            exclusiveGateway.setKey("ExclusiveGateway_1l0d11b");
            exclusiveGateway.setType(FlowElementType.EXCLUSIVE_GATEWAY);

            List<String> egIncomings = new ArrayList<>();
            egIncomings.add("SequenceFlow_06uq82c");
            exclusiveGateway.setIncoming(egIncomings);

            List<String> egOutgoings = new ArrayList<>();
            egOutgoings.add("SequenceFlow_15vyyaj");
            egOutgoings.add("SequenceFlow_168uou3");
            exclusiveGateway.setOutgoing(egOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("hookInfoIds", "");
            exclusiveGateway.setProperties(properties);

            flowElementList.add(exclusiveGateway);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_0j0wc1o");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_15vyyaj");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_18y740t");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_05t37q8");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_168uou3");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_086u2jq");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            EndEvent endEvent = new EndEvent();
            endEvent.setKey("EndEvent_1m02l29");
            endEvent.setType(FlowElementType.END_EVENT);
            List<String> incomings = new ArrayList<>();
            incomings.add("SequenceFlow_18y740t");
            endEvent.setIncoming(incomings);
            flowElementList.add(endEvent);
        }
        {
            EndEvent endEvent = new EndEvent();
            endEvent.setKey("EndEvent_07cgwru");
            endEvent.setType(FlowElementType.END_EVENT);
            List<String> incomings = new ArrayList<>();
            incomings.add("SequenceFlow_086u2jq");
            endEvent.setIncoming(incomings);
            flowElementList.add(endEvent);
        }


        // sequence flow


        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_1udf5vg");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("StartEvent_0ofi5hg");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_1625vn7");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }

        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_06uq82c");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_1625vn7");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ExclusiveGateway_1l0d11b");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }

        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_18y740t");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_0j0wc1o");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("EndEvent_1m02l29");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_086u2jq");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_05t37q8");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("EndEvent_07cgwru");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }

        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_15vyyaj");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ExclusiveGateway_1l0d11b");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_0j0wc1o");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "message.equals(\"open\")");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_168uou3");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ExclusiveGateway_1l0d11b");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_05t37q8");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "true");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        FlowModel flowModel = new FlowModel();
        flowModel.setFlowElementList(flowElementList);
        return flowModel;
    }
}
