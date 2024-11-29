package com.didiglobal.turbo.plugin.dao;

import com.didiglobal.turbo.engine.dao.NodeInstanceLogDAO;
import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;
import com.didiglobal.turbo.plugin.runner.BaseTest;
import com.didiglobal.turbo.plugin.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

public class ParallelNodeInstanceLogHandlerTest extends BaseTest {

    @Resource
    private NodeInstanceLogDAO nodeInstanceLogDAO;
    @Test
    public void insert() {
        NodeInstanceLogPO nodeInstanceLogPO = EntityBuilder.buildParallelNodeInstanceLogPO();
        int insert = nodeInstanceLogDAO.insert(nodeInstanceLogPO);
        Assert.assertEquals(1, insert);
    }
}
