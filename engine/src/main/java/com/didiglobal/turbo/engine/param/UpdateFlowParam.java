package com.didiglobal.turbo.engine.param;

import com.google.common.base.MoreObjects;

public class UpdateFlowParam extends OperationParam {
    private String flowKey;
    private String flowName;
    private String flowModuleId;
    private String flowModel;
    private String remark;

    public UpdateFlowParam(String tenant, String caller) {
        super(tenant, caller);
    }

    public String getFlowKey() {
        return flowKey;
    }

    public void setFlowKey(String flowKey) {
        this.flowKey = flowKey;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getFlowModuleId() {
        return flowModuleId;
    }

    public void setFlowModuleId(String flowModuleId) {
        this.flowModuleId = flowModuleId;
    }

    public String getFlowModel() {
        return flowModel;
    }

    public void setFlowModel(String flowModel) {
        this.flowModel = flowModel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("tenant", getTenant())
                .add("caller", getCaller())
                .add("operator", getOperator())
                .add("flowKey", flowKey)
                .add("flowName", flowName)
                .add("flowModuleId", flowModuleId)
                .add("flowModel", flowModel)
                .add("remark", remark)
                .toString();
    }
}
