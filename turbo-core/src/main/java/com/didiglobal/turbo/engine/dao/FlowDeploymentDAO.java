package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;

/**
 * DAO interface for flow deployment persistence.
 */
public interface FlowDeploymentDAO {

    int insert(FlowDeploymentPO flowDeploymentPO);

    FlowDeploymentPO selectByDeployId(String flowDeployId);

    FlowDeploymentPO selectRecentByFlowModuleId(String flowModuleId);
}
