package com.xiaoju.uemc.turbo.core.dto;

import com.google.common.base.MoreObjects;

/**
 * Created by Stefanie on 2019/12/2.
 */
public class DeployFlowDTO {

    String flowModuleId;
    String flowDeployId;

    public String getFlowModuleId() {
        return flowModuleId;
    }

    public void setFlowModuleId(String flowModuleId) {
        this.flowModuleId = flowModuleId;
    }

    public String getFlowDeployId() {
        return flowDeployId;
    }

    public void setFlowDeployId(String flowDeployId) {
        this.flowDeployId = flowDeployId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("flowModuleId", flowModuleId)
                .add("flowDeployId", flowDeployId)
                .toString();
    }
}
