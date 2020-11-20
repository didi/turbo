package com.xiaoju.uemc.turbo.engine.dao;

import com.xiaoju.uemc.turbo.engine.entity.InstanceDataPO;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by Stefanie on 2020/11/20.
 */
public class InstanceDataDAOTest extends BaseTest {

    @Resource
    private InstanceDataDAO instanceDataDAO;

    @Test
    public void insert_1() {
        InstanceDataPO instanceDataPO = EntityBuilder.buildInstanceDataPO();
        int result = instanceDataDAO.insert(instanceDataPO);
        LOGGER.info("insertTest.result={}", result);
        Assert.assertTrue(result == 1);
    }

    @Test
    public void insert_2() {
        InstanceDataPO instanceDataPO = EntityBuilder.buildInstanceDataPO();
        instanceDataDAO.insert(instanceDataPO);
        // test DuplicateKeyException
        int result = instanceDataDAO.insert(instanceDataPO);
        LOGGER.info("insertTest.result={}", result);
        Assert.assertTrue(result == -1);
    }

    @Test
    public void select() {
        InstanceDataPO instanceDataPO = EntityBuilder.buildInstanceDataPO();
        instanceDataDAO.insert(instanceDataPO);
        InstanceDataPO result = instanceDataDAO.select(instanceDataPO.getFlowInstanceId(), instanceDataPO.getInstanceDataId());
        Assert.assertTrue(result.getInstanceDataId().equals(instanceDataPO.getInstanceDataId()));
    }

    @Test
    public void selectRecentOne() {
        InstanceDataPO oldInstanceDataPO = EntityBuilder.buildDynamicInstanceDataPO();
        instanceDataDAO.insert(oldInstanceDataPO);
        InstanceDataPO newInstanceDataPO = EntityBuilder.buildDynamicInstanceDataPO();
        instanceDataDAO.insert(newInstanceDataPO);
        InstanceDataPO result = instanceDataDAO.selectRecentOne(oldInstanceDataPO.getFlowInstanceId());
        Assert.assertTrue(result.getInstanceDataId().equals(newInstanceDataPO.getInstanceDataId()));
    }
}
