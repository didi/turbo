package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.FlowInstanceMappingPO;

import java.util.List;

public interface FlowInstanceMappingDAO extends BaseDAO{

    /**
     * Used for multiple instances scene
     *
     * @param flowInstanceId
     * @param nodeInstanceId
     * @return
     */
    List<FlowInstanceMappingPO> selectFlowInstanceMappingPOList(String flowInstanceId, String nodeInstanceId);

    /**
     * Used for single instances scene
     *
     * @param flowInstanceId
     * @param nodeInstanceId
     * @return
     */
    FlowInstanceMappingPO selectFlowInstanceMappingPO(String flowInstanceId, String nodeInstanceId);

    /**
     * Insert: insert flowInstanceMappingPO, return -1 while insert failed.
     *
     * @param flowInstanceMappingPO
     * @return
     */
    int insert(FlowInstanceMappingPO flowInstanceMappingPO);

    void updateType(String flowInstanceId, String nodeInstanceId, int type);
}
