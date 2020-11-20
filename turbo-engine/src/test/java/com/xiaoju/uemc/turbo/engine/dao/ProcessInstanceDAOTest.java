package com.xiaoju.uemc.turbo.engine.dao;

import com.xiaoju.uemc.turbo.engine.common.FlowInstanceStatus;
import com.xiaoju.uemc.turbo.engine.entity.FlowInstancePO;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by Stefanie on 2019/12/5.
 */
public class ProcessInstanceDAOTest extends BaseTest {

    @Resource
    private ProcessInstanceDAO processInstanceDAO;

    @Test
    public void insert() {
        FlowInstancePO flowInstancePO = EntityBuilder.buildFlowInstancePO();
        int result = processInstanceDAO.insert(flowInstancePO);
        LOGGER.info("insertTest.result={}", result);
        Assert.assertTrue(result == 1);
    }

    @Test
    public void selectByFlowInstanceId(){
        FlowInstancePO flowInstancePO = EntityBuilder.buildFlowInstancePO();
        processInstanceDAO.insert(flowInstancePO);
        String flowInstanceId = flowInstancePO.getFlowInstanceId();
        flowInstancePO = processInstanceDAO.selectByFlowInstanceId(flowInstancePO.getFlowInstanceId());
        Assert.assertTrue(StringUtils.equals(flowInstancePO.getFlowInstanceId(), flowInstanceId));
    }

    @Test
    public void updateStatus_1() {
        FlowInstancePO flowInstancePO = EntityBuilder.buildFlowInstancePO();
        processInstanceDAO.insert(flowInstancePO);
        // change status
        processInstanceDAO.updateStatus(flowInstancePO, FlowInstanceStatus.COMPLETED);
        FlowInstancePO result = processInstanceDAO.selectByFlowInstanceId(flowInstancePO.getFlowInstanceId());
        Assert.assertTrue(result.getStatus() == FlowInstanceStatus.COMPLETED);
    }

    @Test
    public void updateStatus_2() {
        FlowInstancePO flowInstancePO = EntityBuilder.buildFlowInstancePO();
        processInstanceDAO.insert(flowInstancePO);
        // change status
        processInstanceDAO.updateStatus(flowInstancePO.getFlowInstanceId(), FlowInstanceStatus.COMPLETED);
        FlowInstancePO result = processInstanceDAO.selectByFlowInstanceId(flowInstancePO.getFlowInstanceId());
        Assert.assertTrue(result.getStatus() == FlowInstanceStatus.COMPLETED);
    }
}
