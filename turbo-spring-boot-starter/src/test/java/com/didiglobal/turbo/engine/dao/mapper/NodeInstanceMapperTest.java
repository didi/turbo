package com.didiglobal.turbo.engine.dao.mapper;

import com.didiglobal.turbo.engine.common.NodeInstanceStatus;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import com.didiglobal.turbo.mybatis.dao.mapper.NodeInstanceMapper;
import com.didiglobal.turbo.mybatis.entity.NodeInstanceEntity;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NodeInstanceMapperTest extends BaseTest {

    @Resource
    private NodeInstanceMapper nodeInstanceMapper;

    @Test
    public void insert() {
        NodeInstancePO nodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        int result = nodeInstanceMapper.insert(NodeInstanceEntity.of(nodeInstancePO));
        Assert.assertEquals(1, result);
    }

    @Test
    public void selectByNodeInstanceId() {
        NodeInstancePO nodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        nodeInstanceMapper.insert(NodeInstanceEntity.of(nodeInstancePO));

        NodeInstancePO result = nodeInstanceMapper.selectByNodeInstanceId(nodeInstancePO.getFlowInstanceId(), nodeInstancePO.getNodeInstanceId());
        Assert.assertEquals(result.getNodeInstanceId(), nodeInstancePO.getNodeInstanceId());
    }

    @Test
    public void selectRecentOne() {
        NodeInstancePO oldNodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        nodeInstanceMapper.insert(NodeInstanceEntity.of(oldNodeInstancePO));

        NodeInstancePO newNodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        nodeInstanceMapper.insert(NodeInstanceEntity.of(newNodeInstancePO));

        NodeInstancePO result = nodeInstanceMapper.selectRecentOne(oldNodeInstancePO.getFlowInstanceId());
        Assert.assertEquals(result.getNodeInstanceId(), newNodeInstancePO.getNodeInstanceId());
    }

    @Test
    public void selectRecentOneByStatus() {
        NodeInstancePO oldNodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        nodeInstanceMapper.insert(NodeInstanceEntity.of(oldNodeInstancePO));

        NodeInstancePO newNodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        nodeInstanceMapper.insert(NodeInstanceEntity.of(newNodeInstancePO));

        NodeInstancePO result = nodeInstanceMapper.selectRecentOneByStatus(oldNodeInstancePO.getFlowInstanceId(), NodeInstanceStatus.ACTIVE);
        Assert.assertEquals(result.getNodeInstanceId(), newNodeInstancePO.getNodeInstanceId());
    }

    @Test
    public void selectBySourceInstanceId() {
        NodeInstancePO nodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        nodeInstanceMapper.insert(NodeInstanceEntity.of(nodeInstancePO));

        NodeInstancePO result = nodeInstanceMapper.selectBySourceInstanceId(nodeInstancePO.getFlowInstanceId(),
                nodeInstancePO.getSourceNodeInstanceId(), nodeInstancePO.getNodeKey());
        Assert.assertEquals(result.getNodeInstanceId(), nodeInstancePO.getNodeInstanceId());
    }

    @Test
    public void updateStatus() {
        NodeInstancePO nodeInstancePO = EntityBuilder.buildDynamicNodeInstancePO();
        nodeInstanceMapper.insert(NodeInstanceEntity.of(nodeInstancePO));

        nodeInstancePO.setStatus(NodeInstanceStatus.COMPLETED);
        nodeInstancePO.setModifyTime(new Date());
        nodeInstanceMapper.updateStatus(NodeInstanceEntity.of(nodeInstancePO));

        NodeInstancePO result = nodeInstanceMapper.selectByNodeInstanceId(nodeInstancePO.getFlowInstanceId(), nodeInstancePO.getNodeInstanceId());
        Assert.assertEquals(NodeInstanceStatus.COMPLETED, (int) result.getStatus());
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
        List<NodeInstanceEntity> collect = nodeInstancePOList.stream().map(NodeInstanceEntity::of).collect(Collectors.toList());
        nodeInstanceMapper.batchInsert(firstNodeInstancePO.getFlowInstanceId(), collect);

        List<NodeInstanceEntity> result = nodeInstanceMapper.selectByFlowInstanceId(firstNodeInstancePO.getFlowInstanceId());
        Assert.assertEquals(3, result.size());
    }
}
