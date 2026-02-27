package com.didiglobal.turbo.plugin.dao;

import com.didiglobal.turbo.plugin.dao.mapper.NodeInstanceSourceMapper;
import com.didiglobal.turbo.plugin.entity.NodeInstanceSourcePO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

import java.util.List;

/**
 * 节点实例来源关联 DAO
 */
@Repository
public class NodeInstanceSourceDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeInstanceSourceDAO.class);

    @Resource
    private NodeInstanceSourceMapper nodeInstanceSourceMapper;

    /**
     * 插入一条来源记录
     */
    public int insert(NodeInstanceSourcePO sourcePO) {
        try {
            return nodeInstanceSourceMapper.insert(sourcePO);
        } catch (Exception e) {
            LOGGER.error("insert NodeInstanceSourcePO failed.||sourcePO={}", sourcePO, e);
        }
        return -1;
    }

    /**
     * 根据流程实例ID和节点实例ID查询所有来源记录
     */
    public List<NodeInstanceSourcePO> selectByNodeInstanceId(String flowInstanceId, String nodeInstanceId) {
        return nodeInstanceSourceMapper.selectByNodeInstanceId(flowInstanceId, nodeInstanceId);
    }

    /**
     * 根据流程实例ID和节点key查询所有来源记录
     */
    public List<NodeInstanceSourcePO> selectByNodeKey(String flowInstanceId, String nodeKey) {
        return nodeInstanceSourceMapper.selectByNodeKey(flowInstanceId, nodeKey);
    }
}
