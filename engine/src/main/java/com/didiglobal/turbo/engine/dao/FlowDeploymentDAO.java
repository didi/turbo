package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;

public interface FlowDeploymentDAO {

    /**
     * Insert: insert flowDeploymentPO, return -1 while insert failed.
     *
     * @param flowDeploymentPO
     * @return int
     */
    int insert(FlowDeploymentPO flowDeploymentPO);

    /**
     * SelectByDeployId: query flowDeploymentPO by flowDeployId.
     *
     * @param flowDeployId
     * @return flowDeploymentPO
     */
    FlowDeploymentPO selectByDeployId(String flowDeployId);

    /**
     * SelectRecentByFlowModuleId: query recent flowDeploymentPO by flowModuleId.
     *
     * @param flowModuleId
     * @return flowDeploymentPO
     */
    FlowDeploymentPO selectRecentByFlowModuleId(String flowModuleId);
}
