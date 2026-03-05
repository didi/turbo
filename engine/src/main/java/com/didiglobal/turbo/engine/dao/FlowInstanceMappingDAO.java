package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.FlowInstanceMappingPO;

import java.util.List;

public interface FlowInstanceMappingDAO {

    /**
     * SelectFlowInstanceMappingPOList: used for multiple instances scene.
     *
     * @param flowInstanceId
     * @param nodeInstanceId
     * @return flowInstanceMappingPOList
     */
    List<FlowInstanceMappingPO> selectFlowInstanceMappingPOList(String flowInstanceId, String nodeInstanceId);

    /**
     * SelectFlowInstanceMappingPO: used for single instances scene.
     *
     * @param flowInstanceId
     * @param nodeInstanceId
     * @return flowInstanceMappingPO
     */
    FlowInstanceMappingPO selectFlowInstanceMappingPO(String flowInstanceId, String nodeInstanceId);

    /**
     * Insert: insert flowInstanceMappingPO, return -1 while insert failed.
     *
     * @param flowInstanceMappingPO
     * @return int
     */
    int insert(FlowInstanceMappingPO flowInstanceMappingPO);

    /**
     * UpdateType: update flowInstanceMapping type by flowInstanceId and nodeInstanceId.
     *
     * @param flowInstanceId
     * @param nodeInstanceId
     * @param type
     */
    void updateType(String flowInstanceId, String nodeInstanceId, int type);
}
