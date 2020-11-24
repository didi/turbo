package com.xiaoju.uemc.turbo.engine.validator;

import com.xiaoju.uemc.turbo.engine.exception.ModelException;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SequenceFlowValidatorTest extends BaseTest {

    @Resource
    SequenceFlowValidator sequenceFlowValidator;

    @Test
    public void checkIncoming() {
        FlowElement sequenceFlow = EntityBuilder.buildSequenceFlow();
        Map<String, FlowElement> flowElementMap = new HashMap<>();
        flowElementMap.put(sequenceFlow.getKey(), sequenceFlow);
        try {
            sequenceFlowValidator.checkIncoming(flowElementMap, sequenceFlow);
        } catch (ModelException e) {
            e.printStackTrace();
        }
    }


    /**
     *  ELEMENT_TOO_MUCH_INCOMING
     *
     * */
    @Test
    public void checkIncomingVaild() {
        FlowElement sequenceFlow = EntityBuilder.buildSequenceFlow();
        List<String> sfIncomings = new ArrayList<>();
        sfIncomings.add("userTask2");
        sfIncomings.add("userTask1");
        sequenceFlow.setIncoming(sfIncomings);
        Map<String, FlowElement> flowElementMap = new HashMap<>();
        flowElementMap.put(sequenceFlow.getKey(), sequenceFlow);
        try {
            sequenceFlowValidator.checkIncoming(flowElementMap, sequenceFlow);
        } catch (ModelException e) {
            e.printStackTrace();
        }
    }

   /* @Test
    public void checkOutgoing() {

        FlowElement sequenceFlow = EntityBuilder.buildSequenceFlow();
        Map<String, FlowElement> flowElementMap = new HashMap<>();
        flowElementMap.put(sequenceFlow.getKey(), sequenceFlow);
        sequenceFlowValidator.checkOutgoing(flowElementMap, sequenceFlow);

    }

    *//**
     * Test without outgoing
     *
     * *//*
    @Test
    public void checkOutgoingVaild() {

        FlowElement sequenceFlow = EntityBuilder.buildSequenceFlowInvalid1();
        Map<String, FlowElement> flowElementMap = new HashMap<>();
        flowElementMap.put(sequenceFlow.getKey(), sequenceFlow);
        sequenceFlowValidator.checkOutgoing(flowElementMap, sequenceFlow);

    }*/
}