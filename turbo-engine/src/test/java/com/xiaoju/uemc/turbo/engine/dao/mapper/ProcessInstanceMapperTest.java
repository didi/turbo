package com.xiaoju.uemc.turbo.engine.dao.mapper;

import com.xiaoju.uemc.turbo.engine.common.FlowInstanceStatus;
import com.xiaoju.uemc.turbo.engine.entity.FlowInstancePO;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by Stefanie on 2019/12/1.
 */
public class ProcessInstanceMapperTest extends BaseTest {

    @Autowired
    private ProcessInstanceMapper processInstanceMapper;

    @Test
    public void insert() {
        FlowInstancePO flowInstancePO = EntityBuilder.buildFlowInstancePO();
        int result = processInstanceMapper.insert(flowInstancePO);
        Assert.assertTrue(result == 1);
    }

    @Test
    public void update() {
        FlowInstancePO flowInstancePO = EntityBuilder.buildFlowInstancePO();
        processInstanceMapper.insert(flowInstancePO);
        String flowInstanceId = flowInstancePO.getFlowInstanceId();
        flowInstancePO = processInstanceMapper.selectByFlowInstanceId(flowInstancePO.getFlowInstanceId());
        Assert.assertTrue(StringUtils.equals(flowInstancePO.getFlowInstanceId(),flowInstanceId));
        flowInstancePO.setStatus(FlowInstanceStatus.TERMINATED);
        flowInstancePO.setModifyTime(new Date());
        processInstanceMapper.updateStatus(flowInstancePO);
    }

}
