package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.dao.mapper.ProcessInstanceMapper;
import com.didiglobal.turbo.engine.entity.FlowInstancePO;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class ProcessInstanceDAO extends BaseDAO<ProcessInstanceMapper, FlowInstancePO> {

    public FlowInstancePO selectByFlowInstanceId(String flowInstanceId) {
        return baseMapper.selectByFlowInstanceId(flowInstanceId);
    }

    /**
     * insert flowInstancePO
     *
     * @param flowInstancePO
     * @return -1 while insert failed
     */
    public int insert(FlowInstancePO flowInstancePO) {
        try {
            return baseMapper.insert(flowInstancePO);
        } catch (Exception e) {
            // TODO: 2020/2/1 clear reentrant exception log
            LOGGER.error("insert exception.||flowInstancePO={}", flowInstancePO, e);
        }
        return -1;
    }

    public void updateStatus(String flowInstanceId, int status) {
        FlowInstancePO flowInstancePO = selectByFlowInstanceId(flowInstanceId);
        updateStatus(flowInstancePO, status);
    }

    public void updateStatus(FlowInstancePO flowInstancePO, int status) {
        flowInstancePO.setStatus(status);
        flowInstancePO.setModifyTime(new Date());
        baseMapper.updateStatus(flowInstancePO);
    }
}
