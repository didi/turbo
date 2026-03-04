package com.didiglobal.turbo.engine.exception;

import com.didiglobal.turbo.engine.common.ErrorEnum;

public class ProcessException extends TurboException {

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
