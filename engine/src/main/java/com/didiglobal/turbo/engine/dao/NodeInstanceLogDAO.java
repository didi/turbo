package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;

import java.util.List;

public interface NodeInstanceLogDAO extends BaseDAO{

    /**
     * insert nodeInstanceLogPO
     *
     * @param nodeInstanceLogPO
     * @return -1 while insert failed
     */
    int insert(NodeInstanceLogPO nodeInstanceLogPO);

    /**
     * nodeInstanceLogList batch insert
     *
     * @param nodeInstanceLogList
     * @return
     */
    boolean insertList(List<NodeInstanceLogPO> nodeInstanceLogList);

    /**
     * query all items under flow instance id
     */

    List<NodeInstanceLogPO> queryAllByFlowInstanceId(String flowInstanceId);
}
