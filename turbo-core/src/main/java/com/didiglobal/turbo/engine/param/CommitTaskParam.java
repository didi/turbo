package com.didiglobal.turbo.engine.param;

import com.didiglobal.turbo.engine.model.InstanceData;
import com.google.common.base.MoreObjects;

import java.util.List;

public class CommitTaskParam extends RuntimeTaskParam {
    private List<InstanceData> variables;
    // Used to specify the FlowModuleId when commit CallActivity node
    private String callActivityFlowModuleId;

    public List<InstanceData> getVariables() {
        return variables;
    }

    public void setVariables(List<InstanceData> variables) {
        this.variables = variables;
    }

    public String getCallActivityFlowModuleId() {
        return callActivityFlowModuleId;
    }

    public void setCallActivityFlowModuleId(String callActivityFlowModuleId) {
        this.callActivityFlowModuleId = callActivityFlowModuleId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("flowInstanceId", getFlowInstanceId())
            .add("taskInstanceId", getTaskInstanceId())
            .add("variables", variables)
            .add("callActivityFlowModuleId", callActivityFlowModuleId)
            .toString();
    }
}
