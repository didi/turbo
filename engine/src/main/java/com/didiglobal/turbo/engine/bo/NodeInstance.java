package com.didiglobal.turbo.engine.bo;

import com.didiglobal.turbo.engine.result.RuntimeResult;
import com.google.common.base.MoreObjects;

import java.util.Date;
import java.util.List;

public class NodeInstance extends ElementInstance {
    private String nodeInstanceId;
    private int flowElementType;
    private List<RuntimeResult> subNodeResultList;
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

    public List<RuntimeResult> getSubNodeResultList() {
        return subNodeResultList;
    }

    public void setSubNodeResultList(List<RuntimeResult> subNodeResultList) {
        this.subNodeResultList = subNodeResultList;
    }

    public int getFlowElementType() {
        return flowElementType;
    }

    public void setFlowElementType(int flowElementType) {
        this.flowElementType = flowElementType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("modelKey", getModelKey())
            .add("modelName", getModelName())
            .add("properties", getProperties())
            .add("status", getStatus())
            .add("nodeInstanceId", nodeInstanceId)
            .add("subRuntimeResultList", subNodeResultList)
            .add("flowElementType", flowElementType)
                .add("createTime", createTime)
                .add("modifyTime", modifyTime)
                .toString();
    }
}
