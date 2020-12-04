package com.xiaoju.uemc.turbo.engine.validator;

import com.xiaoju.uemc.turbo.engine.exception.DefinitionException;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


public class UserTaskValidatorTest extends BaseTest {
    @Resource
    UserTaskValidator userTaskValidator;

    /**
     * Check userTask's incoming, whlile normal.
     *
     */
    @Test
    public void checkIncomingAccess() {
        FlowElement userTask = EntityBuilder.buildUserTask();
        Map<String, FlowElement> map = new HashMap<>();
        map.put(userTask.getKey(), userTask);
        boolean access = false;
        try {
            userTaskValidator.checkIncoming(map, userTask);
            access = true;
            Assert.assertTrue(access == true);
        } catch (DefinitionException e) {
            e.printStackTrace();
            Assert.assertTrue(access == true);
        }
    }
    /**
     * Check userTask's incoming, while incoming is lack.
     *
     */
    @Test
    public void checkWithoutIncoming() {
        FlowElement userTask = EntityBuilder.buildUserTask();
        userTask.setIncoming(null);
        Map<String, FlowElement> map = new HashMap<>();
        map.put(userTask.getKey(), userTask);
        boolean access = false;
        try {
            userTaskValidator.checkIncoming(map, userTask);
            access = true;
            Assert.assertTrue(access == false);
        } catch (DefinitionException e) {
            e.printStackTrace();
            Assert.assertTrue(access == false);
        }
    }


    /**
     * Check userTask's outgoing, whlile normal.
     *
     */
    @Test
    public void checkOutgoingAccess() {
        FlowElement userTask = EntityBuilder.buildUserTask();
        Map<String, FlowElement> map = new HashMap<>();
        map.put(userTask.getKey(), userTask);
        boolean access = false;
        try {
            userTaskValidator.checkOutgoing(map, userTask);
            access = true;
            Assert.assertTrue(access == true);
        } catch (DefinitionException e) {
            e.printStackTrace();
            Assert.assertTrue(access == true);
        }
    }

    /**
     * Check userTask's outgoing, while outgoing is lack.
     *
     */
    @Test
    public void checkWithoutOutgoing() {
        FlowElement userTask = EntityBuilder.buildUserTask();
        userTask.setOutgoing(null);
        Map<String, FlowElement> map = new HashMap<>();
        map.put(userTask.getKey(), userTask);
        boolean access = false;
        try {
            userTaskValidator.checkOutgoing(map, userTask);
            access = true;
            Assert.assertTrue(access == false);
        } catch (DefinitionException e) {
            e.printStackTrace();
            Assert.assertTrue(access == false);
        }
    }
}