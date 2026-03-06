package com.didiglobal.turbo.engine.bo;

import com.google.common.base.MoreObjects;

public class FlowBasicInfo {
    private String flowDeployId;
    private String flowModuleId;
    private String tenant;
    private String caller;

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

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("flowDeployId", flowDeployId)
                .add("flowModuleId", flowModuleId)
                .add("tenant", tenant)
                .add("caller", caller)
                .toString();
    }
}
