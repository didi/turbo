package com.didiglobal.turbo.engine.exception;

import com.didiglobal.turbo.engine.common.ErrorEnum;

public class ReentrantException extends ProcessException {

    public ReentrantException(int errNo, String errMsg) {
        super(errNo, errMsg);
    }

    public ReentrantException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
