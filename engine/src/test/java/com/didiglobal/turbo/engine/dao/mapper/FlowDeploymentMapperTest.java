package com.didiglobal.turbo.engine.dao.mapper;

import com.didiglobal.turbo.engine.common.FlowDeploymentStatus;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

public class FlowDeploymentMapperTest extends BaseTest {

    @Resource
    private FlowDeploymentMapper flowDeploymentMapper;

    @Test
    public void insert() {
        FlowDeploymentPO flowDeploymentPO = EntityBuilder.buildFlowDeploymentPO();
        flowDeploymentPO.setFlowDeployId("testFlowDeployId_" + System.currentTimeMillis());
        int result = flowDeploymentMapper.insert(flowDeploymentPO);
        Assert.assertEquals(1, result);
    }

    @Test
    public void selectByDeployId() {
        FlowDeploymentPO flowDeploymentPO = EntityBuilder.buildFlowDeploymentPO();
        flowDeploymentPO.setStatus(FlowDeploymentStatus.DEPLOYED);
        flowDeploymentPO.setFlowDeployId("testFlowDeployId_" + System.currentTimeMillis());
        flowDeploymentMapper.insert(flowDeploymentPO);
        String flowDeployId = flowDeploymentPO.getFlowDeployId();
        flowDeploymentPO = flowDeploymentMapper.selectByDeployId(flowDeployId);
        Assert.assertEquals(flowDeployId, flowDeploymentPO.getFlowDeployId());
    }

    @Test
    public void selectByModuleId() {
        FlowDeploymentPO flowDeploymentPO = EntityBuilder.buildFlowDeploymentPO();
        flowDeploymentPO.setStatus(FlowDeploymentStatus.DEPLOYED);
        flowDeploymentPO.setFlowDeployId("testFlowDeployId_" + System.currentTimeMillis());
        flowDeploymentMapper.insert(flowDeploymentPO);
        FlowDeploymentPO flowDeploymentPONew = EntityBuilder.buildFlowDeploymentPO();
        String flowModuleId1 = flowDeploymentPO.getFlowModuleId();
        flowDeploymentPONew.setFlowModuleId(flowModuleId1);
        flowDeploymentPONew.setStatus(FlowDeploymentStatus.DEPLOYED);
        flowDeploymentPO.setFlowDeployId("testFlowDeployId_" + System.currentTimeMillis());
        flowDeploymentMapper.insert(flowDeploymentPONew);
        FlowDeploymentPO flowDeploymentPORes = flowDeploymentMapper.selectByModuleId(flowModuleId1);
        Assert.assertEquals(flowDeploymentPONew.getFlowDeployId(), flowDeploymentPORes.getFlowDeployId());
    }
}
