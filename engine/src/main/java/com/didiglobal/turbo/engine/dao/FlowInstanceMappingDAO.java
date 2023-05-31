package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.dao.mapper.FlowInstanceMappingMapper;
import com.didiglobal.turbo.engine.entity.FlowInstanceMappingPO;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FlowInstanceMappingDAO extends BaseDAO<FlowInstanceMappingMapper, FlowInstanceMappingPO> {

    /**
     * Used for multiple instances scene
     *
     * @param flowInstanceId
     * @param nodeInstanceId
     * @return
     */
    public List<FlowInstanceMappingPO> selectFlowInstanceMappingPOList(String flowInstanceId, String nodeInstanceId) {
        return baseMapper.selectFlowInstanceMappingPOList(flowInstanceId, nodeInstanceId);
    }

    /**
     * Used for single instances scene
     *
     * @param flowInstanceId
     * @param nodeInstanceId
     * @return
     */
    public FlowInstanceMappingPO selectFlowInstanceMappingPO(String flowInstanceId, String nodeInstanceId) {
        return baseMapper.selectFlowInstanceMappingPO(flowInstanceId, nodeInstanceId);
    }

    /**
     * Insert: insert flowInstanceMappingPO, return -1 while insert failed.
     *
     * @param  flowInstanceMappingPO
     * @return
     */
    public int insert(FlowInstanceMappingPO flowInstanceMappingPO) {
        try {
            return baseMapper.insert(flowInstanceMappingPO);
        } catch (Exception e) {
            LOGGER.error("insert exception.||flowInstanceMappingPO={}", flowInstanceMappingPO, e);
        }
        return -1;
    }

    public void updateStatus(String flowInstanceId, String nodeInstanceId, int status) {
        FlowInstanceMappingPO flowInstanceMappingPO = new FlowInstanceMappingPO();
        flowInstanceMappingPO.setFlowInstanceId(flowInstanceId);
        flowInstanceMappingPO.setNodeInstanceId(nodeInstanceId);
        flowInstanceMappingPO.setType(status);
        flowInstanceMappingPO.setModifyTime(new Date());
        baseMapper.updateStatus(flowInstanceMappingPO);
    }
}
