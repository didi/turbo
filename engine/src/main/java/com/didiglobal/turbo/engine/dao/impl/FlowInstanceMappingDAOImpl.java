package com.didiglobal.turbo.engine.dao.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.didiglobal.turbo.engine.dao.BaseDAO;
import com.didiglobal.turbo.engine.dao.FlowInstanceMappingDAO;
import com.didiglobal.turbo.engine.dao.mapper.FlowInstanceMappingMapper;
import com.didiglobal.turbo.engine.entity.FlowInstanceMappingPO;

import java.util.Date;
import java.util.List;

@DS("engine")
public class FlowInstanceMappingDAOImpl extends BaseDAO<FlowInstanceMappingMapper, FlowInstanceMappingPO>
        implements FlowInstanceMappingDAO {

    @Override
    public List<FlowInstanceMappingPO> selectFlowInstanceMappingPOList(String flowInstanceId, String nodeInstanceId) {
        return baseMapper.selectFlowInstanceMappingPOList(flowInstanceId, nodeInstanceId);
    }

    @Override
    public FlowInstanceMappingPO selectFlowInstanceMappingPO(String flowInstanceId, String nodeInstanceId) {
        return baseMapper.selectFlowInstanceMappingPO(flowInstanceId, nodeInstanceId);
    }

    @Override
    public int insert(FlowInstanceMappingPO flowInstanceMappingPO) {
        try {
            return baseMapper.insert(flowInstanceMappingPO);
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
        baseMapper.updateType(flowInstanceMappingPO);
    }
}
