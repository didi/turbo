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
        Assert.assertEquals(1, result);
    }

    @Test
    public void updateByModuleIdTest() {
        FlowDefinitionPO flowDefinitionPO = EntityBuilder.buildFlowDefinitionPO();
        flowDefinitionPO.setFlowModuleId("testFlowModuleId_" + System.currentTimeMillis());
        int insertResult = flowDefinitionDAO.insert(flowDefinitionPO);
        Assert.assertEquals(1, insertResult);

        int updateResult = flowDefinitionDAO.updateByModuleId(flowDefinitionPO);
        LOGGER.info("updateByModuleIdTest.||result={}", updateResult);
        Assert.assertEquals(1, updateResult);
    }

    @Test
    public void selectByModuleIdTest() {
        FlowDefinitionPO flowDefinitionPO = EntityBuilder.buildFlowDefinitionPO();
        flowDefinitionPO.setFlowModuleId("testFlowModuleId_" + System.currentTimeMillis());
        int result = flowDefinitionDAO.insert(flowDefinitionPO);
        Assert.assertEquals(1, result);

        String flowModuleId = flowDefinitionPO.getFlowModuleId();
        FlowDefinitionPO queryFlowDefinitionPO = flowDefinitionDAO.selectByModuleId(flowDefinitionPO.getFlowModuleId());
        LOGGER.info("selectByModuleIdTest.||flowDefinitionPO={}", flowDefinitionPO);
        Assert.assertEquals(flowModuleId, queryFlowDefinitionPO.getFlowModuleId());
    }
}
