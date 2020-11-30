package com.xiaoju.uemc.turbo.engine.validator;

import com.xiaoju.uemc.turbo.engine.exception.ModelException;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

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
            Assert.assertTrue(access == true);
        } catch (ModelException e) {
            e.printStackTrace();
            Assert.assertTrue(access == true);
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
            Assert.assertTrue(access == false);
        } catch (ModelException e) {
            e.printStackTrace();
            Assert.assertTrue(access == false);
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
            Assert.assertTrue(access == true);
        } catch (ModelException e) {
            e.printStackTrace();
            Assert.assertTrue(access == true);
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
            Assert.assertTrue(access == false);
        } catch (ModelException e) {
            e.printStackTrace();
            Assert.assertTrue(access == false);
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
            Assert.assertTrue(access == false);
        } catch (ModelException e) {
            e.printStackTrace();
            Assert.assertTrue(access == false);
        }
    }
}