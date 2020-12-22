package com.xiaoju.uemc.turbo.engine.dao.mapper;

import com.xiaoju.uemc.turbo.engine.common.NodeInstanceStatus;
import com.xiaoju.uemc.turbo.engine.entity.NodeInstancePO;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Stefanie on 2019/12/1.
 */
public class NodeInstanceMapperTest extends BaseTest {

    @Resource
    private NodeInstanceMapper nodeInstanceMapper;

    @Test
    public void insert() {
        NodeInstancePO nodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        int result = nodeInstanceMapper.insert(nodeInstancePO);
        Assert.assertTrue(result == 1);
    }

    @Test
    public void selectByNodeInstanceId() {
        NodeInstancePO nodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        nodeInstanceMapper.insert(nodeInstancePO);
        NodeInstancePO result = nodeInstanceMapper.selectByNodeInstanceId(nodeInstancePO.getFlowInstanceId(), nodeInstancePO.getNodeInstanceId());
        Assert.assertTrue(result.getNodeInstanceId().equals(nodeInstancePO.getNodeInstanceId()));
    }

    @Test
    public void selectRecentOne() {
        NodeInstancePO oldNodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        nodeInstanceMapper.insert(oldNodeInstancePO);
        NodeInstancePO newNodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        nodeInstanceMapper.insert(newNodeInstancePO);
        NodeInstancePO result = nodeInstanceMapper.selectRecentOne(oldNodeInstancePO.getFlowInstanceId());
        Assert.assertTrue(result.getNodeInstanceId().equals(newNodeInstancePO.getNodeInstanceId()));
    }

    @Test
    public void selectRecentOneByStatus() {
        NodeInstancePO oldNodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        nodeInstanceMapper.insert(oldNodeInstancePO);
        NodeInstancePO newNodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        nodeInstanceMapper.insert(newNodeInstancePO);
        NodeInstancePO result = nodeInstanceMapper.selectRecentOneByStatus(oldNodeInstancePO.getFlowInstanceId(), NodeInstanceStatus.ACTIVE);
        Assert.assertTrue(result.getNodeInstanceId().equals(newNodeInstancePO.getNodeInstanceId()));
    }

    @Test
    public void selectBySourceInstanceId() {
        NodeInstancePO nodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        nodeInstanceMapper.insert(nodeInstancePO);
        NodeInstancePO result = nodeInstanceMapper.selectBySourceInstanceId(nodeInstancePO.getFlowInstanceId(),
                nodeInstancePO.getSourceNodeInstanceId(), nodeInstancePO.getNodeKey());
        Assert.assertTrue(result.getNodeInstanceId().equals(nodeInstancePO.getNodeInstanceId()));
    }

    @Test
    public void updateStatus() {
        NodeInstancePO nodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        nodeInstanceMapper.insert(nodeInstancePO);
        nodeInstancePO.setStatus(NodeInstanceStatus.COMPLETED);
        nodeInstancePO.setModifyTime(new Date());
        nodeInstanceMapper.updateStatus(nodeInstancePO);
        NodeInstancePO result = nodeInstanceMapper.selectByNodeInstanceId(nodeInstancePO.getFlowInstanceId(), nodeInstancePO.getNodeInstanceId());
        Assert.assertTrue(result.getStatus() == NodeInstanceStatus.COMPLETED);
    }

    @Test
    public void batchInsert() {
        NodeInstancePO firstNodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        firstNodeInstancePO.setFlowInstanceId("flowInstanceId" + UUID.randomUUID().toString());
        List<NodeInstancePO> nodeInstancePOList = new ArrayList<>();
        nodeInstancePOList.add(firstNodeInstancePO);
        NodeInstancePO nodeInstancePO1 = EntityBuilder.buildDynamicNodeInstancePO();
        nodeInstancePO1.setFlowInstanceId(firstNodeInstancePO.getFlowInstanceId());
        nodeInstancePOList.add(nodeInstancePO1);
        NodeInstancePO nodeInstancePO2 = EntityBuilder.buildDynamicNodeInstancePO();
        nodeInstancePO2.setFlowInstanceId(firstNodeInstancePO.getFlowInstanceId());
        nodeInstancePOList.add(nodeInstancePO2);
        nodeInstanceMapper.batchInsert(firstNodeInstancePO.getFlowInstanceId(), nodeInstancePOList);
        List<NodeInstancePO> result = nodeInstanceMapper.selectByFlowInstanceId(firstNodeInstancePO.getFlowInstanceId());
        Assert.assertTrue(result.size() == 3);
    }
}
