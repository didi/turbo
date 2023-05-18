package com.didiglobal.turbo.engine.param;

import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.google.common.base.MoreObjects;

public class RuntimeTaskParam {
    private String flowInstanceId;
    private String taskInstanceId;
    // For internal transmission runtimeContext
    private RuntimeContext runtimeContext;

    public String getFlowInstanceId() {
        return flowInstanceId;
    }

    public void setFlowInstanceId(String flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }

    public String getTaskInstanceId() {
        return taskInstanceId;
    }

    public void setTaskInstanceId(String taskInstanceId) {
        this.taskInstanceId = taskInstanceId;
    }

    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("flowInstanceId", flowInstanceId)
            .add("taskInstanceId", taskInstanceId)
            .toString();
    }
}
