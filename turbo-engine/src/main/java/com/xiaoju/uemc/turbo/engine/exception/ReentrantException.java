package com.xiaoju.uemc.turbo.engine.exception;

import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;

/**
 * Created by Stefanie on 2019/12/8.
 */
public class ReentrantException extends ProcessException {

    public ReentrantException(int errNo, String errMsg) {
        super(errNo, errMsg);
    }

    public ReentrantException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}