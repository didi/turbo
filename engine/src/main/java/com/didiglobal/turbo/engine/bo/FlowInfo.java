package com.didiglobal.turbo.engine.bo;

import com.google.common.base.MoreObjects;

public class FlowInfo extends FlowBasicInfo {
    private String flowModel;

    public String getFlowModel() {
        return flowModel;
    }

    public void setFlowModel(String flowModel) {
        this.flowModel = flowModel;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("flowDeployId", getFlowDeployId())
                .add("flowModuleId", getFlowModuleId())
                .add("tenant", getTenant())
                .add("caller", getCaller())
                .add("flowModel", flowModel)
                .toString();
    }
}
