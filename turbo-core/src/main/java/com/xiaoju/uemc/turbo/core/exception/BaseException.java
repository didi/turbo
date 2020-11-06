package com.xiaoju.uemc.turbo.core.exception;


import com.xiaoju.uemc.turbo.core.common.ErrorEnum;

import java.text.MessageFormat;

/**
 * Created by Stefanie on 2019/12/5.
 */
public class BaseException extends Exception {

    private static final String ERROR_MSG_FORMAT = "{0}({1})";

    private int errNo;
    private String errMsg;

    public BaseException(int errNo, String errMsg) {
        super(errMsg);
        this.errNo = errNo;
        this.errMsg = errMsg;
    }

    public BaseException(ErrorEnum errorEnum) {
        super(errorEnum.getErrMsg());
        this.errNo = errorEnum.getErrNo();
        this.errMsg = errorEnum.getErrMsg();
    }

    public BaseException(ErrorEnum errorEnum, String detailMsg) {
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
