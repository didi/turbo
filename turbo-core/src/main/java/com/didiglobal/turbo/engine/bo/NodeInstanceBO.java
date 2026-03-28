package com.didiglobal.turbo.engine.bo;

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class NodeInstanceBO implements Serializable {
    //used while updateById
    private Long id;
    private String nodeInstanceId;
    private String nodeKey;
    private String sourceNodeInstanceId;
    private String sourceNodeKey;
    private String instanceDataId;
    private int status;
    private int nodeType;
    private Map<String, Object> properties = new HashMap<>();

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

    public int getNodeType() {
        return nodeType;
    }

    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
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
                .add("id", id)
                .add("nodeInstanceId", nodeInstanceId)
                .add("nodeKey", nodeKey)
                .add("sourceNodeInstanceId", sourceNodeInstanceId)
                .add("sourceNodeKey", sourceNodeKey)
                .add("instanceDataId", instanceDataId)
                .add("status", status)
                .add("nodeType", nodeType)
                .toString();
    }
}
