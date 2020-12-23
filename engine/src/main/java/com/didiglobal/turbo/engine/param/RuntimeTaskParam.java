package com.didiglobal.turbo.engine.param;

import com.google.common.base.MoreObjects;

public class RuntimeTaskParam {
    private String flowInstanceId;
    private String taskInstanceId;

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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("flowInstanceId", flowInstanceId)
                .add("taskInstanceId", taskInstanceId)
                .toString();
    }
}
