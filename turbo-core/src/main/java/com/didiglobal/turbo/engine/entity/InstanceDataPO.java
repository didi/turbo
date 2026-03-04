package com.didiglobal.turbo.engine.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("ei_instance_data")
public class InstanceDataPO extends CommonPO {
    private String flowInstanceId;
    private String instanceDataId;
    private String nodeInstanceId;
    private String flowDeployId;
    private String flowModuleId;
    private String nodeKey;
    private String instanceData;
    private String instanceDataEncode;
    private Integer type;

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

    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(String nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
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

    public String getInstanceDataEncode() {
        return instanceDataEncode;
    }

    public void setInstanceDataEncode(String instanceDataEncode) {
        this.instanceDataEncode = instanceDataEncode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
