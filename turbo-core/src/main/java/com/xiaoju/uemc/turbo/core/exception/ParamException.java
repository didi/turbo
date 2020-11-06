package com.xiaoju.uemc.turbo.core.exception;

import com.xiaoju.uemc.turbo.core.common.ErrorEnum;

/**
 * Created by Stefanie on 2019/12/5.
 */
public class ParamException extends ProcessException {

    public ParamException(int errNo, String errMsg) {
        super(errNo, errMsg);
    }

    public ParamException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}

