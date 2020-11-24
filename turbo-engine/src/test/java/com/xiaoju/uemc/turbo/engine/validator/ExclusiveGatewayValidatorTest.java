package com.xiaoju.uemc.turbo.engine.validator;

import com.xiaoju.uemc.turbo.engine.exception.ModelException;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ExclusiveGatewayValidatorTest extends BaseTest {


    @Resource
    ExclusiveGatewayValidator exclusiveGatewayValidator;

    @Test
    public void checkOutgoing() {
        FlowElement exclusiveGateway= EntityBuilder.buildExclusiveGateway();
        FlowElement outgoningSequence = EntityBuilder.buildSequenceFlow2();
        FlowElement outgoningSequence1 = EntityBuilder.buildSequenceFlow3();

        Map<String, FlowElement> map = new HashMap<>();
        map.put(exclusiveGateway.getKey(), exclusiveGateway);
        map.put(outgoningSequence.getKey(), outgoningSequence);
        map.put(outgoningSequence1.getKey(), outgoningSequence1);
        try {
            exclusiveGatewayValidator.checkOutgoing(map, exclusiveGateway);
        } catch (ModelException e) {
            e.printStackTrace();
        }
    }

    /**
     * EMPTY_SEQUENCE_OUTGOING
     *
     */

    @Test
    public void checkOutgoingInvalid() {
        FlowElement exclusiveGateway= EntityBuilder.buildExclusiveGateway();
        FlowElement outgoningSequence = EntityBuilder.buildSequenceFlow2();
        Map<String, Object> properties = new HashMap<>();
        properties.put("defaultConditions", "false");
        properties.put("conditionsequenceflow", "");
        outgoningSequence.setProperties(properties);
        FlowElement outgoningSequence1 = EntityBuilder.buildSequenceFlow3();
        Map<String, FlowElement> map = new HashMap<>();
        map.put(exclusiveGateway.getKey(), exclusiveGateway);
        map.put(outgoningSequence.getKey(), outgoningSequence);
        map.put(outgoningSequence1.getKey(), outgoningSequence1);
        try {
            exclusiveGatewayValidator.checkOutgoing(map, exclusiveGateway);
        } catch (ModelException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * TOO_MANY_DEFAULT_SEQUENCE
     */

    @Test
    public void checkOutgoingInvalid1() {
        FlowElement exclusiveGateway= EntityBuilder.buildExclusiveGateway();
        FlowElement outgoningSequence = EntityBuilder.buildSequenceFlow2();
        Map<String, Object> properties = new HashMap<>();
        properties.put("defaultConditions", "true");
        properties.put("conditionsequenceflow", "");
        outgoningSequence.setProperties(properties);
        FlowElement outgoningSequence1 = EntityBuilder.buildSequenceFlow3();
        Map<String, Object> properties1 = new HashMap<>();
        properties.put("defaultConditions", "true");
        properties.put("conditionsequenceflow", "");
        outgoningSequence1.setProperties(properties);
        Map<String, FlowElement> map = new HashMap<>();
        map.put(exclusiveGateway.getKey(), exclusiveGateway);
        map.put(outgoningSequence.getKey(), outgoningSequence);
        map.put(outgoningSequence1.getKey(), outgoningSequence1);
        try {
            exclusiveGatewayValidator.checkOutgoing(map, exclusiveGateway);
        } catch (ModelException e) {
            e.printStackTrace();
        }
    }
}