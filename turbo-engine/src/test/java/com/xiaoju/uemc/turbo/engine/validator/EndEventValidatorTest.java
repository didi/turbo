package com.xiaoju.uemc.turbo.engine.validator;

import com.xiaoju.uemc.turbo.engine.exception.ModelException;
import com.xiaoju.uemc.turbo.engine.model.EndEvent;
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

public class EndEventValidatorTest extends BaseTest {

    @Resource EndEventValidator endEventValidator;

    /**
     * Test endEvent's checkIncoming, while incoming is normal.
     *
     */
    @Test
    public void checkIncomingAcess() {
        FlowElement endEvent = EntityBuilder.buildEndEvent();
        Map<String, FlowElement> map = new HashMap<>();
        map.put(endEvent.getKey(), endEvent);
        try {
            endEventValidator.checkIncoming(map, endEvent);
        } catch (ModelException e) {
            e.printStackTrace();
        }
    }


    /**
     * Test endEvent's checkIncoming, while incoming is null.
     *
     */
    @Test
    public void checkWithoutIncoming() {
        FlowElement endEventInvalid = EntityBuilder.buildEndEvent();
        endEventInvalid.setIncoming(null);
        Map<String, FlowElement> map = new HashMap<>();
        map.put(endEventInvalid.getKey(), endEventInvalid);
        try {
            endEventValidator.checkIncoming(map, endEventInvalid);
        } catch (ModelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test endEvent's checkOutgoing, while outgoing is normal.
     *
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
     *
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