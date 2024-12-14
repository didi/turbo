package com.didiglobal.turbo.mybatis.dao.impl;

import com.didiglobal.turbo.engine.dao.FlowInstanceMappingDAO;
import com.didiglobal.turbo.engine.entity.FlowInstanceMappingPO;
import com.didiglobal.turbo.engine.util.TurboBeanUtils;
import com.didiglobal.turbo.mybatis.dao.mapper.FlowInstanceMappingMapper;
import com.didiglobal.turbo.mybatis.entity.FlowInstanceMappingEntity;

import java.util.Date;
import java.util.List;

public class FlowInstanceMappingDAOImpl implements FlowInstanceMappingDAO {

    private final FlowInstanceMappingMapper baseMapper;

    public FlowInstanceMappingDAOImpl(FlowInstanceMappingMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    /**
     * Used for multiple instances scene
     *
     * @param flowInstanceId
     * @param nodeInstanceId
     * @return
     */
    @Override
    public List selectFlowInstanceMappingPOList(String flowInstanceId, String nodeInstanceId) {
        return baseMapper.selectFlowInstanceMappingPOList(flowInstanceId, nodeInstanceId);
    }

    /**
     * Used for single instances scene
     *
     * @param flowInstanceId
     * @param nodeInstanceId
     * @return
     */
    @Override
    public FlowInstanceMappingEntity selectFlowInstanceMappingPO(String flowInstanceId, String nodeInstanceId) {
        return baseMapper.selectFlowInstanceMappingPO(flowInstanceId, nodeInstanceId);
    }

    /**
     * Insert: insert flowInstanceMappingPO, return -1 while insert failed.
     *
     * @param flowInstanceMappingPO
     * @return
     */
    @Override
    public int insert(FlowInstanceMappingPO flowInstanceMappingPO) {
        try {
            FlowInstanceMappingEntity flowInstanceMappingEntity = FlowInstanceMappingEntity.of(flowInstanceMappingPO);
            int insert = baseMapper.insert(flowInstanceMappingEntity);
            TurboBeanUtils.copyProperties(flowInstanceMappingEntity, flowInstanceMappingPO);
            return insert;
        } catch (Exception e) {
            LOGGER.error("insert exception.||flowInstanceMappingPO={}", flowInstanceMappingPO, e);
        }
        return -1;
    }

    @Override
    public void updateType(String flowInstanceId, String nodeInstanceId, int type) {
        FlowInstanceMappingPO flowInstanceMappingPO = new FlowInstanceMappingPO();
        flowInstanceMappingPO.setFlowInstanceId(flowInstanceId);
        flowInstanceMappingPO.setNodeInstanceId(nodeInstanceId);
        flowInstanceMappingPO.setType(type);
        flowInstanceMappingPO.setModifyTime(new Date());
        baseMapper.updateType(FlowInstanceMappingEntity.of(flowInstanceMappingPO));
    }

}
