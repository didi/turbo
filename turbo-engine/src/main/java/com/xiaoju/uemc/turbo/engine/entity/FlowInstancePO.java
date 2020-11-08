package com.xiaoju.uemc.turbo.engine.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.base.MoreObjects;

import java.util.Date;

/**
 * Created by Stefanie on 2019/11/27.
 */
@TableName("ei_flow_instance")
public class FlowInstancePO extends CommonPO {

    private String flowInstanceId;
    private String flowDeployId;
    private String flowModuleId;
    private Integer status;
    private Date modifyTime;

    public String getFlowInstanceId() {
        return flowInstanceId;
    }

    public void setFlowInstanceId(String flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("flowInstanceId", flowInstanceId)
                .add("flowDeployId", flowDeployId)
                .add("flowModuleId", flowModuleId)
                .add("status", status)
                .add("modifyTime", modifyTime)
                .toString();
    }
}
