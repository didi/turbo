package com.didiglobal.turbo.engine.bo;

import com.google.common.base.MoreObjects;

import java.util.HashMap;
import java.util.Map;

public class FlowInstanceBO {
    private String flowInstanceId;
    private String flowDeployId;
    private String flowModuleId;
    private Integer status;
    private String parentFlowInstanceId;
    private Map<String, Object> properties = new HashMap<>();

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

    public String getParentFlowInstanceId() {
        return parentFlowInstanceId;
    }

    public void setParentFlowInstanceId(String parentFlowInstanceId) {
        this.parentFlowInstanceId = parentFlowInstanceId;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Object get(String key) {
        return properties.get(key);
    }

    public void put(String key, Object value) {
        properties.put(key, value);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("flowInstanceId", flowInstanceId)
            .add("flowDeployId", flowDeployId)
            .add("flowModuleId", flowModuleId)
            .add("status", status)
            .add("parentFlowInstanceId", parentFlowInstanceId)
            .toString();
    }
}
