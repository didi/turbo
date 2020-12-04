package com.xiaoju.uemc.turbo.engine.exception;

import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;

/**
 * Created by Stefanie on 2019/12/5.
 */
public class ParamException extends TurboException {

    public ParamException(int errNo, String errMsg) {
        super(errNo, errMsg);
    }

    public ParamException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}

