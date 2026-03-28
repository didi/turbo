package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.FlowInstancePO;

/**
 * DAO interface for flow process instance persistence.
 */
public interface ProcessInstanceDAO {

    FlowInstancePO selectByFlowInstanceId(String flowInstanceId);

    int insert(FlowInstancePO flowInstancePO);

    void updateStatus(String flowInstanceId, int status);

    void updateStatus(FlowInstancePO flowInstancePO, int status);
}
