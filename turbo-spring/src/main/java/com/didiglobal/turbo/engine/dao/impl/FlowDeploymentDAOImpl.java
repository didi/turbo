package com.didiglobal.turbo.engine.dao.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.didiglobal.turbo.engine.dao.BaseDAO;
import com.didiglobal.turbo.engine.dao.FlowDeploymentDAO;
import com.didiglobal.turbo.engine.dao.mapper.FlowDeploymentMapper;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;

@DS("engine")
public class FlowDeploymentDAOImpl extends BaseDAO<FlowDeploymentMapper, FlowDeploymentPO>
        implements FlowDeploymentDAO {

    @Override
    public int insert(FlowDeploymentPO flowDeploymentPO) {
        try {
            return baseMapper.insert(flowDeploymentPO);
        } catch (Exception e) {
            LOGGER.error("insert exception.||flowDeploymentPO={}", flowDeploymentPO, e);
        }
        return -1;
    }

    @Override
    public FlowDeploymentPO selectByDeployId(String flowDeployId) {
        return baseMapper.selectByDeployId(flowDeployId);
    }

    @Override
    public FlowDeploymentPO selectRecentByFlowModuleId(String flowModuleId) {
        return baseMapper.selectByModuleId(flowModuleId);
    }
}
