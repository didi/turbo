package com.xiaoju.uemc.turbo.core.dao;

import com.xiaoju.uemc.turbo.core.dao.mapper.NodeInstanceLogMapper;
import com.xiaoju.uemc.turbo.core.entity.NodeInstanceLogPO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Stefanie on 2019/12/5.
 */
@Service
public class NodeInstanceLogDAO extends BaseDAO<NodeInstanceLogMapper, NodeInstanceLogPO> {

    public int insert(NodeInstanceLogPO nodeInstanceLogPO) {
        try {
            return baseMapper.insert(nodeInstanceLogPO);
        } catch (Exception e) {
            LOGGER.error("insert exception.||nodeInstanceLogPO={}", nodeInstanceLogPO, e);
        }
        return -1;
    }

    public boolean insertList(List<NodeInstanceLogPO> nodeInstanceLogList) {
        return baseMapper.batchInsert(nodeInstanceLogList.get(0).getFlowInstanceId(), nodeInstanceLogList);
    }
}
