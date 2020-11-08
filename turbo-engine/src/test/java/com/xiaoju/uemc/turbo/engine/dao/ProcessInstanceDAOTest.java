package com.xiaoju.uemc.turbo.engine.dao;

import com.xiaoju.uemc.turbo.engine.entity.FlowInstancePO;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
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
    public void insertTest() {
        FlowInstancePO flowInstancePO = EntityBuilder.buildFlowInstancePO();
        int result = processInstanceDAO.insert(flowInstancePO);
        LOGGER.info("insertTest.result={}", result);
        Assert.assertTrue(result == 1);
    }
}
