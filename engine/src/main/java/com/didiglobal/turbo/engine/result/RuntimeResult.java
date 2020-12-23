package com.didiglobal.turbo.engine.result;

import com.didiglobal.turbo.engine.bo.NodeInstance;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.google.common.base.MoreObjects;

import java.util.List;

public class RuntimeResult extends CommonResult {
    private String flowInstanceId;
    private int status;
    private NodeInstance activeTaskInstance;
    private List<InstanceData> variables;

    public RuntimeResult() {
        super();
    }

    public RuntimeResult(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public String getFlowInstanceId() {
        return flowInstanceId;
    }

    public void setFlowInstanceId(String flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public NodeInstance getActiveTaskInstance() {
        return activeTaskInstance;
    }

    public void setActiveTaskInstance(NodeInstance activeTaskInstance) {
        this.activeTaskInstance = activeTaskInstance;
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
                .add("errCode", getErrCode())
                .add("errMsg", getErrMsg())
                .add("flowInstanceId", flowInstanceId)
                .add("status", status)
                .add("activeTaskInstance", activeTaskInstance)
                .add("variables", variables)
                .toString();
    }
}
