package com.didiglobal.turbo.mybatis.dao.impl;


import com.didiglobal.turbo.engine.common.NodeInstanceStatus;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.util.TurboBeanUtils;
import com.didiglobal.turbo.mybatis.dao.mapper.NodeInstanceMapper;
import com.didiglobal.turbo.mybatis.entity.NodeInstanceEntity;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeInstanceDAOImpl implements NodeInstanceDAO {

    private final NodeInstanceMapper baseMapper;

    public NodeInstanceDAOImpl(NodeInstanceMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public int insert(NodeInstancePO nodeInstancePO) {
        try {
            NodeInstanceEntity nodeInstanceEntity = NodeInstanceEntity.of(nodeInstancePO);
            int insert = baseMapper.insert(nodeInstanceEntity);
            TurboBeanUtils.copyProperties(nodeInstanceEntity, nodeInstancePO);
            return insert;
        } catch (Exception e) {
            LOGGER.error("insert exception.||nodeInstancePO={}", nodeInstancePO, e);
        }
        return -1;
    }

    /**
     * when nodeInstancePO's id is null, batch insert.
     * when nodeInstancePO's id is not null, update it status.
     *
     * @param nodeInstanceList
     * @return
     */
    // TODO: 2020/1/14 post handle while failed: retry 5 times
    @Override
    public boolean insertOrUpdateList(List<NodeInstancePO> nodeInstanceList) {
        if (CollectionUtils.isEmpty(nodeInstanceList)) {
            LOGGER.warn("insertOrUpdateList: nodeInstanceList is empty.");
            return true;
        }

        Map<String, NodeInstanceEntity> nodeInstanceEntityMap = new HashMap<>();
        String flowInstanceId = null;
        for (int i = 0; i < nodeInstanceList.size(); i++) {
            NodeInstancePO nodeInstance = nodeInstanceList.get(i);
            if (nodeInstance.getId() != null) {
                baseMapper.updateStatus(NodeInstanceEntity.of(nodeInstance));
                continue;

            }

            flowInstanceId = nodeInstance.getFlowInstanceId();
            nodeInstanceEntityMap.put(String.valueOf(i), NodeInstanceEntity.of(nodeInstance));
        }
        if (nodeInstanceEntityMap.isEmpty()) {
            return true;
        }

        boolean batched = baseMapper.batchInsert(flowInstanceId, new ArrayList<>(nodeInstanceEntityMap.values()));
        nodeInstanceEntityMap.forEach((k, v) -> {
            TurboBeanUtils.copyProperties(v, nodeInstanceList.get(Integer.parseInt(k)));
        });
        return batched;
    }

    @Override
    public NodeInstancePO selectByNodeInstanceId(String flowInstanceId, String nodeInstanceId) {
        return baseMapper.selectByNodeInstanceId(flowInstanceId, nodeInstanceId);
    }

    @Override
    public NodeInstancePO selectBySourceInstanceId(String flowInstanceId, String sourceNodeInstanceId, String nodeKey) {
        return baseMapper.selectBySourceInstanceId(flowInstanceId, sourceNodeInstanceId, nodeKey);
    }

    /**
     * select recent nodeInstancePO order by id desc
     *
     * @param flowInstanceId
     * @return
     */
    @Override
    public NodeInstancePO selectRecentOne(String flowInstanceId) {
        return baseMapper.selectRecentOne(flowInstanceId);
    }

    /**
     * select recent active nodeInstancePO order by id desc
     *
     * @param flowInstanceId
     * @return
     */
    @Override
    public NodeInstancePO selectRecentActiveOne(String flowInstanceId) {
        return baseMapper.selectRecentOneByStatus(flowInstanceId, NodeInstanceStatus.ACTIVE);
    }

    /**
     * select recent completed nodeInstancePO order by id desc
     *
     * @param flowInstanceId
     * @return
     */
    @Override
    public NodeInstancePO selectRecentCompletedOne(String flowInstanceId) {
        return baseMapper.selectRecentOneByStatus(flowInstanceId, NodeInstanceStatus.COMPLETED);
    }

    /**
     * select recent active nodeInstancePO order by id desc
     * If it doesn't exist, select recent completed nodeInstancePO order by id desc
     *
     * @param flowInstanceId
     * @return
     */
    @Override
    public NodeInstancePO selectEnabledOne(String flowInstanceId) {
        NodeInstancePO nodeInstancePO = baseMapper.selectRecentOneByStatus(flowInstanceId, NodeInstanceStatus.ACTIVE);
        if (nodeInstancePO == null) {
            LOGGER.info("selectEnabledOne: there's no active node of the flowInstance.||flowInstanceId={}", flowInstanceId);
            nodeInstancePO = baseMapper.selectRecentOneByStatus(flowInstanceId, NodeInstanceStatus.COMPLETED);
        }
        return nodeInstancePO;
    }

    @Override
    public List selectByFlowInstanceId(String flowInstanceId) {
        return baseMapper.selectByFlowInstanceId(flowInstanceId);
    }

    /**
     * select nodeInstancePOList order by id desc
     *
     * @param flowInstanceId
     * @return
     */
    @Override
    public List selectDescByFlowInstanceId(String flowInstanceId) {
        return baseMapper.selectDescByFlowInstanceId(flowInstanceId);
    }

    /**
     * update nodeInstancePO status by nodeInstanceId
     *
     * @param nodeInstancePO
     * @param status
     */
    @Override
    public void updateStatus(NodeInstancePO nodeInstancePO, int status) {
        nodeInstancePO.setStatus(status);
        nodeInstancePO.setModifyTime(new Date());
        baseMapper.updateStatus(NodeInstanceEntity.of(nodeInstancePO));
    }

    /**
     * select nodeInstancePOList by flowInstanceId and nodeKey
     *
     * @param flowInstanceId
     * @param nodeKey
     * @return
     */
    @Override
    public List selectByFlowInstanceIdAndNodeKey(String flowInstanceId, String nodeKey) {
        return baseMapper.selectByFlowInstanceIdAndNodeKey(flowInstanceId, nodeKey);
    }

    @Override
    public void updateById(NodeInstancePO joinNodeInstancePo) {
        baseMapper.updateById(NodeInstanceEntity.of(joinNodeInstancePo));
    }
}
