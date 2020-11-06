package com.xiaoju.uemc.turbo.core.dto;

import com.google.common.base.MoreObjects;

/**
 * Created by Stefanie on 2019/12/2.
 */
public class StartProcessDTO extends RuntimeDTO {

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
                .add("flowInstanceId", getFlowInstanceId())
                .add("status", getStatus())
                .add("activeTaskInstance", getActiveTaskInstance())
                .add("variables", getVariables())
                .add("flowDeployId", flowDeployId)
                .add("flowModuleId", flowModuleId)
                .toString();
    }
}
