package com.didiglobal.turbo.plugin.service;

import com.didiglobal.turbo.engine.common.FlowElementType;
import com.didiglobal.turbo.engine.common.NodeInstanceStatus;
import com.didiglobal.turbo.engine.common.NodeInstanceType;
import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceLogDAO;
import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.plugin.common.ParallelNodeInstanceStatus;
import com.didiglobal.turbo.plugin.util.ExecutorUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Collection;
import java.util.List;

@Service
public class ParallelNodeInstanceService {

    @Resource
    private NodeInstanceDAO nodeInstanceDAO;
    @Resource
    private NodeInstanceLogDAO nodeInstanceLogDAO;

    public void closeParallelSuspendUserTask(RuntimeContext runtimeContext, Collection<String> executeIds) throws ProcessException {
        String flowInstanceId = runtimeContext.getFlowInstanceId();
        List<NodeInstancePO> poList = nodeInstanceDAO.selectByFlowInstanceId(flowInstanceId);
        poList.forEach(po -> {
            if(po.getNodeType() == FlowElementType.USER_TASK
                && ExecutorUtil.containsAny(executeIds, (String) po.get("executeId"))
                && po.getStatus() == NodeInstanceStatus.ACTIVE){
                nodeInstanceDAO.updateStatus(po, ParallelNodeInstanceStatus.CLOSED);
                nodeInstanceLogDAO.insert(buildNodeInstanceLogPO(po));
            }
        });
    }

    public void closeAllSuspendUserTask(String  flowInstanceId) throws ProcessException {
        List<NodeInstancePO> poList = nodeInstanceDAO.selectByFlowInstanceId(flowInstanceId);
        poList.forEach(po -> {
            if(po.getNodeType() == FlowElementType.USER_TASK
                && po.getStatus() == NodeInstanceStatus.ACTIVE){
                nodeInstanceDAO.updateStatus(po, ParallelNodeInstanceStatus.CLOSED);
                nodeInstanceLogDAO.insert(buildNodeInstanceLogPO(po));
            }
        });
    }

    protected NodeInstanceLogPO buildNodeInstanceLogPO(NodeInstancePO nodeInstancePO) {
        NodeInstanceLogPO nodeInstanceLogPO = new NodeInstanceLogPO();
        BeanUtils.copyProperties(nodeInstancePO, nodeInstanceLogPO);
        nodeInstanceLogPO.setId(null);
        nodeInstanceLogPO.setType(NodeInstanceType.EXECUTE);
        return nodeInstanceLogPO;
    }
}
