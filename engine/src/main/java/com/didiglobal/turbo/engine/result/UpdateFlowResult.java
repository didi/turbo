package com.didiglobal.turbo.engine.result;

import com.google.common.base.MoreObjects;

public class UpdateFlowResult extends CommonResult {
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("errCode", getErrCode())
                .add("errMsg", getErrMsg())
                .toString();
    }
}
