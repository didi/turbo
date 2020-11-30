package com.xiaoju.uemc.turbo.engine.dao;

import com.xiaoju.uemc.turbo.engine.entity.FlowDeploymentPO;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;


public class FlowDeploymentDAOTest extends BaseTest {

    @Resource
    FlowDeploymentDAO flowDeploymentDAO;

    @Test
    public void insert() {
        FlowDeploymentPO flowDeploymentPO = EntityBuilder.buildFlowDeploymentPO();
        int result = flowDeploymentDAO.insert(flowDeploymentPO);
        Assert.assertTrue(result == 1);
    }

    @Test
    public void selectByDeployId() {
        FlowDeploymentPO flowDeploymentPO = EntityBuilder.buildFlowDeploymentPO();
        flowDeploymentDAO.insert(flowDeploymentPO);
        String flowDeployId = flowDeploymentPO.getFlowDeployId();
        flowDeploymentPO = flowDeploymentDAO.selectByDeployId(flowDeployId);
        Assert.assertTrue(flowDeployId.equals(flowDeploymentPO.getFlowDeployId()));


    }

    @Test
    public void selectRecentByFlowModuleId() {
        FlowDeploymentPO flowDeploymentPO = EntityBuilder.buildFlowDeploymentPO();
        flowDeploymentDAO.insert(flowDeploymentPO);
        FlowDeploymentPO flowDeploymentPONew = EntityBuilder.buildFlowDeploymentPO();
        String flowModuleId1 = flowDeploymentPO.getFlowModuleId();
        flowDeploymentPONew.setFlowDeployId("newtest_id");
        flowDeploymentPONew.setFlowModuleId(flowModuleId1);
        flowDeploymentDAO.insert(flowDeploymentPONew);
        FlowDeploymentPO flowDeploymentPORes = flowDeploymentDAO.selectRecentByFlowModuleId(flowModuleId1);
        Assert.assertTrue(flowDeploymentPONew.getFlowDeployId().equals(flowDeploymentPORes.getFlowDeployId()));



    }
}