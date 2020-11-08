package com.xiaoju.uemc.turbo.engine.bo;

import java.util.Map;

/**
 * Created by Stefanie on 2019/12/13.
 */
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

}
