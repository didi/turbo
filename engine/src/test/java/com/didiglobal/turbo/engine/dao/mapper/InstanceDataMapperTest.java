package com.didiglobal.turbo.engine.dao.mapper;

import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

public class InstanceDataMapperTest extends BaseTest {

    @Resource
    private InstanceDataMapper instanceDataMapper;

    @Test
    public void insert() {
        InstanceDataPO instanceDataPO = EntityBuilder.buildDynamicInstanceDataPO();
        int result = instanceDataMapper.insert(instanceDataPO);
        LOGGER.info("insert.result={}", result);
        Assert.assertEquals(1, result);
    }

    @Test
    public void select() {
        InstanceDataPO instanceDataPO = EntityBuilder.buildDynamicInstanceDataPO();
        instanceDataMapper.insert(instanceDataPO);
        InstanceDataPO result = instanceDataMapper.select(instanceDataPO.getFlowInstanceId(), instanceDataPO.getInstanceDataId());
        Assert.assertEquals(result.getInstanceDataId(), instanceDataPO.getInstanceDataId());
    }

    @Test
    public void selectRecentOne() {
        InstanceDataPO oldInstanceDataPO = EntityBuilder.buildDynamicInstanceDataPO();
        instanceDataMapper.insert(oldInstanceDataPO);
        InstanceDataPO newInstanceDataPO = EntityBuilder.buildDynamicInstanceDataPO();
        instanceDataMapper.insert(newInstanceDataPO);
        InstanceDataPO result = instanceDataMapper.selectRecentOne(oldInstanceDataPO.getFlowInstanceId());
        Assert.assertEquals(result.getInstanceDataId(), newInstanceDataPO.getInstanceDataId());
    }
}
