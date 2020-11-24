package com.xiaoju.uemc.turbo.engine.validator;

import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.model.StartEvent;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class StartEventValidatorTest extends BaseTest {

    @Resource StartEventValidator startEventValidator;

    @Test
    public void checkIncoming() {
        FlowElement startEvent = EntityBuilder.buildStartEvent();
        Map<String, FlowElement> map = new HashMap<>();
        map.put(startEvent.getKey(), startEvent);
        startEventValidator.checkIncoming(map, startEvent);
    }
    @Test
    public void checkIncomingVaild() {
        FlowElement startEventVaild = EntityBuilder.buildStartEventInvalid();
        Map<String, FlowElement> map = new HashMap<>();
        map.put("startEvent", startEventVaild);
        startEventValidator.checkIncoming(map, startEventVaild);
    }
}