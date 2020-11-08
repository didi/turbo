package com.xiaoju.uemc.turbo.engine.dto;

import com.google.common.base.MoreObjects;

import java.util.Date;

/**
 * Created by Stefanie on 2019/12/5.
 */
public class NodeInstanceDTO extends ElementInstanceDTO {

    private String nodeInstanceId;
    private Date createTime;
    private Date modifyTime;

    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(String nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("nodeInstanceId", nodeInstanceId)
                .add("modelKey", getModelKey())
                .add("modelName", getModelName())
                .add("properties", getProperties())
                .add("status", getStatus())
                .add("createTime", createTime)
                .add("modifyTime", modifyTime)
                .toString();
    }
}
