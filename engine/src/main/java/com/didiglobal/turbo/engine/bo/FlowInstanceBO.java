package com.didiglobal.turbo.engine.bo;

import com.google.common.base.MoreObjects;

public class FlowInstanceBO {
    private String flowInstanceId;
    private String flowDeployId;
    private Integer status;

    public String getFlowInstanceId() {
        return flowInstanceId;
    }

    public void setFlowInstanceId(String flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }

    public String getFlowDeployId() {
        return flowDeployId;
    }

    public void setFlowDeployId(String flowDeployId) {
        this.flowDeployId = flowDeployId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("flowInstanceId", flowInstanceId)
                .add("flowDeployId", flowDeployId)
                .add("status", status)
                .toString();
    }
}
