package com.xiaoju.uemc.turbo.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.base.MoreObjects;

/**
 * Created by Stefanie on 2019/11/27.
 */
@TableName("ei_instance_data")
public class InstanceDataPO extends CommonPO {

    private String flowInstanceId;
    private String instanceDataId;
    private String nodeInstanceId;
    private String flowDeployId;
    private String flowModuleId;
    private String nodeKey;
    private String instanceData;
    private Integer type;

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

    public String getFlowModuleId() {
        return flowModuleId;
    }

    public void setFlowModuleId(String flowModuleId) {
        this.flowModuleId = flowModuleId;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getInstanceData() {
        return instanceData;
    }

    public void setInstanceData(String instanceData) {
        this.instanceData = instanceData;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("nodeInstanceId", nodeInstanceId)
                .add("flowInstanceId", flowInstanceId)
                .add("instanceDataId", instanceDataId)
                .add("flowDeployId", flowDeployId)
                .add("flowModuleId", flowModuleId)
                .add("nodeKey", nodeKey)
                .add("instanceData", instanceData)
                .add("type", type)
                .toString();
    }
}
