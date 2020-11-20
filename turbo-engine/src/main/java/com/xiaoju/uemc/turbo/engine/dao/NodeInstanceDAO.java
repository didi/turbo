package com.xiaoju.uemc.turbo.engine.dao;

import com.google.common.collect.Lists;
import com.xiaoju.uemc.turbo.engine.common.NodeInstanceStatus;
import com.xiaoju.uemc.turbo.engine.dao.mapper.NodeInstanceMapper;
import com.xiaoju.uemc.turbo.engine.entity.NodeInstancePO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Stefanie on 2019/12/5.
 */
@Service
public class NodeInstanceDAO extends BaseDAO<NodeInstanceMapper, NodeInstancePO> {

    /**
     * insert nodeInstancePO
     * if error, this will not throw exption but return -1
     *
     * @param nodeInstancePO
     * @return
     */
    public int insert(NodeInstancePO nodeInstancePO) {
        try {
            return baseMapper.insert(nodeInstancePO);
        } catch (Exception e) {
            LOGGER.error("insert exception.||nodeInstancePO={}", nodeInstancePO, e);
        }
        return -1;
    }

    /**
     * when nodeInstancePO id is null, batch insert them.
     * when nodeInstancePO id is not null, update it status.
     *
     * @param nodeInstanceList
     * @return
     */
    // TODO: 2020/1/14 post handle while failed: retry 5 times
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
                // because this node instance is exist
                baseMapper.updateStatus(nodeInstancePO);
            }
        });

        if (CollectionUtils.isEmpty(insertNodeInstanceList)) {
            return true;
        }

        return baseMapper.batchInsert(insertNodeInstanceList.get(0).getFlowInstanceId(), insertNodeInstanceList);
    }

    /**
     * query NodeInstancePO by flowInstanceId and nodeInstanceId
     * @param flowInstanceId
     * @param nodeInstanceId
     * @return
     */
    public NodeInstancePO selectByNodeInstanceId(String flowInstanceId, String nodeInstanceId) {
        return baseMapper.selectByNodeInstanceId(flowInstanceId, nodeInstanceId);
    }

    /**
     * query NodeInstancePO by flowInstanceId, sourceNodeInstanceId and nodeKey
     * @param flowInstanceId
     * @param sourceNodeInstanceId
     * @param nodeKey
     * @return
     */
    public NodeInstancePO selectBySourceInstanceId(String flowInstanceId, String sourceNodeInstanceId, String nodeKey) {
        return baseMapper.selectBySourceInstanceId(flowInstanceId, sourceNodeInstanceId, nodeKey);
    }

    /**
     * select recent nodeInstancePO
     * @param flowInstanceId
     * @return
     */
    public NodeInstancePO selectRecentOne(String flowInstanceId) {
        return baseMapper.selectRecentOne(flowInstanceId);
    }

    /**
     * select recent active nodeInstancePO
     * @param flowInstanceId
     * @return
     */
    public NodeInstancePO selectRecentActiveOne(String flowInstanceId) {
        return baseMapper.selectRecentOneByStatus(flowInstanceId, NodeInstanceStatus.ACTIVE);
    }

    /**
     * select recent completed nodeInstancePO
     * @param flowInstanceId
     * @return
     */
    public NodeInstancePO selectRecentCompletedOne(String flowInstanceId) {
        return baseMapper.selectRecentOneByStatus(flowInstanceId, NodeInstanceStatus.COMPLETED);
    }

    /**
     * query recent active nodeInstancePO and check exist
     *
     * @param flowInstanceId
     * @return
     */
    public NodeInstancePO selectEnabledOne(String flowInstanceId) {
        NodeInstancePO nodeInstancePO = baseMapper.selectRecentOneByStatus(flowInstanceId, NodeInstanceStatus.ACTIVE);
        if (nodeInstancePO == null) {
            LOGGER.info("selectEnabledOne: there's no active node of the flowInstance.||flowInstanceId={}", flowInstanceId);
            nodeInstancePO = baseMapper.selectRecentOneByStatus(flowInstanceId, NodeInstanceStatus.COMPLETED);
        }
        return nodeInstancePO;
    }

    /**
     * query NodeInstancePOList by flowInstanceId
     *
     * @param flowInstanceId
     * @return
     */
    public List<NodeInstancePO> selectByFlowInstanceId(String flowInstanceId) {
        return baseMapper.selectByFlowInstanceId(flowInstanceId);
    }

    /**
     * query desc NodeInstancePOList
     *
     * @param flowInstanceId
     * @return
     */
    public List<NodeInstancePO> selectDescByFlowInstanceId(String flowInstanceId) {
        return baseMapper.selectDescByFlowInstanceId(flowInstanceId);
    }

    /**
     * update nodeInstancePO status
     * @param nodeInstancePO
     * @param status
     */
    public void updateStatus(NodeInstancePO nodeInstancePO, int status) {
        nodeInstancePO.setStatus(status);
        nodeInstancePO.setModifyTime(new Date());
        baseMapper.updateStatus(nodeInstancePO);
    }

    /**
     * ？？？
     *
     * @param nodeInstancePO
     * @return
     */
    // TODO: 2019/12/15
    public int insertOrUpdateStatus(NodeInstancePO nodeInstancePO) {
        try {
            return baseMapper.insert(nodeInstancePO);
        } catch (Exception e1) {
            LOGGER.error("insertOrUpdateStatus exception, insert failed.||nodeInstancePO={}", nodeInstancePO, e1);
        }

        try {
            updateStatus(nodeInstancePO, nodeInstancePO.getStatus());
        } catch (Exception e2) {
            LOGGER.error("insertOrUpdateStatus exception, updateStatus failed.||nodeInstancePO={}", nodeInstancePO, e2);
        }
        return -1;
    }
}
