package com.didiglobal.turbo.engine.validator;

import com.didiglobal.turbo.engine.exception.DefinitionException;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


public class ExclusiveGatewayValidatorTest extends BaseTest {


    @Resource
    ExclusiveGatewayValidator exclusiveGatewayValidator;

    /**
     * Test exclusiveGateway's checkIncoming, while exclusiveGateway's incoming is normal.
     *
     */
    @Test
    public void checkIncomingAccess() {
        FlowElement exclusiveGateway= EntityBuilder.buildExclusiveGateway();
        FlowElement outgoningSequence = EntityBuilder.buildSequenceFlow2();
        FlowElement outgoningSequence1 = EntityBuilder.buildSequenceFlow3();
        Map<String, FlowElement> map = new HashMap<>();
        map.put(exclusiveGateway.getKey(), exclusiveGateway);
        map.put(outgoningSequence.getKey(), outgoningSequence);
        map.put(outgoningSequence1.getKey(), outgoningSequence1);
        boolean access = false;
        try {
            exclusiveGatewayValidator.checkIncoming(map, exclusiveGateway);
            access = true;
            Assert.assertTrue(access);
        } catch (DefinitionException e) {
            LOGGER.error("", e);
            Assert.assertTrue(access);
        }
    }

    /**
     * Test exclusiveGateway's checkIncoming, while incoming is null.
     *
     */
    @Test
    public void checkWithoutIncoming() {
        FlowElement exclusiveGateway= EntityBuilder.buildExclusiveGateway();
        FlowElement outgoningSequence = EntityBuilder.buildSequenceFlow2();
        FlowElement outgoningSequence1 = EntityBuilder.buildSequenceFlow3();
        exclusiveGateway.setIncoming(null);
        Map<String, FlowElement> map = new HashMap<>();
        map.put(exclusiveGateway.getKey(), exclusiveGateway);
        map.put(outgoningSequence.getKey(), outgoningSequence);
        map.put(outgoningSequence1.getKey(), outgoningSequence1);
        boolean access = false;
        try {
            exclusiveGatewayValidator.checkIncoming(map, exclusiveGateway);
            access = true;
            Assert.assertFalse(access);
        } catch (DefinitionException e) {
            LOGGER.error("", e);
            Assert.assertFalse(access);
        }
    }

    /**
     * Test exclusiveGateway's checkOutgoing, while exclusiveGateway's outgoing is normal.
     *
     */
    @Test
    public void checkOutgoingAccess() {
        FlowElement exclusiveGateway= EntityBuilder.buildExclusiveGateway();
        FlowElement outgoningSequence = EntityBuilder.buildSequenceFlow2();
        FlowElement outgoningSequence1 = EntityBuilder.buildSequenceFlow3();
        Map<String, FlowElement> map = new HashMap<>();
        map.put(exclusiveGateway.getKey(), exclusiveGateway);
        map.put(outgoningSequence.getKey(), outgoningSequence);
        map.put(outgoningSequence1.getKey(), outgoningSequence1);
        boolean access = false;
        try {
            exclusiveGatewayValidator.checkOutgoing(map, exclusiveGateway);
            access = true;
            Assert.assertTrue(access);
        } catch (DefinitionException e) {
            LOGGER.error("", e);
            Assert.assertTrue(access);
        }
    }

    /**
     * Test exclusiveGateway's checkOutgoing, while exclusiveGateway's outgoing is empty.
     *
     */
    @Test
    public void checkEmptyOutgoing() {
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
        boolean access = false;
        try {
            exclusiveGatewayValidator.checkOutgoing(map, exclusiveGateway);
            access = true;
            Assert.assertFalse(access);
        } catch (DefinitionException e) {
            LOGGER.error("", e);
            Assert.assertFalse(access);
        }
    }

    /**
     * Test exclusiveGateway's checkOutgoing, while too many default sequence.
     *
     */
    @Test
    public void checkTooManySequenceOutgoig() {
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
        boolean access = false;
        try {
            exclusiveGatewayValidator.checkOutgoing(map, exclusiveGateway);
            access = true;
            Assert.assertFalse(access);
        } catch (DefinitionException e) {
            LOGGER.error("", e);
            Assert.assertFalse(access);
        }
    }
}