package com.xiaoju.uemc.turbo.core.exception;


import com.xiaoju.uemc.turbo.core.common.ErrorEnum;

public class BusinessException extends BaseException {

    public BusinessException(int errNo, String errMsg) {
        super(errNo, errMsg);
    }

    public BusinessException(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public BusinessException(ErrorEnum errorEnum, String detailMsg) {
        super(errorEnum, detailMsg);
    }
}