package com.didiglobal.turbo.engine.validator;

import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.exception.DefinitionException;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EndEventValidatorTest extends BaseTest {

    @Resource
    EndEventValidator endEventValidator;

    /**
     * Test endEvent's checkIncoming, while incoming is normal.
     */
    @Test
    public void checkIncomingAccess() {
        FlowElement endEvent = EntityBuilder.buildEndEvent();
        Map<String, FlowElement> map = new HashMap<>();
        map.put(endEvent.getKey(), endEvent);
        boolean access = false;
        try {
            endEventValidator.checkIncoming(map, endEvent);
            access = true;
        } catch (DefinitionException e) {
            LOGGER.error("", e);
        }
        Assert.assertTrue(access);

    }


    /**
     * Test endEvent's checkIncoming, while incoming is null.
     */
    @Test
    public void checkWithoutIncoming() {
        FlowElement endEventInvalid = EntityBuilder.buildEndEvent();
        endEventInvalid.setIncoming(null);
        Map<String, FlowElement> map = new HashMap<>();
        map.put(endEventInvalid.getKey(), endEventInvalid);
        boolean access = false;
        try {
            endEventValidator.checkIncoming(map, endEventInvalid);
            access = true;
        } catch (DefinitionException e) {
            Assert.assertEquals(ErrorEnum.ELEMENT_LACK_INCOMING.getErrNo(), e.getErrNo());
        }
        Assert.assertFalse(access);
    }

    /**
     * Test endEvent's checkOutgoing, while outgoing is normal.
     */
    @Test
    public void checkOutgoingAccess() {
        FlowElement endEvent = EntityBuilder.buildEndEvent();
        Map<String, FlowElement> map = new HashMap<>();
        map.put(endEvent.getKey(), endEvent);
        endEventValidator.checkOutgoing(map, endEvent);
    }

    /**
     * Test endEvent's checkOutgoing, while outgoing is not null.
     */
    @Test
    public void checkOutgoingIsNotNull() {
        FlowElement endEventInvalid = EntityBuilder.buildEndEvent();
        List<String> setOutgoing = new ArrayList<>();
        setOutgoing.add("sequenceFlow2");
        endEventInvalid.setOutgoing(setOutgoing);
        Map<String, FlowElement> map = new HashMap<>();
        map.put(endEventInvalid.getKey(), endEventInvalid);
        endEventValidator.checkOutgoing(map, endEventInvalid);
    }
}