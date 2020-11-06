package com.xiaoju.uemc.turbo.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.base.MoreObjects;

/**
 * Created by Stefanie on 2019/11/27.
 */
@TableName("ei_node_instance_log")
public class NodeInstanceLogPO extends CommonPO {

    private String nodeInstanceId;
    private String flowInstanceId;
    private String instanceDataId;
    private String nodeKey;
    private Integer type;
    private Integer status;

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

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
                .add("nodeInstanceId", nodeInstanceId)
                .add("flowInstanceId", flowInstanceId)
                .add("instanceDataId", instanceDataId)
                .add("nodeKey", nodeKey)
                .add("type", type)
                .add("status", status)
                .toString();
    }
}
