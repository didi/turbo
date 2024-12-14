package com.didiglobal.turbo.mybatis.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.turbo.engine.dao.FlowDeploymentDAO;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import com.didiglobal.turbo.engine.util.TurboBeanUtils;
import com.didiglobal.turbo.mybatis.dao.mapper.FlowDeploymentMapper;
import com.didiglobal.turbo.mybatis.entity.FlowDeploymentEntity;

public class FlowDeploymentDAOImpl implements FlowDeploymentDAO {

    private final FlowDeploymentMapper baseMapper;

    public FlowDeploymentDAOImpl(FlowDeploymentMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public int insert(FlowDeploymentPO flowDeploymentPO) {
        try {
            FlowDeploymentEntity flowDeploymentEntity = FlowDeploymentEntity.of(flowDeploymentPO);
            int insert = baseMapper.insert(flowDeploymentEntity);
            TurboBeanUtils.copyProperties(flowDeploymentEntity, flowDeploymentPO);
            return insert;
        } catch (Exception e) {
            LOGGER.error("insert exception.||flowDeploymentPO={}", flowDeploymentPO, e);
        }
        return -1;
    }

    @Override
    public FlowDeploymentEntity selectByDeployId(String flowDeployId) {
        return baseMapper.selectByDeployId(flowDeployId);
    }

    @Override
    public FlowDeploymentEntity selectRecentByFlowModuleId(String flowModuleId) {
        return baseMapper.selectByModuleId(flowModuleId);
    }

    public int count(QueryWrapper<FlowDeploymentEntity> flowDeployQuery) {
        return baseMapper.selectCount(flowDeployQuery);
    }
}
