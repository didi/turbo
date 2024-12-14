package com.didiglobal.turbo.demo.pojo.response;

/**
 * @Author: james zhangxiao
 * @Date: 4/6/22
 * @Description:
 */
public class DeployFlowResponse {

    private String flowModuleId;

    private String flowDeployId;

    public String getFlowModuleId() {
        return flowModuleId;
    }

    public void setFlowModuleId(String flowModuleId) {
        this.flowModuleId = flowModuleId;
    }

    public String getFlowDeployId() {
        return flowDeployId;
    }

    public void setFlowDeployId(String flowDeployId) {
        this.flowDeployId = flowDeployId;
    }
}
