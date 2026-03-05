package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.common.NodeInstanceStatus;
import com.didiglobal.turbo.engine.dao.mapper.NodeInstanceMapper;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("nodeInstanceDAO")
public class NodeInstanceDAOImpl extends BaseDAO<NodeInstanceMapper, NodeInstancePO> implements NodeInstanceDAO {

    /**
     * insert nodeInstancePO
     *
     * @param nodeInstancePO
     * @return -1 while insert failed
     */
    @Override
    public int insert(NodeInstancePO nodeInstancePO) {
        try {
            return baseMapper.insert(nodeInstancePO);
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
    public List<NodeInstancePO> selectByFlowInstanceId(String flowInstanceId) {
        return baseMapper.selectByFlowInstanceId(flowInstanceId);
    }

    /**
     * select nodeInstancePOList order by id desc
     *
     * @param flowInstanceId
     * @return
     */
    @Override
    public List<NodeInstancePO> selectDescByFlowInstanceId(String flowInstanceId) {
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
        baseMapper.updateStatus(nodeInstancePO);
    }

    /**
     * select nodeInstancePOList by flowInstanceId and nodeKey
     *
     * @param flowInstanceId
     * @param nodeKey
     * @return
     */
    @Override
    public List<NodeInstancePO> selectByFlowInstanceIdAndNodeKey(String flowInstanceId, String nodeKey) {
        return baseMapper.selectByFlowInstanceIdAndNodeKey(flowInstanceId, nodeKey);
    }

    /**
     * update nodeInstancePO by primary key id
     *
     * @param nodeInstancePO
     * @return
     */
    @Override
    public boolean updateById(NodeInstancePO nodeInstancePO) {
        return super.updateById(nodeInstancePO);
    }
}
