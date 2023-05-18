package com.didiglobal.turbo.engine.bo;

import com.google.common.base.MoreObjects;

import java.util.List;
import java.util.Map;

public class ElementInstance {

    private String modelKey;
    private String modelName;
    private Map<String, Object> properties;
    private int status;
    private String nodeInstanceId;
    private List<String> subFlowInstanceIdList;
    private List<ElementInstance> subElementInstanceList;
    private String instanceDataId;

    public ElementInstance() {
        super();
    }

    public ElementInstance(String modelKey, int status, String nodeInstanceId, String instanceDataId) {
        super();
        this.modelKey = modelKey;
        this.status = status;
        this.nodeInstanceId = nodeInstanceId;
        this.instanceDataId = instanceDataId;
    }

    public String getModelKey() {
        return modelKey;
    }

    public void setModelKey(String modelKey) {
        this.modelKey = modelKey;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(String nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    public List<String> getSubFlowInstanceIdList() {
        return subFlowInstanceIdList;
    }

    public void setSubFlowInstanceIdList(List<String> subFlowInstanceIdList) {
        this.subFlowInstanceIdList = subFlowInstanceIdList;
    }

    public String getInstanceDataId() {
        return instanceDataId;
    }

    public void setInstanceDataId(String instanceDataId) {
        this.instanceDataId = instanceDataId;
    }

    public List<ElementInstance> getSubElementInstanceList() {
        return subElementInstanceList;
    }

    public void setSubElementInstanceList(List<ElementInstance> subElementInstanceList) {
        this.subElementInstanceList = subElementInstanceList;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("modelKey", modelKey)
            .add("modelName", modelName)
            .add("properties", properties)
            .add("status", status)
            .add("nodeInstanceId", nodeInstanceId)
            .add("subFlowInstanceIdList", subFlowInstanceIdList)
            .add("instanceDataId", instanceDataId)
            .toString();
    }
}
