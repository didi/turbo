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
        Assert.assertTrue(result == 1);
    }

    @Test
    public void update() {
        FlowDefinitionPO flowDefinitionPO = EntityBuilder.buildFlowDefinitionPO();
        flowDefinitionMapper.insert(flowDefinitionPO);
        flowDefinitionPO = flowDefinitionMapper.selectByFlowModuleId(flowDefinitionPO.getFlowModuleId());
        flowDefinitionPO.setStatus(3);
        int result = flowDefinitionMapper.updateById(flowDefinitionPO);
        Assert.assertTrue(result == 1);
        flowDefinitionPO = flowDefinitionMapper.selectByFlowModuleId(flowDefinitionPO.getFlowModuleId());
        Assert.assertTrue(flowDefinitionPO.getStatus().equals(3));
    }

    @Test
    public void selectByFlowModuleId() {
        FlowDefinitionPO flowDefinitionPO = EntityBuilder.buildFlowDefinitionPO();
        int result = flowDefinitionMapper.insert(flowDefinitionPO);
        Assert.assertTrue(result == 1);
        String flowModuleId = flowDefinitionPO.getFlowModuleId();
        FlowDefinitionPO queryFlowDefinitionPO = flowDefinitionMapper.selectByFlowModuleId(flowModuleId);
        LOGGER.info("selectByModuleIdTest.||flowDefinitionPO={}", flowDefinitionPO);
        Assert.assertTrue(flowModuleId.equals(queryFlowDefinitionPO.getFlowModuleId()));

    }
}
