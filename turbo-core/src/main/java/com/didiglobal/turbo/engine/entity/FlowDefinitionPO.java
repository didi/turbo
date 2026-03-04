package com.didiglobal.turbo.engine.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("em_flow_definition")
public class FlowDefinitionPO extends OperationPO {
    private String flowModuleId;
    private String flowName;
    private String flowKey;
    private String flowModel;
    private Integer status;

    public String getFlowModuleId() {
        return flowModuleId;
    }

    public void setFlowModuleId(String flowModuleId) {
        this.flowModuleId = flowModuleId;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getFlowKey() {
        return flowKey;
    }

    public void setFlowKey(String flowKey) {
        this.flowKey = flowKey;
    }

    public String getFlowModel() {
        return flowModel;
    }

    public void setFlowModel(String flowModel) {
        this.flowModel = flowModel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
