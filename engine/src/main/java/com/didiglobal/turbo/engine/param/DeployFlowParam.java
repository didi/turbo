package com.didiglobal.turbo.engine.param;

import com.google.common.base.MoreObjects;

public class DeployFlowParam extends OperationParam {
    private String flowModuleId;

    public DeployFlowParam(String tenant, String caller) {
        super(tenant, caller);
    }

    public String getFlowModuleId() {
        return flowModuleId;
    }

    public void setFlowModuleId(String flowModuleId) {
        this.flowModuleId = flowModuleId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("tenant", getTenant())
                .add("caller", getCaller())
                .add("operator", getOperator())
                .add("flowModuleId", flowModuleId)
                .toString();
    }
}
