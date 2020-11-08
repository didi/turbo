package com.xiaoju.uemc.turbo.engine.dto;


import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;

/**
 * Created by Stefanie on 2019/12/8.
 */
public class CommonDTO {

    private int errCode;
    private String errMsg;

    public CommonDTO() {
        super();
    }

    public CommonDTO(ErrorEnum errorEnum) {
        this.errCode = errorEnum.getErrNo();
        this.errMsg = errorEnum.getErrMsg();
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
