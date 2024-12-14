package com.didiglobal.turbo.plugin.dao;

import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import org.junit.Assert;
import org.junit.Test;
import com.didiglobal.turbo.plugin.runner.BaseTest;
import com.didiglobal.turbo.plugin.util.EntityBuilder;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ParallelNodeInstanceHandlerTest extends BaseTest {

    @Resource
    NodeInstanceDAO nodeInstanceDAO;

    @Test
    public void insert(){
        NodeInstancePO nodeInstancePO = EntityBuilder.buildDynamicParallelNodeInstancePO();
        int result = nodeInstanceDAO.insert(nodeInstancePO);
        Assert.assertEquals(1, result);
    }

    @Test
    public void insertOrUpdateList() {
        NodeInstancePO nodeInstancePO = EntityBuilder.buildDynamicParallelNodeInstancePO();
        nodeInstanceDAO.insert(nodeInstancePO);
        nodeInstancePO.setInstanceDataId(nodeInstancePO.getInstanceDataId() + "_updated");
        List<NodeInstancePO> list = new ArrayList<>();
        list.add(nodeInstancePO);
        list.add(EntityBuilder.buildDynamicParallelNodeInstancePO());
        list.add(EntityBuilder.buildDynamicParallelNodeInstancePO());
        boolean result = nodeInstanceDAO.insertOrUpdateList(list);
        Assert.assertTrue(result);
    }

    @Test
    public void selectByFlowInstanceIdAndNodeKey(){
        NodeInstancePO nodeInstancePO = EntityBuilder.buildDynamicParallelNodeInstancePO();
        nodeInstancePO.setFlowInstanceId(nodeInstancePO.getFlowInstanceId() + UUID.randomUUID());
        nodeInstanceDAO.insert(nodeInstancePO);
        List<NodeInstancePO> result = nodeInstanceDAO.selectByFlowInstanceIdAndNodeKey(nodeInstancePO.getFlowInstanceId(), nodeInstancePO.getNodeKey());
        Assert.assertTrue(result.size() == 1);
    }

    @Test
    public void selectByFlowInstanceId(){
        NodeInstancePO nodeInstancePO = EntityBuilder.buildDynamicParallelNodeInstancePO();
        nodeInstancePO.setFlowInstanceId(nodeInstancePO.getFlowInstanceId() + UUID.randomUUID());
        nodeInstanceDAO.insert(nodeInstancePO);
        NodeInstancePO nodeInstancePO1 = EntityBuilder.buildDynamicParallelNodeInstancePO();
        nodeInstancePO1.setFlowInstanceId(nodeInstancePO.getFlowInstanceId());
        nodeInstanceDAO.insert(nodeInstancePO1);
        NodeInstancePO nodeInstancePO2 = EntityBuilder.buildDynamicParallelNodeInstancePO();
        nodeInstancePO2.setFlowInstanceId(nodeInstancePO.getFlowInstanceId());
        nodeInstanceDAO.insert(nodeInstancePO2);
        List<NodeInstancePO> result = nodeInstanceDAO.selectByFlowInstanceId(nodeInstancePO.getFlowInstanceId());
        Assert.assertTrue(result.size() == 3);
    }

    @Test
    public void updateParallelNodeInstancePo(){
        NodeInstancePO parallelNodeInstancePO = EntityBuilder.buildParallelNodeInstancePO();
        nodeInstanceDAO.insert(parallelNodeInstancePO);
        parallelNodeInstancePO.put("executeId", "12345678|1234567");
        parallelNodeInstancePO.setStatus(1);
        nodeInstanceDAO.updateStatus(parallelNodeInstancePO, 1);
    }

    @Test
    public void selectOne() {
        NodeInstancePO nodeInstancePO = EntityBuilder.buildParallelNodeInstancePO();
        nodeInstanceDAO.insert(nodeInstancePO);
        NodeInstancePO nodeInstancePO1 = nodeInstanceDAO.selectByNodeInstanceId(null, nodeInstancePO.getNodeInstanceId());
        Assert.assertTrue(nodeInstancePO1.getId() != null);
    }
}
