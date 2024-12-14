package com.didiglobal.turbo.mybatis.dao.impl;

import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.engine.entity.FlowInstancePO;
import com.didiglobal.turbo.engine.util.TurboBeanUtils;
import com.didiglobal.turbo.mybatis.dao.mapper.ProcessInstanceMapper;
import com.didiglobal.turbo.mybatis.entity.FlowInstanceEntity;

import java.util.Date;

public class ProcessInstanceDAOImpl implements ProcessInstanceDAO {

    private final ProcessInstanceMapper baseMapper;

    public ProcessInstanceDAOImpl(ProcessInstanceMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public FlowInstancePO selectByFlowInstanceId(String flowInstanceId) {
        return baseMapper.selectByFlowInstanceId(flowInstanceId);
    }

    @Override
    public int insert(FlowInstancePO flowInstancePO) {
        try {
            FlowInstanceEntity flowInstanceEntity = FlowInstanceEntity.of(flowInstancePO);
            int insert = baseMapper.insert(flowInstanceEntity);
            TurboBeanUtils.copyProperties(flowInstanceEntity, flowInstancePO);
            return insert;
        } catch (Exception e) {
            // TODO: 2020/2/1 clear reentrant exception log
            LOGGER.error("insert exception.||flowInstancePO={}", flowInstancePO, e);
        }
        return -1;
    }

    @Override
    public void updateStatus(String flowInstanceId, int status) {
        FlowInstancePO flowInstancePO = selectByFlowInstanceId(flowInstanceId);
        updateStatus(flowInstancePO, status);
    }

    @Override
    public void updateStatus(FlowInstancePO flowInstancePO, int status) {
        flowInstancePO.setStatus(status);
        flowInstancePO.setModifyTime(new Date());
        baseMapper.updateStatus(FlowInstanceEntity.of(flowInstancePO));
    }
}
