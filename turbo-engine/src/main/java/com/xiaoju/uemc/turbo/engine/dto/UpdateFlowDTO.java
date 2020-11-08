package com.xiaoju.uemc.turbo.engine.dto;

import com.google.common.base.MoreObjects;

/**
 * Created by Stefanie on 2019/12/2.
 */
public class UpdateFlowDTO {

    boolean isSuccess;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("isSuccess", isSuccess)
                .toString();
    }
}
