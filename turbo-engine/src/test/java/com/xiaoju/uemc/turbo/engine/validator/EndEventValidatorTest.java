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

    @Test
    public void checkOutgoing() {
        FlowElement endEvent = EntityBuilder.buildEndEvent();
        Map<String, FlowElement> map = new HashMap<>();
        map.put(endEvent.getKey(), endEvent);
        endEventValidator.checkOutgoing(map, endEvent);
    }


    /**
     *
     *  ELEMENT_TOO_MUCH_OUTGOING
     */

    @Test
    public void checkOutgoingInvalid() {
        FlowElement endEventInvalid = EntityBuilder.buildEndEvent();
        List<String> setOutgoing = new ArrayList<>();
        setOutgoing.add("sequenceFlow2");
        endEventInvalid.setOutgoing(setOutgoing);
        Map<String, FlowElement> map = new HashMap<>();
        map.put(endEventInvalid.getKey(), endEventInvalid);
        endEventValidator.checkOutgoing(map, endEventInvalid);
    }
}