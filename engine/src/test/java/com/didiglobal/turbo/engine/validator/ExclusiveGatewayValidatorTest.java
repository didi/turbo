package com.didiglobal.turbo.engine.validator;

import com.didiglobal.turbo.engine.common.ErrorEnum;
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
     */
    @Test
    public void checkIncomingAccess() {
        FlowElement exclusiveGateway = EntityBuilder.buildExclusiveGateway();
        FlowElement outGoingSequence = EntityBuilder.buildSequenceFlow2();
        FlowElement outGoingSequence1 = EntityBuilder.buildSequenceFlow3();
        Map<String, FlowElement> map = new HashMap<>();
        map.put(exclusiveGateway.getKey(), exclusiveGateway);
        map.put(outGoingSequence.getKey(), outGoingSequence);
        map.put(outGoingSequence1.getKey(), outGoingSequence1);
        boolean access = false;
        try {
            exclusiveGatewayValidator.checkIncoming(map, exclusiveGateway);
            access = true;
        } catch (DefinitionException e) {
            LOGGER.error("", e);
        }
        Assert.assertTrue(access);
    }

    /**
     * Test exclusiveGateway's checkIncoming, while incoming is null.
     */
    @Test
    public void checkWithoutIncoming() {
        FlowElement exclusiveGateway = EntityBuilder.buildExclusiveGateway();
        FlowElement outGoingSequence = EntityBuilder.buildSequenceFlow2();
        FlowElement outGoingSequence1 = EntityBuilder.buildSequenceFlow3();
        exclusiveGateway.setIncoming(null);
        Map<String, FlowElement> map = new HashMap<>();
        map.put(exclusiveGateway.getKey(), exclusiveGateway);
        map.put(outGoingSequence.getKey(), outGoingSequence);
        map.put(outGoingSequence1.getKey(), outGoingSequence1);
        boolean access = false;
        try {
            exclusiveGatewayValidator.checkIncoming(map, exclusiveGateway);
            access = true;
        } catch (DefinitionException e) {
            Assert.assertEquals(ErrorEnum.ELEMENT_LACK_INCOMING.getErrNo(), e.getErrNo());
        }
        Assert.assertFalse(access);
    }

    /**
     * Test exclusiveGateway's checkOutgoing, while exclusiveGateway's outgoing is normal.
     */
    @Test
    public void checkOutgoingAccess() {
        FlowElement exclusiveGateway = EntityBuilder.buildExclusiveGateway();
        FlowElement outGoingSequence = EntityBuilder.buildSequenceFlow2();
        FlowElement outGoingSequence1 = EntityBuilder.buildSequenceFlow3();
        Map<String, FlowElement> map = new HashMap<>();
        map.put(exclusiveGateway.getKey(), exclusiveGateway);
        map.put(outGoingSequence.getKey(), outGoingSequence);
        map.put(outGoingSequence1.getKey(), outGoingSequence1);
        boolean access = false;
        try {
            exclusiveGatewayValidator.checkOutgoing(map, exclusiveGateway);
            access = true;
        } catch (DefinitionException e) {
            LOGGER.error("", e);
        }
        Assert.assertTrue(access);
    }

    /**
     * Test exclusiveGateway's checkOutgoing, while exclusiveGateway's outgoing is empty.
     */
    @Test
    public void checkEmptyOutgoing() {
        FlowElement exclusiveGateway = EntityBuilder.buildExclusiveGateway();
        FlowElement outGoingSequence = EntityBuilder.buildSequenceFlow2();
        Map<String, Object> properties = new HashMap<>();
        properties.put("defaultConditions", "false");
        properties.put("conditionsequenceflow", "");
        outGoingSequence.setProperties(properties);
        FlowElement outgoningSequence1 = EntityBuilder.buildSequenceFlow3();
        Map<String, FlowElement> map = new HashMap<>();
        map.put(exclusiveGateway.getKey(), exclusiveGateway);
        map.put(outGoingSequence.getKey(), outGoingSequence);
        map.put(outgoningSequence1.getKey(), outgoningSequence1);
        boolean access = false;
        try {
            exclusiveGatewayValidator.checkOutgoing(map, exclusiveGateway);
            access = true;
        } catch (DefinitionException e) {
            Assert.assertEquals(ErrorEnum.EMPTY_SEQUENCE_OUTGOING.getErrNo(), e.getErrNo());
        }
        Assert.assertFalse(access);
    }

    /**
     * Test exclusiveGateway's checkOutgoing, while too many default sequence.
     */
    @Test
    public void checkTooManyDefaultSequenceOutgoing() {
        FlowElement exclusiveGateway = EntityBuilder.buildExclusiveGateway();

        FlowElement outGoingSequence = EntityBuilder.buildSequenceFlow2();
        Map<String, Object> properties = new HashMap<>();
        properties.put("defaultConditions", "true");
        properties.put("conditionsequenceflow", "");
        outGoingSequence.setProperties(properties);

        FlowElement outGoingSequence1 = EntityBuilder.buildSequenceFlow3();
        Map<String, Object> properties1 = new HashMap<>();
        properties1.put("defaultConditions", "true");
        properties1.put("conditionsequenceflow", "");
        outGoingSequence1.setProperties(properties1);

        Map<String, FlowElement> map = new HashMap<>();
        map.put(exclusiveGateway.getKey(), exclusiveGateway);
        map.put(outGoingSequence.getKey(), outGoingSequence);
        map.put(outGoingSequence1.getKey(), outGoingSequence1);

        boolean access = false;
        try {
            exclusiveGatewayValidator.checkOutgoing(map, exclusiveGateway);
            access = true;
        } catch (DefinitionException e) {
            Assert.assertEquals(ErrorEnum.TOO_MANY_DEFAULT_SEQUENCE.getErrNo(), e.getErrNo());
        }
        Assert.assertFalse(access);
    }
}