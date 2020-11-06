package com.xiaoju.uemc.turbo.core.param;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.MoreObjects;
import com.xiaoju.uemc.turbo.core.model.InstanceData;

import java.util.List;

/**
 * Created by Stefanie on 2019/12/2.
 */
public class CommitTaskParam extends RuntimeTaskParam {

    private List<InstanceData> variables;

    public List<InstanceData> getVariables() {
        return variables;
    }

    public void setVariables(List<InstanceData> variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("flowInstanceId", getFlowInstanceId())
                .add("taskInstanceId", getTaskInstanceId())
                .add("variables", JSONObject.toJSONString(variables))
                .toString();
    }
}
