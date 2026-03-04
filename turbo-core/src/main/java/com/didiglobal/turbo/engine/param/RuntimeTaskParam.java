package com.didiglobal.turbo.engine.param;

import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.google.common.base.MoreObjects;

import java.util.HashMap;
import java.util.Map;

public class RuntimeTaskParam {
    private String flowInstanceId;
    private String taskInstanceId;
    // For internal transmission runtimeContext
    private RuntimeContext runtimeContext;
    private Map<String, Object> extendProperties = new HashMap<>(16);

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

    public Map<String, Object> getExtendProperties() {
        return extendProperties;
    }

    public void setExtendProperties(Map<String, Object> extendProperties) {
        this.extendProperties = extendProperties;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("flowInstanceId", flowInstanceId)
            .add("taskInstanceId", taskInstanceId)
            .toString();
    }
}
