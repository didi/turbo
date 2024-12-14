package com.didiglobal.turbo.engine.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import com.didiglobal.turbo.mybatis.dao.mapper.NodeInstanceLogMapper;
import com.didiglobal.turbo.mybatis.entity.NodeInstanceLogEntity;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NodeInstanceLogMapperTest extends BaseTest {

    @Resource
    private NodeInstanceLogMapper nodeInstanceLogMapper;

    @Test
    public void batchInsert() {
        String flowInstanceId = "flowInstanceId_" + UUID.randomUUID().toString();
        NodeInstanceLogPO nodeInstanceLogPO = EntityBuilder.buildNodeInstanceLogPO(flowInstanceId);
        List<NodeInstanceLogPO> nodeInstanceLogPOList = new ArrayList<>();
        nodeInstanceLogPOList.add(nodeInstanceLogPO);
        nodeInstanceLogPOList.add(EntityBuilder.buildNodeInstanceLogPO(flowInstanceId));
        nodeInstanceLogPOList.add(EntityBuilder.buildNodeInstanceLogPO(flowInstanceId));
        List<NodeInstanceLogEntity> collect = nodeInstanceLogPOList.stream().map(NodeInstanceLogEntity::of).collect(Collectors.toList());
        nodeInstanceLogMapper.batchInsert(nodeInstanceLogPO.getFlowInstanceId(), collect);

        QueryWrapper<NodeInstanceLogEntity> entityWrapper = new QueryWrapper<>();
        entityWrapper.in("flow_instance_id", nodeInstanceLogPO.getFlowInstanceId());
        List<NodeInstanceLogEntity> nodeInstanceLogList = nodeInstanceLogMapper.selectList(entityWrapper);
        Assert.assertEquals(3, nodeInstanceLogList.size());
    }
}
