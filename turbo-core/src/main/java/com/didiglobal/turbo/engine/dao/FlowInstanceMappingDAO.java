package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.FlowInstanceMappingPO;

import java.util.List;

/**
 * DAO interface for flow instance mapping persistence.
 */
public interface FlowInstanceMappingDAO {

    List<FlowInstanceMappingPO> selectFlowInstanceMappingPOList(String flowInstanceId, String nodeInstanceId);

    FlowInstanceMappingPO selectFlowInstanceMappingPO(String flowInstanceId, String nodeInstanceId);

    int insert(FlowInstanceMappingPO flowInstanceMappingPO);

    void updateType(String flowInstanceId, String nodeInstanceId, int type);
}
