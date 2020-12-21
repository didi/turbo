package com.xiaoju.uemc.turbo.engine.exception;


import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import lombok.Data;
import lombok.ToString;

import java.text.MessageFormat;

/**
 * Created by Stefanie on 2019/12/5.
 */
@Data
@ToString(callSuper = true)
public class TurboException extends Exception {

    private static final String ERROR_MSG_FORMAT = "{0}({1})";

    private int errNo;
    private String errMsg;

    public TurboException(int errNo, String errMsg) {
        super(errMsg);
        this.errNo = errNo;
        this.errMsg = errMsg;
    }

    public TurboException(ErrorEnum errorEnum) {
        super(errorEnum.getErrMsg());
        this.errNo = errorEnum.getErrNo();
        this.errMsg = errorEnum.getErrMsg();
    }

    public TurboException(ErrorEnum errorEnum, String detailMsg) {
        super(errorEnum.getErrMsg());
        String errMsg = MessageFormat.format(ERROR_MSG_FORMAT, errorEnum.getErrMsg(), detailMsg);
        this.errNo = errorEnum.getErrNo();
        this.errMsg = errMsg;
    }
}
