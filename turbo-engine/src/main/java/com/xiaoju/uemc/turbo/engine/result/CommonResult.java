package com.xiaoju.uemc.turbo.engine.result;


import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import lombok.Data;

/**
 * Created by Stefanie on 2019/12/8.
 */
@Data
public class CommonResult {

    private int errCode;
    private String errMsg;

    public CommonResult() {
        super();
    }

    public CommonResult(ErrorEnum errorEnum) {
        this.errCode = errorEnum.getErrNo();
        this.errMsg = errorEnum.getErrMsg();
    }
}
