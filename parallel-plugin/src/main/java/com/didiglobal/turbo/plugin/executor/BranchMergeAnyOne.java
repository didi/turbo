package com.didiglobal.turbo.plugin.executor;

import com.didiglobal.turbo.engine.bo.NodeInstanceBO;
import com.didiglobal.turbo.engine.common.NodeInstanceStatus;
import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.plugin.common.ParallelErrorEnum;
import com.didiglobal.turbo.plugin.service.ParallelNodeInstanceService;
import com.didiglobal.turbo.plugin.util.ExecutorUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.Set;

@Component
public class BranchMergeAnyOne extends BranchMergeStrategy {

    @Resource
    private ParallelNodeInstanceService parallelNodeInstanceService;

    /**
     * The first branch to arrive
     */
    @Override
    void joinFirst(RuntimeContext runtimeContext, NodeInstancePO forkNodeInstancePo, NodeInstanceBO currentNodeInstance, String parentExecuteId, String currentExecuteId, Set<String> executeIds, DataMergeStrategy dataMergeStrategy) {

        InstanceDataPO joinInstanceData = instanceDataDAO.select(runtimeContext.getFlowInstanceId(), runtimeContext.getInstanceDataId());
        InstanceDataPO forkInstanceData = instanceDataDAO.select(runtimeContext.getFlowInstanceId(), forkNodeInstancePo.getInstanceDataId());
        InstanceDataPO mergePo = dataMergeStrategy.merge(runtimeContext, forkInstanceData, joinInstanceData);
        instanceDataDAO.insertOrUpdate(mergePo);
        currentNodeInstance.setStatus(NodeInstanceStatus.COMPLETED);
        currentNodeInstance.put("executeId", ExecutorUtil.genExecuteIdWithParent(parentExecuteId, currentExecuteId));
        NodeInstancePO joinNodeInstancePo = buildNodeInstancePO(runtimeContext, currentNodeInstance);
        nodeInstanceDAO.insert(joinNodeInstancePo);
        nodeInstanceLogDAO.insert(buildNodeInstanceLogPO(joinNodeInstancePo));

        // Close other nodes with pending ACTIVE status
        parallelNodeInstanceService.closeParallelSuspendUserTask(runtimeContext, executeIds);
    }

    /**
     * The AnyOne policy only handles the first branch that arrives,
     * and all subsequent branches that arrive fail
     */
    @Override
    void joinMerge(RuntimeContext runtimeContext, NodeInstancePO joinNodeInstance, NodeInstanceBO currentNodeInstance, String parentExecuteId, String currentExecuteId, Set<String> allExecuteIdSet, DataMergeStrategy dataMergeStrategy) {
        throw new ProcessException(ParallelErrorEnum.BRANCH_MERGE_STRATEGY_ERROR.getErrNo(), ParallelErrorEnum.BRANCH_MERGE_STRATEGY_ERROR.getErrMsg());
    }

}