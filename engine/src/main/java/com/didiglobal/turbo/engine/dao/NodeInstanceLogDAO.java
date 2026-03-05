package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;

import java.util.List;

public interface NodeInstanceLogDAO {

    /**
     * Insert: insert nodeInstanceLogPO, return -1 while insert failed.
     *
     * @param nodeInstanceLogPO
     * @return int
     */
    int insert(NodeInstanceLogPO nodeInstanceLogPO);

    /**
     * InsertList: nodeInstanceLogList batch insert.
     *
     * @param nodeInstanceLogList
     * @return boolean
     */
    boolean insertList(List<NodeInstanceLogPO> nodeInstanceLogList);
}
