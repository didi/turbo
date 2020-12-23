package com.didiglobal.turbo.engine.bo;

import com.google.common.base.MoreObjects;

import java.util.Date;

public class NodeInstance extends ElementInstance {
    private String nodeInstanceId;
    private Date createTime;
    private Date modifyTime;

    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(String nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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
                .add("modelKey", getModelKey())
                .add("modelName", getModelName())
                .add("properties", getProperties())
                .add("status", getStatus())
                .add("nodeInstanceId", nodeInstanceId)
                .add("createTime", createTime)
                .add("modifyTime", modifyTime)
                .toString();
    }
}
