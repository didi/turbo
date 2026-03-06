package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;

import java.util.List;

/**
 * DAO interface for node instance log persistence.
 */
public interface NodeInstanceLogDAO {

    int insert(NodeInstanceLogPO nodeInstanceLogPO);

    boolean insertList(List<NodeInstanceLogPO> nodeInstanceLogList);
}
