package com.xiaoju.uemc.turbo.engine.dto;

import com.google.common.base.MoreObjects;

/**
 * Created by Stefanie on 2019/12/2.
 */
public class CreateFlowDTO {

    String flowModuleId;

    public String getFlowModuleId() {
        return flowModuleId;
    }

    public void setFlowModuleId(String flowModuleId) {
        this.flowModuleId = flowModuleId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("flowModuleId", flowModuleId)
                .toString();
    }
}
