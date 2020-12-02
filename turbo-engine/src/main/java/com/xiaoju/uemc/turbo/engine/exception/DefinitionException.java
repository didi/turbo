package com.xiaoju.uemc.turbo.engine.exception;

import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;

/**
 * Created by Stefanie on 2019/12/6.
 */
public class DefinitionException extends TurboException {

    public DefinitionException(int errNo, String errMsg) {
        super(errNo, errMsg);
    }

    public DefinitionException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
