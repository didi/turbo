package com.didiglobal.turbo.engine.model;

import com.google.common.base.MoreObjects;

import java.util.List;
import java.util.Map;

public class FlowElement {
    /**
     * Unique key in flow element, resourceId
     */
    private String key;

    /**
     * The type of element
     */
    private int type;

    /**
     * List of elements before the current element
     */
    private List<String> incoming;

    /**
     * List of elements following the current element
     */
    private List<String> outgoing;

    /**
     * Element configuration attributes, which can be used to extend
     */
    private Map<String, Object> properties;

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
