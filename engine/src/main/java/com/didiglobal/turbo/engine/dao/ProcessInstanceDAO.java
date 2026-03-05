package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.FlowInstancePO;

public interface ProcessInstanceDAO {

    /**
     * SelectByFlowInstanceId: query flowInstancePO by flowInstanceId.
     *
     * @param flowInstanceId
     * @return flowInstancePO
     */
    FlowInstancePO selectByFlowInstanceId(String flowInstanceId);

    /**
     * Insert: insert flowInstancePO, return -1 while insert failed.
     *
     * @param flowInstancePO
     * @return int
     */
    int insert(FlowInstancePO flowInstancePO);

    /**
     * UpdateStatus: update flowInstance status by flowInstanceId.
     *
     * @param flowInstanceId
     * @param status
     */
    void updateStatus(String flowInstanceId, int status);

    /**
     * UpdateStatus: update flowInstance status by flowInstancePO.
     *
     * @param flowInstancePO
     * @param status
     */
    void updateStatus(FlowInstancePO flowInstancePO, int status);
}
