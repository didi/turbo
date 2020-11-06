package com.xiaoju.uemc.turbo.core.exception;

import com.xiaoju.uemc.turbo.core.common.ErrorEnum;

/**
 * Created by Stefanie on 2019/12/6.
 */
public class ModelException extends BaseException {

    public ModelException(int errNo, String errMsg) {
        super(errNo, errMsg);
    }

    public ModelException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
