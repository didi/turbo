package com.didiglobal.turbo.engine.dao.mapper;

import com.didiglobal.turbo.engine.common.FlowInstanceStatus;
import com.didiglobal.turbo.engine.entity.FlowInstancePO;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import com.didiglobal.turbo.engine.common.FlowInstanceStatus;
import com.didiglobal.turbo.engine.entity.FlowInstancePO;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

public class ProcessInstanceMapperTest extends BaseTest {

    @Resource
    private ProcessInstanceMapper processInstanceMapper;

    @Test
    public void insert() {
        FlowInstancePO flowInstancePO = EntityBuilder.buildDynamicFlowInstancePO();
        int result = processInstanceMapper.insert(flowInstancePO);
        Assert.assertTrue(result == 1);
    }

    @Test
    public void selectByFlowInstanceId() {
        FlowInstancePO flowInstancePO = EntityBuilder.buildDynamicFlowInstancePO();
        processInstanceMapper.insert(flowInstancePO);
        String flowInstanceId = flowInstancePO.getFlowInstanceId();
        flowInstancePO = processInstanceMapper.selectByFlowInstanceId(flowInstancePO.getFlowInstanceId());
        Assert.assertTrue(StringUtils.equals(flowInstancePO.getFlowInstanceId(), flowInstanceId));
    }

    @Test
    public void updateStatus() {
        FlowInstancePO flowInstancePO = EntityBuilder.buildDynamicFlowInstancePO();
        processInstanceMapper.insert(flowInstancePO);
        // change status
        flowInstancePO.setStatus(FlowInstanceStatus.COMPLETED);
        flowInstancePO.setModifyTime(new Date());
        processInstanceMapper.updateStatus(flowInstancePO);
    }
}
