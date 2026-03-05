package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.dao.mapper.NodeInstanceLogMapper;
import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("nodeInstanceLogDAO")
public class NodeInstanceLogDAOImpl extends BaseDAO<NodeInstanceLogMapper, NodeInstanceLogPO> implements NodeInstanceLogDAO {

    /**
     * insert nodeInstanceLogPO
     *
     * @param nodeInstanceLogPO
     * @return -1 while insert failed
     */
    @Override
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
    @Override
    public boolean insertList(List<NodeInstanceLogPO> nodeInstanceLogList) {
        return baseMapper.batchInsert(nodeInstanceLogList.get(0).getFlowInstanceId(), nodeInstanceLogList);
    }
}
