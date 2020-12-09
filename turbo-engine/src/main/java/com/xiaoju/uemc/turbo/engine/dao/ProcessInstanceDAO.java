package com.xiaoju.uemc.turbo.engine.dao;

import com.xiaoju.uemc.turbo.engine.dao.mapper.ProcessInstanceMapper;
import com.xiaoju.uemc.turbo.engine.entity.FlowInstancePO;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Stefanie on 2019/12/5.
 */
@Service
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
        FlowInstancePO flowInstancePO = baseMapper.selectByFlowInstanceId(flowInstanceId);
        updateStatus(flowInstancePO, status);
    }

    public void updateStatus(FlowInstancePO flowInstancePO, int status) {
        flowInstancePO.setStatus(status);
        flowInstancePO.setModifyTime(new Date());
        baseMapper.updateStatus(flowInstancePO);
    }
}
