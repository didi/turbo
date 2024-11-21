package com.didiglobal.turbo.plugin.executor;

import com.didiglobal.turbo.engine.bo.NodeInstanceBO;
import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.plugin.common.ParallelErrorEnum;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class BranchMergeCustom extends BranchMergeStrategy{

    @Override
    void joinFirst(RuntimeContext runtimeContext, NodeInstancePO forkNodeInstancePo, NodeInstanceBO currentNodeInstance, String parentExecuteId, String currentExecuteId, Set<String> executeIds, DataMergeStrategy dataMergeStrategy) {
        throw new ProcessException(ParallelErrorEnum.UNSUPPORTED_BRANCH_MERGE_STRATEGY.getErrNo(), ParallelErrorEnum.UNSUPPORTED_BRANCH_MERGE_STRATEGY.getErrMsg());
    }

    @Override
    void joinMerge(RuntimeContext runtimeContext, NodeInstancePO joinNodeInstance, NodeInstanceBO currentNodeInstance, String parentExecuteId, String currentExecuteId, Set<String> allExecuteIdSet, DataMergeStrategy dataMergeStrategy) {
        throw new ProcessException(ParallelErrorEnum.UNSUPPORTED_BRANCH_MERGE_STRATEGY.getErrNo(), ParallelErrorEnum.UNSUPPORTED_BRANCH_MERGE_STRATEGY.getErrMsg());
    }

}