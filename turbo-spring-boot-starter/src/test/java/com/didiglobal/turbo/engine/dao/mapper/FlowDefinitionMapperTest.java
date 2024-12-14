package com.didiglobal.turbo.engine.dao.mapper;

import com.didiglobal.turbo.engine.entity.FlowDefinitionPO;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import com.didiglobal.turbo.mybatis.dao.mapper.FlowDefinitionMapper;
import com.didiglobal.turbo.mybatis.entity.FlowDefinitionEntity;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FlowDefinitionMapperTest extends BaseTest {

    @Autowired
    private FlowDefinitionMapper flowDefinitionMapper;

    @Test
    public void insert() {
        FlowDefinitionPO flowDefinitionPO = EntityBuilder.buildFlowDefinitionPO();
        flowDefinitionPO.setFlowModuleId("testFlowModuleId_" + System.currentTimeMillis());
        int result = flowDefinitionMapper.insert(FlowDefinitionEntity.of(flowDefinitionPO));
        Assert.assertEquals(1, result);
    }

    @Test
    public void update() {
        FlowDefinitionPO flowDefinitionPO = EntityBuilder.buildFlowDefinitionPO();
        flowDefinitionPO.setFlowModuleId("testFlowModuleId_" + System.currentTimeMillis());
        flowDefinitionMapper.insert(FlowDefinitionEntity.of(flowDefinitionPO));
        flowDefinitionPO = flowDefinitionMapper.selectByFlowModuleId(flowDefinitionPO.getFlowModuleId());
        flowDefinitionPO.setStatus(3);
        int result = flowDefinitionMapper.updateById(FlowDefinitionEntity.of(flowDefinitionPO));
        Assert.assertEquals(1, result);
        flowDefinitionPO = flowDefinitionMapper.selectByFlowModuleId(flowDefinitionPO.getFlowModuleId());
        Assert.assertEquals(3, (int) flowDefinitionPO.getStatus());
    }

    @Test
    public void selectByFlowModuleId() {
        FlowDefinitionPO flowDefinitionPO = EntityBuilder.buildFlowDefinitionPO();
        flowDefinitionPO.setFlowModuleId("testFlowModuleId_" + System.currentTimeMillis());
        int result = flowDefinitionMapper.insert(FlowDefinitionEntity.of(flowDefinitionPO));
        Assert.assertEquals(1, result);
        String flowModuleId = flowDefinitionPO.getFlowModuleId();
        FlowDefinitionPO queryFlowDefinitionPO = flowDefinitionMapper.selectByFlowModuleId(flowModuleId);
        LOGGER.info("selectByModuleIdTest.||flowDefinitionPO={}", flowDefinitionPO);
        Assert.assertEquals(flowModuleId, queryFlowDefinitionPO.getFlowModuleId());
    }
}
