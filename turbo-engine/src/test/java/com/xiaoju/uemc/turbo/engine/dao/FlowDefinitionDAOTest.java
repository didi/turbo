package com.xiaoju.uemc.turbo.engine.dao;

import com.xiaoju.uemc.turbo.engine.entity.FlowDefinitionPO;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Stefanie on 2019/11/25.
 */
public class FlowDefinitionDAOTest extends BaseTest {

    @Autowired
    private FlowDefinitionDAO flowDefinitionDAO;

    @Test
    public void insertTest() {
        FlowDefinitionPO flowDefinitionPO = EntityBuilder.buildFlowDefinitionPO();
        int result = flowDefinitionDAO.insert(flowDefinitionPO);
        LOGGER.info("insertTest.result={}", result);
        Assert.assertTrue(result == 1);
    }

    @Test
    public void updateByModuleIdTest() {
        FlowDefinitionPO flowDefinitionPO = EntityBuilder.buildFlowDefinitionPO();
        int result = flowDefinitionDAO.insert(flowDefinitionPO);
        Assert.assertTrue(result == 1);
        int result1 = flowDefinitionDAO.updateByModuleId(flowDefinitionPO);
        LOGGER.info("updateByModuleIdTest.||result={}", result);
        Assert.assertTrue(result1 == 1);
    }

    @Test
    public void selectByModuleIdTest() {
        FlowDefinitionPO flowDefinitionPO = EntityBuilder.buildFlowDefinitionPO();
        int result = flowDefinitionDAO.insert(flowDefinitionPO);
        Assert.assertTrue(result == 1);
        String flowModuleId = flowDefinitionPO.getFlowModuleId();
        FlowDefinitionPO queryFlowDefinitionPO = flowDefinitionDAO.selectByModuleId(flowDefinitionPO.getFlowModuleId());
        LOGGER.info("selectByModuleIdTest.||flowDefinitionPO={}", flowDefinitionPO);
        Assert.assertTrue(flowModuleId.equals(queryFlowDefinitionPO.getFlowModuleId()));
    }
}