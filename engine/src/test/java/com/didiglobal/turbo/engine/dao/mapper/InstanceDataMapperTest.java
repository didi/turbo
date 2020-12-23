package com.didiglobal.turbo.engine.dao.mapper;

import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InstanceDataMapperTest extends BaseTest {

    @Autowired
    private InstanceDataMapper instanceDataMapper;

    @Test
    public void insert() {
        InstanceDataPO instanceDataPO = EntityBuilder.buildDynamicInstanceDataPO();
        int result = instanceDataMapper.insert(instanceDataPO);
        LOGGER.info("insert.result={}", result);
        Assert.assertTrue(result == 1);
    }

    @Test
    public void select() {
        InstanceDataPO instanceDataPO = EntityBuilder.buildDynamicInstanceDataPO();
        instanceDataMapper.insert(instanceDataPO);
        InstanceDataPO result = instanceDataMapper.select(instanceDataPO.getFlowInstanceId(), instanceDataPO.getInstanceDataId());
        Assert.assertTrue(result.getInstanceDataId().equals(instanceDataPO.getInstanceDataId()));
    }

    @Test
    public void selectRecentOne() {
        InstanceDataPO oldInstanceDataPO = EntityBuilder.buildDynamicInstanceDataPO();
        instanceDataMapper.insert(oldInstanceDataPO);
        InstanceDataPO newInstanceDataPO = EntityBuilder.buildDynamicInstanceDataPO();
        instanceDataMapper.insert(newInstanceDataPO);
        InstanceDataPO result = instanceDataMapper.selectRecentOne(oldInstanceDataPO.getFlowInstanceId());
        Assert.assertTrue(result.getInstanceDataId().equals(newInstanceDataPO.getInstanceDataId()));
    }
}
