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
//        flowDefinitionPO.setFlowModuleId("testFlowModuleId_1575185546972");
        int result = flowDefinitionDAO.insert(flowDefinitionPO);
        LOGGER.info("insertTest.result={}", result);
        Assert.assertTrue(result == 1);
    }

    @Test
    public void updateByModuleIdTest() {
        FlowDefinitionPO flowDefinitionPO = EntityBuilder.buildFlowDefinitionPO();
        flowDefinitionPO.setFlowModuleId("80097e8b-298f-11eb-ba2c-6eca5e511091");
        //flowDefinitionPO.setFlowModuleId("");
        int result = flowDefinitionDAO.updateByModuleId(flowDefinitionPO);
        LOGGER.info("updateByModuleIdTest.||result={}", result);
        Assert.assertTrue(result == 1);
    }

    @Test
    public void selectByModuleIdTest() {
        String flowModuleId = "80097e8b-298f-11eb-ba2c-6eca5e511091";
        String flowModele1 = "";
        FlowDefinitionPO flowDefinitionPO = flowDefinitionDAO.selectByModuleId(flowModuleId);
        LOGGER.info("selectByModuleIdTest.||flowDefinitionPO={}", flowDefinitionPO);
        Assert.assertTrue(flowModuleId.equals(flowDefinitionPO.getFlowModuleId()));
    }
}