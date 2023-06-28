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


public class StartEventValidatorTest extends BaseTest {

    @Resource
    StartEventValidator startEventValidator;

    /**
     * Test startEvent's incoming, while normal.
     */
    @Test
    public void checkIncomingAccess() {
        FlowElement startEvent = EntityBuilder.buildStartEvent();
        Map<String, FlowElement> map = new HashMap<>();
        map.put(startEvent.getKey(), startEvent);
        startEventValidator.checkIncoming(map, startEvent);
    }

    /**
     * Test startEvent's incoming, when there are too many incomings.
     */
    @Test
    public void checkTooManyIncoming() {
        FlowElement startEvent = EntityBuilder.buildStartEvent();
        List<String> incomings = new ArrayList<>();
        incomings.add("sequence");
        startEvent.setIncoming(incomings);
        Map<String, FlowElement> map = new HashMap<>();
        map.put("startEvent", startEvent);
        // A warning log will be printed when the startEvent's incoming exists.
        startEventValidator.checkIncoming(map, startEvent);
    }

    /**
     * Test startEvent's incoming, while normal.
     */
    @Test
    public void checkOutgoingAccess() {
        FlowElement startEvent = EntityBuilder.buildStartEvent();
        Map<String, FlowElement> map = new HashMap<>();
        map.put(startEvent.getKey(), startEvent);
        boolean access = false;
        try {
            startEventValidator.checkOutgoing(map, startEvent);
            access = true;
        } catch (DefinitionException e) {
            LOGGER.error("", e);
        }
        Assert.assertTrue(access);
    }

    /**
     * Test startEvent's outgoing, when the outgoing don't exist.
     */
    @Test
    public void checkEmptyOutgoing() {
        FlowElement startEvent = EntityBuilder.buildStartEvent();
        List<String> outgoings = new ArrayList<>();
        startEvent.setOutgoing(outgoings);
        Map<String, FlowElement> map = new HashMap<>();
        map.put("startEvent", startEvent);
        boolean access = false;
        try {
            startEventValidator.checkOutgoing(map, startEvent);
            access = true;
        } catch (DefinitionException e) {
            Assert.assertEquals(ErrorEnum.ELEMENT_LACK_OUTGOING.getErrNo(), e.getErrNo());
        }
        Assert.assertFalse(access);
    }
}