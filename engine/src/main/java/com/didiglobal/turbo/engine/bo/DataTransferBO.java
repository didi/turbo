package com.didiglobal.turbo.engine.bo;

/**
 * The data transfer object is used in the scenario of parent to child
 * and child to parent of the CallActivity.
 * <p>
 * 1.parent to child
 * 2.child to parent
 */
public class DataTransferBO {

    private String sourceType;
    private String sourceKey;
    private String sourceValue;
    private String targetKey;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }

    public String getTargetKey() {
        return targetKey;
    }

    public void setTargetKey(String targetKey) {
        this.targetKey = targetKey;
    }
}
