package com.xiaoju.uemc.turbo.engine.dto;

import com.google.common.base.MoreObjects;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;

import java.util.List;

/**
 * Created by Stefanie on 2019/12/5.
 */
public class RuntimeDTO extends CommonDTO {

    private String flowInstanceId;
    private int status;
    private NodeInstanceDTO activeTaskInstance;
    private List<InstanceData> variables;

    public RuntimeDTO() {
        super();
    }

    public RuntimeDTO(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public String getFlowInstanceId() {
        return flowInstanceId;
    }

    public void setFlowInstanceId(String flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public NodeInstanceDTO getActiveTaskInstance() {
        return activeTaskInstance;
    }

    public void setActiveTaskInstance(NodeInstanceDTO activeTaskInstance) {
        this.activeTaskInstance = activeTaskInstance;
    }

    public List<InstanceData> getVariables() {
        return variables;
    }

    public void setVariables(List<InstanceData> variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("errCode", getErrCode())
                .add("errMsg", getErrMsg())
                .add("flowInstanceId", flowInstanceId)
                .add("status", status)
                .add("activeTaskInstance", activeTaskInstance)
                .add("variables", variables)
                .toString();
    }
}
