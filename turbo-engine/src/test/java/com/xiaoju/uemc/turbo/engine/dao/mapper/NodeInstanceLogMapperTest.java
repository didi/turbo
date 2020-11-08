package com.xiaoju.uemc.turbo.engine.dao.mapper;

import com.xiaoju.uemc.turbo.engine.entity.NodeInstanceLogPO;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Stefanie on 2019/12/1.
 */
public class NodeInstanceLogMapperTest extends BaseTest {

    @Autowired
    private NodeInstanceLogMapper nodeInstanceLogMapper;

    @Test
    public void insert() {
        NodeInstanceLogPO nodeInstanceLogPO = EntityBuilder.buildNodeInstanceLogPO();
        int result = nodeInstanceLogMapper.insert(nodeInstanceLogPO);
        Assert.assertTrue(result == 1);
    }
}
