package com.xiaoju.uemc.turbo.engine.dao.mapper;

import com.xiaoju.uemc.turbo.engine.entity.InstanceDataPO;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Stefanie on 2019/12/1.
 */
public class InstanceDataMapperTest extends BaseTest {

    @Autowired
    private InstanceDataMapper instanceDataMapper;

    @Test
    public void insert() {
        InstanceDataPO instanceDataPO = EntityBuilder.buildInstanceDataPO();
        int result = instanceDataMapper.insert(instanceDataPO);
        LOGGER.info("insert.result={}", result);
        Assert.assertTrue(result == 1);
    }
}
