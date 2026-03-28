package com.didiglobal.turbo.engine.dao.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.didiglobal.turbo.engine.dao.BaseDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceLogDAO;
import com.didiglobal.turbo.engine.dao.mapper.NodeInstanceLogMapper;
import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;

import java.util.List;

@DS("engine")
public class NodeInstanceLogDAOImpl extends BaseDAO<NodeInstanceLogMapper, NodeInstanceLogPO>
        implements NodeInstanceLogDAO {

    @Override
    public int insert(NodeInstanceLogPO nodeInstanceLogPO) {
        try {
            return baseMapper.insert(nodeInstanceLogPO);
        } catch (Exception e) {
            LOGGER.error("insert exception.||nodeInstanceLogPO={}", nodeInstanceLogPO, e);
        }
        return -1;
    }

    @Override
    public boolean insertList(List<NodeInstanceLogPO> nodeInstanceLogList) {
        return baseMapper.batchInsert(nodeInstanceLogList.get(0).getFlowInstanceId(), nodeInstanceLogList);
    }
}
