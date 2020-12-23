package com.didiglobal.turbo.engine.result;

import com.google.common.base.MoreObjects;

public class StartProcessResult extends RuntimeResult {
    private String flowDeployId;
    private String flowModuleId;

    public String getFlowDeployId() {
        return flowDeployId;
    }

    public void setFlowDeployId(String flowDeployId) {
        this.flowDeployId = flowDeployId;
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
                .add("errCode", getErrCode())
                .add("errMsg", getErrMsg())
                .add("flowDeployId", flowDeployId)
                .add("flowModuleId", flowModuleId)
                .toString();
    }
}
