package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.NodeInstancePO;

import java.util.List;

/**
 * DAO interface for node instance persistence.
 */
public interface NodeInstanceDAO {

    int insert(NodeInstancePO nodeInstancePO);

    boolean insertOrUpdateList(List<NodeInstancePO> nodeInstanceList);

    NodeInstancePO selectByNodeInstanceId(String flowInstanceId, String nodeInstanceId);

    NodeInstancePO selectBySourceInstanceId(String flowInstanceId, String sourceNodeInstanceId, String nodeKey);

    NodeInstancePO selectRecentOne(String flowInstanceId);

    NodeInstancePO selectRecentActiveOne(String flowInstanceId);

    NodeInstancePO selectRecentCompletedOne(String flowInstanceId);

    NodeInstancePO selectEnabledOne(String flowInstanceId);

    List<NodeInstancePO> selectByFlowInstanceId(String flowInstanceId);

    List<NodeInstancePO> selectDescByFlowInstanceId(String flowInstanceId);

    void updateStatus(NodeInstancePO nodeInstancePO, int status);

    List<NodeInstancePO> selectByFlowInstanceIdAndNodeKey(String flowInstanceId, String nodeKey);
}
