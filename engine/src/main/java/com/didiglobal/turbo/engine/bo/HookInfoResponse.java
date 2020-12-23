package com.didiglobal.turbo.engine.bo;

import com.google.common.base.MoreObjects;

import java.util.Map;

public class HookInfoResponse {
    private int status;
    private String error;
    private String detailMessage;
    private Map<String, Object> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("status", status)
                .add("error", error)
                .add("detailMessage", detailMessage)
                .add("data", data)
                .toString();
    }
}
