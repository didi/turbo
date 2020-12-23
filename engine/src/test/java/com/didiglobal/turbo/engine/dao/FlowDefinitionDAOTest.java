package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.FlowDefinitionPO;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FlowDefinitionDAOTest extends BaseTest {

    @Autowired
    private FlowDefinitionDAO flowDefinitionDAO;

    @Test
    public void insertTest() {
        FlowDefinitionPO flowDefinitionPO = EntityBuilder.buildFlowDefinitionPO();
        flowDefinitionPO.setFlowModuleId("testFlowModuleId_" + System.currentTimeMillis());
        int result = flowDefinitionDAO.insert(flowDefinitionPO);
        LOGGER.info("insertTest.result={}", result);
        Assert.assertTrue(result == 1);
    }

    @Test
    public void updateByModuleIdTest() {
        FlowDefinitionPO flowDefinitionPO = EntityBuilder.buildFlowDefinitionPO();
        flowDefinitionPO.setFlowModuleId("testFlowModuleId_" + System.currentTimeMillis());
        int insertResult = flowDefinitionDAO.insert(flowDefinitionPO);
        Assert.assertTrue(insertResult == 1);
        int updateResult = flowDefinitionDAO.updateByModuleId(flowDefinitionPO);
        LOGGER.info("updateByModuleIdTest.||result={}", updateResult);
        Assert.assertTrue(updateResult == 1);
    }

    @Test
    public void selectByModuleIdTest() {
        FlowDefinitionPO flowDefinitionPO = EntityBuilder.buildFlowDefinitionPO();
        flowDefinitionPO.setFlowModuleId("testFlowModuleId_" + System.currentTimeMillis());
        int result = flowDefinitionDAO.insert(flowDefinitionPO);
        Assert.assertTrue(result == 1);
        String flowModuleId = flowDefinitionPO.getFlowModuleId();
        FlowDefinitionPO queryFlowDefinitionPO = flowDefinitionDAO.selectByModuleId(flowDefinitionPO.getFlowModuleId());
        LOGGER.info("selectByModuleIdTest.||flowDefinitionPO={}", flowDefinitionPO);
        Assert.assertTrue(flowModuleId.equals(queryFlowDefinitionPO.getFlowModuleId()));
    }
}
