package com.didiglobal.turbo.engine.result;

import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.google.common.base.MoreObjects;

public class CommonResult {

    private int errCode;
    private String errMsg;

    public CommonResult() {
        super();
    }

    public CommonResult(ErrorEnum errorEnum) {
        this.errCode = errorEnum.getErrNo();
        this.errMsg = errorEnum.getErrMsg();
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("errCode", errCode)
                .add("errMsg", errMsg)
                .toString();
    }
}
