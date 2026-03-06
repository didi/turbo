package com.didiglobal.turbo.engine.dao.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.didiglobal.turbo.engine.common.NodeInstanceStatus;
import com.didiglobal.turbo.engine.dao.BaseDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.mapper.NodeInstanceMapper;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Date;
import java.util.List;

@DS("engine")
public class NodeInstanceDAOImpl extends BaseDAO<NodeInstanceMapper, NodeInstancePO>
        implements NodeInstanceDAO {

    @Override
    public int insert(NodeInstancePO nodeInstancePO) {
        try {
            return baseMapper.insert(nodeInstancePO);
        } catch (Exception e) {
            LOGGER.error("insert exception.||nodeInstancePO={}", nodeInstancePO, e);
        }
        return -1;
    }

    @Override
    public boolean insertOrUpdateList(List<NodeInstancePO> nodeInstanceList) {
        if (CollectionUtils.isEmpty(nodeInstanceList)) {
            LOGGER.warn("insertOrUpdateList: nodeInstanceList is empty.");
            return true;
        }

        List<NodeInstancePO> insertNodeInstanceList = Lists.newArrayList();
        nodeInstanceList.forEach(nodeInstancePO -> {
            if (nodeInstancePO.getId() == null) {
                insertNodeInstanceList.add(nodeInstancePO);
            } else {
                baseMapper.updateStatus(nodeInstancePO);
            }
        });

        if (CollectionUtils.isEmpty(insertNodeInstanceList)) {
            return true;
        }

        return baseMapper.batchInsert(insertNodeInstanceList.get(0).getFlowInstanceId(), insertNodeInstanceList);
    }

    @Override
    public NodeInstancePO selectByNodeInstanceId(String flowInstanceId, String nodeInstanceId) {
        return baseMapper.selectByNodeInstanceId(flowInstanceId, nodeInstanceId);
    }

    @Override
    public NodeInstancePO selectBySourceInstanceId(String flowInstanceId, String sourceNodeInstanceId, String nodeKey) {
        return baseMapper.selectBySourceInstanceId(flowInstanceId, sourceNodeInstanceId, nodeKey);
    }

    @Override
    public NodeInstancePO selectRecentOne(String flowInstanceId) {
        return baseMapper.selectRecentOne(flowInstanceId);
    }

    @Override
    public NodeInstancePO selectRecentActiveOne(String flowInstanceId) {
        return baseMapper.selectRecentOneByStatus(flowInstanceId, NodeInstanceStatus.ACTIVE);
    }

    @Override
    public NodeInstancePO selectRecentCompletedOne(String flowInstanceId) {
        return baseMapper.selectRecentOneByStatus(flowInstanceId, NodeInstanceStatus.COMPLETED);
    }

    @Override
    public NodeInstancePO selectEnabledOne(String flowInstanceId) {
        NodeInstancePO nodeInstancePO = baseMapper.selectRecentOneByStatus(flowInstanceId, NodeInstanceStatus.ACTIVE);
        if (nodeInstancePO == null) {
            LOGGER.info("selectEnabledOne: there's no active node.||flowInstanceId={}", flowInstanceId);
            nodeInstancePO = baseMapper.selectRecentOneByStatus(flowInstanceId, NodeInstanceStatus.COMPLETED);
        }
        return nodeInstancePO;
    }

    @Override
    public List<NodeInstancePO> selectByFlowInstanceId(String flowInstanceId) {
        return baseMapper.selectByFlowInstanceId(flowInstanceId);
    }

    @Override
    public List<NodeInstancePO> selectDescByFlowInstanceId(String flowInstanceId) {
        return baseMapper.selectDescByFlowInstanceId(flowInstanceId);
    }

    @Override
    public void updateStatus(NodeInstancePO nodeInstancePO, int status) {
        nodeInstancePO.setStatus(status);
        nodeInstancePO.setModifyTime(new Date());
        baseMapper.updateStatus(nodeInstancePO);
    }

    @Override
    public List<NodeInstancePO> selectByFlowInstanceIdAndNodeKey(String flowInstanceId, String nodeKey) {
        return baseMapper.selectByFlowInstanceIdAndNodeKey(flowInstanceId, nodeKey);
    }
}
