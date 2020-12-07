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

    public static String  buildFlowModelStr() {
        return JSON.toJSONString(buildFlowModelEntity());
    }

    public static FlowModel buildFlowModelEntity() {
        List<FlowElement> flowElementList = Lists.newArrayList();
        {
            StartEvent startEvent = new StartEvent();
            startEvent.setKey("StartEvent_1fp6eeo");
            startEvent.setType(FlowElementType.START_EVENT);
            List<String> outgoings = new ArrayList<>();
            outgoings.add("SequenceFlow_1amuk7p");
            startEvent.setOutgoing(outgoings);
            flowElementList.add(startEvent);
        }
        {
            ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
            exclusiveGateway.setKey("ExclusiveGateway_1a21kmj");
            exclusiveGateway.setType(FlowElementType.EXCLUSIVE_GATEWAY);

            List<String> egIncomings = new ArrayList<>();
            egIncomings.add("SequenceFlow_1amuk7p");
            exclusiveGateway.setIncoming(egIncomings);

            List<String> egOutgoings = new ArrayList<>();
            egOutgoings.add("SequenceFlow_0cvpkym");
            egOutgoings.add("SequenceFlow_0cffuv2");
            exclusiveGateway.setOutgoing(egOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("hookInfoIds", "");
            exclusiveGateway.setProperties(properties);

            flowElementList.add(exclusiveGateway);
        }
        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_1angf8l");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_0cffuv2");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_1hi0u5n");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }
        {
            EndEvent endEvent = new EndEvent();
            endEvent.setKey("EndEvent_1fqnm5s");
            endEvent.setType(FlowElementType.END_EVENT);
            List<String> incomings = new ArrayList<>();
            incomings.add("SequenceFlow_0cvpkym");
            endEvent.setIncoming(incomings);
            flowElementList.add(endEvent);
        }
        {
            EndEvent endEvent = new EndEvent();
            endEvent.setKey("EndEvent_03sr1s4");
            endEvent.setType(FlowElementType.END_EVENT);
            List<String> incomings = new ArrayList<>();
            incomings.add("SequenceFlow_1hi0u5n");
            endEvent.setIncoming(incomings);
            flowElementList.add(endEvent);
        }

        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_1amuk7p");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("StartEvent_1fp6eeo");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("ExclusiveGateway_1a21kmj");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }

        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_0cvpkym");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ExclusiveGateway_1a21kmj");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("EndEvent_1fqnm5s");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "orderId.equals(\"123\")");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }

        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_0cffuv2");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("ExclusiveGateway_1a21kmj");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("UserTask_1angf8l");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "orderId.equals(\"456\")");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }
        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_1hi0u5n");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_1angf8l");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("EndEvent_03sr1s4");
            sequenceFlow1.setOutgoing(sfOutgoings);

            Map<String, Object> properties = new HashMap<>();
            properties.put("defaultConditions", "false");
            properties.put("conditionsequenceflow", "");
            sequenceFlow1.setProperties(properties);

            flowElementList.add(sequenceFlow1);
        }

        {
            UserTask userTask = new UserTask();
            userTask.setKey("UserTask_12h33j6");
            userTask.setType(FlowElementType.USER_TASK);

            List<String> utIncomings = new ArrayList<>();
            utIncomings.add("SequenceFlow_1hi0u5n");
            userTask.setIncoming(utIncomings);

            List<String> utOutgoings = new ArrayList<>();
            utOutgoings.add("SequenceFlow_0wbpwud");
            userTask.setOutgoing(utOutgoings);

            flowElementList.add(userTask);
        }

        {
            SequenceFlow sequenceFlow1 = new SequenceFlow();
            sequenceFlow1.setKey("SequenceFlow_0wbpwud");
            sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
            List<String> sfIncomings = new ArrayList<>();
            sfIncomings.add("UserTask_12h33j6");
            sequenceFlow1.setIncoming(sfIncomings);
            List<String> sfOutgoings = new ArrayList<>();
            sfOutgoings.add("EndEvent_03sr1s4");
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
