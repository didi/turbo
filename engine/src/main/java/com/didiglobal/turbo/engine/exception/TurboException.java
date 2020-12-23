package com.didiglobal.turbo.engine.exception;

import com.didiglobal.turbo.engine.common.ErrorEnum;

import java.text.MessageFormat;

public class TurboException extends Exception {

    private static final String ERROR_MSG_FORMAT = "{0}({1})";

    private int errNo;
    private String errMsg;

    public TurboException(int errNo, String errMsg) {
        super(errMsg);
        this.errNo = errNo;
        this.errMsg = errMsg;
    }

    public TurboException(ErrorEnum errorEnum) {
        super(errorEnum.getErrMsg());
        this.errNo = errorEnum.getErrNo();
        this.errMsg = errorEnum.getErrMsg();
    }

    public TurboException(ErrorEnum errorEnum, String detailMsg) {
        super(errorEnum.getErrMsg());
        String errMsg = MessageFormat.format(ERROR_MSG_FORMAT, errorEnum.getErrMsg(), detailMsg);
        this.errNo = errorEnum.getErrNo();
        this.errMsg = errMsg;
    }

    public int getErrNo() {
        return errNo;
    }

    public void setErrNo(int errNo) {
        this.errNo = errNo;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
