package com.xiaoju.uemc.turbo.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.base.MoreObjects;

import java.util.Date;

/**
 * Created by Stefanie on 2019/11/27.
 */
@TableName("ei_node_instance")
public class NodeInstancePO extends CommonPO {

    private String flowInstanceId;
    private String flowDeployId;
    private String instanceDataId;
    private String nodeInstanceId;
    private String sourceNodeInstanceId;
    private String nodeKey;
    private String sourceNodeKey;
    private Integer status;
    private Date modifyTime;

    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(String nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    public String getFlowInstanceId() {
        return flowInstanceId;
    }

    public void setFlowInstanceId(String flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }

    public String getSourceNodeInstanceId() {
        return sourceNodeInstanceId;
    }

    public void setSourceNodeInstanceId(String sourceNodeInstanceId) {
        this.sourceNodeInstanceId = sourceNodeInstanceId;
    }

    public String getInstanceDataId() {
        return instanceDataId;
    }

    public void setInstanceDataId(String instanceDataId) {
        this.instanceDataId = instanceDataId;
    }

    public String getFlowDeployId() {
        return flowDeployId;
    }

    public void setFlowDeployId(String flowDeployId) {
        this.flowDeployId = flowDeployId;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getSourceNodeKey() {
        return sourceNodeKey;
    }

    public void setSourceNodeKey(String sourceNodeKey) {
        this.sourceNodeKey = sourceNodeKey;
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
                .add("nodeInstanceId", nodeInstanceId)
                .add("flowInstanceId", flowInstanceId)
                .add("sourceNodeInstanceId", sourceNodeInstanceId)
                .add("instanceDataId", instanceDataId)
                .add("flowDeployId", flowDeployId)
                .add("nodeKey", nodeKey)
                .add("sourceNodeKey", sourceNodeKey)
                .add("status", status)
                .add("modifyTime", modifyTime)
                .toString();
    }
}
