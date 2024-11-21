package com.didiglobal.turbo.plugin.executor;

import com.didiglobal.turbo.plugin.common.MergeStrategy;
import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.util.InstanceDataUtil;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataMergeAll extends DataMergeStrategy{

    @Override
    public InstanceDataPO merge(RuntimeContext runtimeContext, InstanceDataPO forkNodeInstanceData, InstanceDataPO joinNodeInstanceData) {
        Map<String, InstanceData> instanceDataMap = runtimeContext.getInstanceDataMap();
        if(instanceDataMap == null){
            instanceDataMap = Maps.newHashMap();
        }
        Map<String, InstanceData> forkInstanceDataMap = InstanceDataUtil.getInstanceDataMap(getForkData(forkNodeInstanceData));
        Map<String, InstanceData> joinInstanceDataMap = InstanceDataUtil.getInstanceDataMap(joinNodeInstanceData.getInstanceData());
        forkInstanceDataMap.putAll(joinInstanceDataMap);
        forkInstanceDataMap.putAll(instanceDataMap);

        runtimeContext.setInstanceDataMap(forkInstanceDataMap);
        String dataListStr = InstanceDataUtil.getInstanceDataListStr(forkInstanceDataMap);
        joinNodeInstanceData.setInstanceData(dataListStr);
        return joinNodeInstanceData;
    }

    private String getForkData(InstanceDataPO forkNodeInstanceData) {
        return forkNodeInstanceData == null ? null : forkNodeInstanceData.getInstanceData();
    }

    @Override
    public String name() {
        return MergeStrategy.DATA_MERGE.ALL;
    }
}