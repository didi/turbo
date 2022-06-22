package com.didiglobal.turbo.demo.pojo.response;

import com.didiglobal.turbo.engine.common.ErrorEnum;

/**
 * @Author: james zhangxiao
 * @Date: 4/6/22
 * @Description:
 */
public class BaseResponse<T> {

    int errCode;
    String errMsg;
    T data;


    public BaseResponse(ErrorEnum errorEnum) {
        this.errCode = errorEnum.getErrNo();
        this.errMsg = errorEnum.getErrMsg();
    }

    public static <T> BaseResponse<T> make(T data) {
        return (new BaseResponse<T>(ErrorEnum.SUCCESS)).setData(data);
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

    public T getData() {
        return data;
    }

    public BaseResponse<T> setData(T data) {
        this.data = data;
        return this;
    }
}
