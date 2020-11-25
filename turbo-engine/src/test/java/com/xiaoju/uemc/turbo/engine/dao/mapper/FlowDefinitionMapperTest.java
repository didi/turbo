package com.xiaoju.uemc.turbo.engine.dao.mapper;

import com.xiaoju.uemc.turbo.engine.entity.FlowDefinitionPO;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Stefanie on 2019/11/29.
 */
public class FlowDefinitionMapperTest extends BaseTest {

    @Autowired
    private FlowDefinitionMapper flowDefinitionMapper;

    @Test
    public void insert() {
        FlowDefinitionPO flowDefinitionPO = EntityBuilder.buildFlowDefinitionPO();
        int result = flowDefinitionMapper.insert(flowDefinitionPO);
        LOGGER.info("insert.result={}", result);
        Assert.assertTrue(result == 1);
    }

    @Test
    public void update() {
        FlowDefinitionPO flowDefinitionPO = EntityBuilder.buildFlowDefinitionPO();
        flowDefinitionMapper.insert(flowDefinitionPO);
        flowDefinitionPO = flowDefinitionMapper.selectByFlowModuleId(flowDefinitionPO.getFlowModuleId());
        flowDefinitionPO.setStatus(3);
        int result = flowDefinitionMapper.updateById(flowDefinitionPO);
        LOGGER.info("update.result={}", result);
        flowDefinitionPO = flowDefinitionMapper.selectByFlowModuleId(flowDefinitionPO.getFlowModuleId());

        Assert.assertTrue(flowDefinitionPO.getStatus().equals(3));
    }

    @Test
    public void selectByFlowModuleId() {
        String flowModuleId = "80097e8b-298f-11eb-ba2c-6eca5e511091";
        //String flowModele1 = "";
        FlowDefinitionPO flowDefinitionPO = flowDefinitionMapper.selectByFlowModuleId(flowModuleId);
        LOGGER.info("selectByModuleIdTest.||flowDefinitionPO={}", flowDefinitionPO);
        Assert.assertTrue(flowModuleId.equals(flowDefinitionPO.getFlowModuleId()));

    }
}
