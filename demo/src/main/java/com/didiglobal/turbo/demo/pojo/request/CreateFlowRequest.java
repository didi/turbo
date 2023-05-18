package com.didiglobal.turbo.demo.pojo.request;

import com.didiglobal.turbo.demo.util.Constant;
import com.didiglobal.turbo.engine.param.CreateFlowParam;

/**
 * @Author: james zhangxiao
 * @Date: 4/1/22
 * @Description:
 */
public class CreateFlowRequest extends CreateFlowParam {

    public CreateFlowRequest() {
        super(Constant.tenant, Constant.caller);
    }

}
