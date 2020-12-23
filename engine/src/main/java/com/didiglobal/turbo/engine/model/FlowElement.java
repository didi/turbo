package com.didiglobal.turbo.engine.model;

import com.google.common.base.MoreObjects;

import java.util.List;
import java.util.Map;

public class FlowElement {
    private String key; // 流程内元素唯一key resourceId
    private int type; // stencil 类型
    private List<String> outgoing;
    private Map<String, Object> properties; // 配置属性
    private List<String> incoming;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getOutgoing() {
        return outgoing;
    }

    public void setOutgoing(List<String> outgoing) {
        this.outgoing = outgoing;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public List<String> getIncoming() {
        return incoming;
    }

    public void setIncoming(List<String> incoming) {
        this.incoming = incoming;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("key", key)
                .add("type", type)
                .add("outgoing", outgoing)
                .add("properties", properties)
                .add("incoming", incoming)
                .toString();
    }
}
