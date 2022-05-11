package com.didiglobal.turbo.demo.pojo.request;

import com.didiglobal.turbo.demo.util.Constant;
import com.didiglobal.turbo.engine.param.DeployFlowParam;

/**
 * @Author: james zhangxiao
 * @Date: 4/1/22
 * @Description:
 */
public class DeployFlowRequest extends DeployFlowParam {

    public DeployFlowRequest() {
        super(Constant.tenant, Constant.caller);
    }
}
