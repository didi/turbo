package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.common.FlowInstanceStatus;
import com.didiglobal.turbo.engine.entity.FlowInstancePO;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

public class ProcessInstanceDAOTest extends BaseTest {

    @Resource
    private ProcessInstanceDAO processInstanceDAO;

    @Test
    public void insert() {
        FlowInstancePO flowInstancePO = EntityBuilder.buildDynamicFlowInstancePO();
        int result = processInstanceDAO.insert(flowInstancePO);
        LOGGER.info("insertTest.result={}", result);
        Assert.assertEquals(1, result);
    }

    @Test
    public void selectByFlowInstanceId(){
        FlowInstancePO flowInstancePO = EntityBuilder.buildDynamicFlowInstancePO();
        processInstanceDAO.insert(flowInstancePO);
        String flowInstanceId = flowInstancePO.getFlowInstanceId();
        flowInstancePO = processInstanceDAO.selectByFlowInstanceId(flowInstancePO.getFlowInstanceId());
        Assert.assertTrue(StringUtils.equals(flowInstancePO.getFlowInstanceId(), flowInstanceId));
    }

    @Test
    public void updateStatusByFlowInstancePO() {
        FlowInstancePO flowInstancePO = EntityBuilder.buildDynamicFlowInstancePO();
        processInstanceDAO.insert(flowInstancePO);
        // change status
        processInstanceDAO.updateStatus(flowInstancePO, FlowInstanceStatus.COMPLETED);
        FlowInstancePO result = processInstanceDAO.selectByFlowInstanceId(flowInstancePO.getFlowInstanceId());
        Assert.assertEquals(FlowInstanceStatus.COMPLETED, (int) result.getStatus());
    }

    @Test
    public void updateStatusByFlowInstanceId() {
        FlowInstancePO flowInstancePO = EntityBuilder.buildDynamicFlowInstancePO();
        processInstanceDAO.insert(flowInstancePO);
        // change status
        processInstanceDAO.updateStatus(flowInstancePO.getFlowInstanceId(), FlowInstanceStatus.COMPLETED);
        FlowInstancePO result = processInstanceDAO.selectByFlowInstanceId(flowInstancePO.getFlowInstanceId());
        Assert.assertEquals(FlowInstanceStatus.COMPLETED, (int) result.getStatus());
    }
}
