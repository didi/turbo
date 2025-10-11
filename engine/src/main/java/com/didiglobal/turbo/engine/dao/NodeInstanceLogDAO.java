package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.dao.mapper.NodeInstanceLogMapper;
import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NodeInstanceLogDAO extends BaseDAO<NodeInstanceLogMapper, NodeInstanceLogPO> {

    /**
     * insert nodeInstanceLogPO
     *
     * @param nodeInstanceLogPO
     * @return -1 while insert failed
     */
    public int insert(NodeInstanceLogPO nodeInstanceLogPO) {
        try {
            return baseMapper.insert(nodeInstanceLogPO);
        } catch (Exception e) {
            LOGGER.error("insert exception.||nodeInstanceLogPO={}", nodeInstanceLogPO, e);
        }
        return -1;
    }

    /**
     * nodeInstanceLogList batch insert
     *
     * @param nodeInstanceLogList
     * @return
     */
    public boolean insertList(List<NodeInstanceLogPO> nodeInstanceLogList) {
        return baseMapper.batchInsert(nodeInstanceLogList.get(0).getFlowInstanceId(), nodeInstanceLogList);
    }
}
