package com.didiglobal.turbo.engine.model;

import com.google.common.base.MoreObjects;

public class InstanceData {
    private String key;
    private String type;
    private Object value;

    public InstanceData(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public InstanceData(String key, String type, Object value) {
        this.key = key;
        this.type = type;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("key", key)
                .add("type", type)
                .add("value", value)
                .toString();
    }
}
