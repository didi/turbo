package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.dao.mapper.FlowDeploymentMapper;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import org.springframework.stereotype.Repository;

@Repository
public class FlowDeploymentDAO extends BaseDAO<FlowDeploymentMapper, FlowDeploymentPO> {

    /**
     * Insert: insert flowDeploymentPO, return -1 while insert failed.
     *
     * @param  flowDeploymentPO
     * @return
     */
    public int insert(FlowDeploymentPO flowDeploymentPO) {
        try {
            return baseMapper.insert(flowDeploymentPO);
        } catch (Exception e) {
            LOGGER.error("insert exception.||flowDeploymentPO={}", flowDeploymentPO, e);
        }
        return -1;
    }

    public FlowDeploymentPO selectByDeployId(String flowDeployId) {
        return baseMapper.selectByDeployId(flowDeployId);
    }

    /**
     * SelectRecentByFlowModuleId : query recent flowDeploymentPO by flowModuleId.
     *
     * @param  flowModuleId
     * @return flowDeploymentPO
     */
    public FlowDeploymentPO selectRecentByFlowModuleId(String flowModuleId) {
        return baseMapper.selectByModuleId(flowModuleId);
    }
}
