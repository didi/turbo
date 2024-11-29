package com.didiglobal.turbo.plugin.executor;

import com.didiglobal.turbo.plugin.common.ParallelErrorEnum;
import com.didiglobal.turbo.plugin.common.MergeStrategy;
import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.exception.ProcessException;
import org.springframework.stereotype.Component;

@Component
public class DataMergeCustom extends DataMergeStrategy{
    @Override
    public InstanceDataPO merge(RuntimeContext runtimeContext, InstanceDataPO forkNodeInstanceData, InstanceDataPO joinNodeInstanceData) {
        // TODO Confirm which part of the data to incorporate into the context based on the groovy script
        throw new ProcessException(ParallelErrorEnum.UNSUPPORTED_DATA_MERGE_STRATEGY.getErrNo(), ParallelErrorEnum.UNSUPPORTED_DATA_MERGE_STRATEGY.getErrMsg());
    }

    @Override
    public String name() {
        return MergeStrategy.DATA_MERGE.CUSTOM;
    }
}