package com.didiglobal.turbo.plugin.executor;


import com.didiglobal.turbo.plugin.common.MergeStrategy;
import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.util.InstanceDataUtil;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataMergeNone extends DataMergeStrategy{

    @Override
    public InstanceDataPO merge(RuntimeContext runtimeContext, InstanceDataPO forkNodeInstanceData, InstanceDataPO joinNodeInstanceData) {
        // update runtime context instance data map to be same to fork node data
        runtimeContext.getInstanceDataMap().clear();
        Map<String, InstanceData> instanceDataMap = InstanceDataUtil.getInstanceDataMap(forkNodeInstanceData.getInstanceData());
        runtimeContext.getInstanceDataMap().putAll(instanceDataMap);
        // merge nothing
        return forkNodeInstanceData;
    }

    @Override
    public String name() {
        return MergeStrategy.DATA_MERGE.NONE;
    }
}