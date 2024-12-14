package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class NodeInstanceLogDAOTest extends BaseTest {

    @Resource
    private NodeInstanceLogDAO nodeInstanceLogDAO;

    @Test
    public void batchInsert() {
        NodeInstanceLogPO nodeInstanceLogPO = EntityBuilder.buildNodeInstanceLogPO();
        List<NodeInstanceLogPO> nodeInstanceLogPOList = new ArrayList<>();
        nodeInstanceLogPOList.add(nodeInstanceLogPO);
        nodeInstanceLogPOList.add(EntityBuilder.buildNodeInstanceLogPO());
        nodeInstanceLogPOList.add(EntityBuilder.buildNodeInstanceLogPO());
        nodeInstanceLogDAO.insertList(nodeInstanceLogPOList);

        nodeInstanceLogPOList = nodeInstanceLogDAO.queryAllByFlowInstanceId(nodeInstanceLogPO.getFlowInstanceId());
        Assert.assertEquals(3, nodeInstanceLogPOList.size());
    }
}
