package com.didiglobal.turbo.mybatis.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.turbo.engine.dao.NodeInstanceLogDAO;
import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;
import com.didiglobal.turbo.engine.util.TurboBeanUtils;
import com.didiglobal.turbo.mybatis.dao.mapper.NodeInstanceLogMapper;
import com.didiglobal.turbo.mybatis.entity.NodeInstanceLogEntity;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class NodeInstanceLogDAOImpl implements NodeInstanceLogDAO {

    private final NodeInstanceLogMapper baseMapper;

    public NodeInstanceLogDAOImpl(NodeInstanceLogMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public int insert(NodeInstanceLogPO nodeInstanceLogPO) {
        try {
            NodeInstanceLogEntity nodeInstanceLogEntity = NodeInstanceLogEntity.of(nodeInstanceLogPO);
            int insert = baseMapper.insert(nodeInstanceLogEntity);
            TurboBeanUtils.copyProperties(nodeInstanceLogEntity, nodeInstanceLogPO);
            return insert;
        } catch (Exception e) {
            LOGGER.error("insert exception.||nodeInstanceLogPO={}", nodeInstanceLogPO, e);
        }
        return -1;
    }

    @Override
    public boolean insertList(List<NodeInstanceLogPO> nodeInstanceLogList) {
        if (CollectionUtils.isEmpty(nodeInstanceLogList)) {
            return false;
        }

        List<NodeInstanceLogEntity> collect = nodeInstanceLogList.stream().map(NodeInstanceLogEntity::of).collect(Collectors.toList());
        boolean batched = baseMapper.batchInsert(nodeInstanceLogList.get(0).getFlowInstanceId(), collect);
        if (!batched) {
            return false;
        }

        for (int i = 0; i < nodeInstanceLogList.size(); i++) {
            TurboBeanUtils.copyProperties(collect.get(i), nodeInstanceLogList.get(i));
        }
        return true;
    }

    @Override
    public List queryAllByFlowInstanceId(String flowInstanceId) {
        QueryWrapper<NodeInstanceLogEntity> entityWrapper = new QueryWrapper<>();
        entityWrapper.in("flow_instance_id", flowInstanceId);
        return baseMapper.selectList(entityWrapper);
    }
}
