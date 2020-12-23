package com.didiglobal.turbo.engine.result;

import com.google.common.base.MoreObjects;

public class CreateFlowResult extends CommonResult {
    private String flowModuleId;

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
                .add("flowModuleId", flowModuleId)
                .toString();
    }
}
