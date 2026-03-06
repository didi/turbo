package com.didiglobal.turbo.engine.result;

import com.google.common.base.MoreObjects;

public class CommitTaskResult extends RuntimeResult {

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("errCode", getErrCode())
                .add("errMsg", getErrMsg())
                .toString();
    }
}
