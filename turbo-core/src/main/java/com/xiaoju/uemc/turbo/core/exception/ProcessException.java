package com.xiaoju.uemc.turbo.core.exception;

import com.xiaoju.uemc.turbo.core.common.ErrorEnum;

/**
 * Created by Stefanie on 2019/12/6.
 */
public class ProcessException extends BaseException {

    public ProcessException(int errNo, String errMsg) {
        super(errNo, errMsg);
    }

    public ProcessException(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public ProcessException(ErrorEnum errorEnum, String detailMsg) {
        super(errorEnum, detailMsg);
    }
}