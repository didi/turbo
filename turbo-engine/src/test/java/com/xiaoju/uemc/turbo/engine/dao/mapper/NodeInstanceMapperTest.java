package com.xiaoju.uemc.turbo.engine.dao.mapper;

import com.xiaoju.uemc.turbo.engine.entity.NodeInstancePO;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Stefanie on 2019/12/1.
 */
public class NodeInstanceMapperTest extends BaseTest {

    @Autowired
    private NodeInstanceMapper nodeInstanceMapper;

    @Test
    public void insert() {
        NodeInstancePO nodeInstancePO = EntityBuilder.buildNodeInstancePO();
        int result = nodeInstanceMapper.insert(nodeInstancePO);
        Assert.assertTrue(result == 1);
    }
}
