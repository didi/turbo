package com.xiaoju.uemc.turbo.engine.param;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.MoreObjects;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;

import java.util.List;

/**
 * Created by Stefanie on 2019/12/2.
 */
public class StartProcessParam {

    private String flowModuleId;

    private String flowDeployId;

    private List<InstanceData> variables;

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

    public List<InstanceData> getVariables() {
        return variables;
    }

    public void setVariables(List<InstanceData> variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("flowModuleId", flowModuleId)
                .add("flowDeployId", flowDeployId)
                .add("variables", JSONObject.toJSONString(variables))
                .toString();
    }
}
