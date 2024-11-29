package com.didiglobal.turbo.plugin.executor;

import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;

public abstract class DataMergeStrategy {

    /**
     * merge data. update instanceDataPO.instanceData
     *
     * @param runtimeContext       runtimeContext
     * @param forkNodeInstanceData from db
     * @param joinNodeInstanceData from db
     * @return InstanceDataPO
     */
    public abstract InstanceDataPO merge(RuntimeContext runtimeContext, InstanceDataPO forkNodeInstanceData, InstanceDataPO joinNodeInstanceData);

    /**
     * strategy name
     *
     * @return name
     */
    public abstract String name();
}