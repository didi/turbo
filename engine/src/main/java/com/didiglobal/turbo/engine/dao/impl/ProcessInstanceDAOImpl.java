package com.didiglobal.turbo.engine.dao.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.didiglobal.turbo.engine.dao.BaseDAO;
import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.engine.dao.mapper.ProcessInstanceMapper;
import com.didiglobal.turbo.engine.entity.FlowInstancePO;

import java.util.Date;

@DS("engine")
public class ProcessInstanceDAOImpl extends BaseDAO<ProcessInstanceMapper, FlowInstancePO>
        implements ProcessInstanceDAO {

    @Override
    public FlowInstancePO selectByFlowInstanceId(String flowInstanceId) {
        return baseMapper.selectByFlowInstanceId(flowInstanceId);
    }

    @Override
    public int insert(FlowInstancePO flowInstancePO) {
        try {
            return baseMapper.insert(flowInstancePO);
        } catch (Exception e) {
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
        baseMapper.updateStatus(flowInstancePO);
    }
}
