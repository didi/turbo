package com.xiaoju.uemc.turbo.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.base.MoreObjects;

/**
 * Created by Stefanie on 2019/11/27.
 */
@TableName("em_flow_deployment")
public class FlowDeploymentPO extends OperationPO {

    private String flowDeployId;
    private String flowModuleId;
    private String flowName;
    private String flowKey;
    private String flowModel;
    private Integer status;

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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("flowDeployId", flowDeployId)
                .add("flowModuleId", flowModuleId)
                .add("flowName", flowName)
                .add("flowKey", flowKey)
                .add("flowModel", flowModel)
                .add("status", status)
                .toString();
    }
}
