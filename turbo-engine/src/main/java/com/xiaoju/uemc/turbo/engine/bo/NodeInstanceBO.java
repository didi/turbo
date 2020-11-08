package com.xiaoju.uemc.turbo.engine.bo;

import com.google.common.base.MoreObjects;

/**
 * Created by Stefanie on 2019/12/5.
 */
public class NodeInstanceBO {
    //used while updateById
    private Long id;
    private String nodeInstanceId;
    private String nodeKey;
    private String sourceNodeInstanceId;
    private String sourceNodeKey;
    private String instanceDataId;
    private int status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(String nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getSourceNodeInstanceId() {
        return sourceNodeInstanceId;
    }

    public void setSourceNodeInstanceId(String sourceNodeInstanceId) {
        this.sourceNodeInstanceId = sourceNodeInstanceId;
    }

    public String getSourceNodeKey() {
        return sourceNodeKey;
    }

    public void setSourceNodeKey(String sourceNodeKey) {
        this.sourceNodeKey = sourceNodeKey;
    }

    public String getInstanceDataId() {
        return instanceDataId;
    }

    public void setInstanceDataId(String instanceDataId) {
        this.instanceDataId = instanceDataId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("nodeInstanceId", nodeInstanceId)
                .add("nodeKey", nodeKey)
                .add("sourceNodeInstanceId", sourceNodeInstanceId)
                .add("sourceNodeKey", sourceNodeKey)
                .add("instanceDataId", instanceDataId)
                .add("status", status)
                .toString();
    }
}
