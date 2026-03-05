package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.NodeInstancePO;

import java.util.List;

public interface NodeInstanceDAO {

    /**
     * Insert: insert nodeInstancePO, return -1 while insert failed.
     *
     * @param nodeInstancePO
     * @return int
     */
    int insert(NodeInstancePO nodeInstancePO);

    /**
     * InsertOrUpdateList: when nodeInstancePO's id is null, batch insert.
     * When nodeInstancePO's id is not null, update it status.
     *
     * @param nodeInstanceList
     * @return boolean
     */
    boolean insertOrUpdateList(List<NodeInstancePO> nodeInstanceList);

    /**
     * SelectByNodeInstanceId: query nodeInstancePO by flowInstanceId and nodeInstanceId.
     *
     * @param flowInstanceId
     * @param nodeInstanceId
     * @return nodeInstancePO
     */
    NodeInstancePO selectByNodeInstanceId(String flowInstanceId, String nodeInstanceId);

    /**
     * SelectBySourceInstanceId: query nodeInstancePO by flowInstanceId, sourceNodeInstanceId and nodeKey.
     *
     * @param flowInstanceId
     * @param sourceNodeInstanceId
     * @param nodeKey
     * @return nodeInstancePO
     */
    NodeInstancePO selectBySourceInstanceId(String flowInstanceId, String sourceNodeInstanceId, String nodeKey);

    /**
     * SelectRecentOne: select recent nodeInstancePO order by id desc.
     *
     * @param flowInstanceId
     * @return nodeInstancePO
     */
    NodeInstancePO selectRecentOne(String flowInstanceId);

    /**
     * SelectRecentActiveOne: select recent active nodeInstancePO order by id desc.
     *
     * @param flowInstanceId
     * @return nodeInstancePO
     */
    NodeInstancePO selectRecentActiveOne(String flowInstanceId);

    /**
     * SelectRecentCompletedOne: select recent completed nodeInstancePO order by id desc.
     *
     * @param flowInstanceId
     * @return nodeInstancePO
     */
    NodeInstancePO selectRecentCompletedOne(String flowInstanceId);

    /**
     * SelectEnabledOne: select recent active nodeInstancePO order by id desc.
     * If it doesn't exist, select recent completed nodeInstancePO order by id desc.
     *
     * @param flowInstanceId
     * @return nodeInstancePO
     */
    NodeInstancePO selectEnabledOne(String flowInstanceId);

    /**
     * SelectByFlowInstanceId: query nodeInstancePOList by flowInstanceId.
     *
     * @param flowInstanceId
     * @return nodeInstancePOList
     */
    List<NodeInstancePO> selectByFlowInstanceId(String flowInstanceId);

    /**
     * SelectDescByFlowInstanceId: query nodeInstancePOList by flowInstanceId order by id desc.
     *
     * @param flowInstanceId
     * @return nodeInstancePOList
     */
    List<NodeInstancePO> selectDescByFlowInstanceId(String flowInstanceId);

    /**
     * UpdateStatus: update nodeInstancePO status by nodeInstanceId.
     *
     * @param nodeInstancePO
     * @param status
     */
    void updateStatus(NodeInstancePO nodeInstancePO, int status);

    /**
     * SelectByFlowInstanceIdAndNodeKey: query nodeInstancePOList by flowInstanceId and nodeKey.
     *
     * @param flowInstanceId
     * @param nodeKey
     * @return nodeInstancePOList
     */
    List<NodeInstancePO> selectByFlowInstanceIdAndNodeKey(String flowInstanceId, String nodeKey);

    /**
     * UpdateById: update nodeInstancePO by primary key id.
     *
     * @param nodeInstancePO
     * @return boolean
     */
    boolean updateById(NodeInstancePO nodeInstancePO);
}
