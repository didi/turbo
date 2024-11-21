package com.didiglobal.turbo.engine.common;

import com.didiglobal.turbo.engine.bo.NodeInstanceBO;
import com.didiglobal.turbo.engine.exception.TurboException;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.model.InstanceData;

import java.io.Serializable;
import java.util.Map;

public class ExtendRuntimeContext implements Serializable {
    /**
     * 分支执行数据
     */
    private Map<String, InstanceData> branchExecuteDataMap;
    /**
     * 分支挂起节点
     */
    private NodeInstanceBO branchSuspendNodeInstance;
    /**
     * 分支挂起节点
     */
    private FlowElement currentNodeModel;

    /**
     * 分支抛出的异常
     */
    private TurboException exception;

    public Map<String, InstanceData> getBranchExecuteDataMap() {
        return branchExecuteDataMap;
    }

    public void setBranchExecuteDataMap(Map<String, InstanceData> branchExecuteDataMap) {
        this.branchExecuteDataMap = branchExecuteDataMap;
    }

    public NodeInstanceBO getBranchSuspendNodeInstance() {
        return branchSuspendNodeInstance;
    }

    public void setBranchSuspendNodeInstance(NodeInstanceBO branchSuspendNodeInstance) {
        this.branchSuspendNodeInstance = branchSuspendNodeInstance;
    }

    public FlowElement getCurrentNodeModel() {
        return currentNodeModel;
    }

    public void setCurrentNodeModel(FlowElement currentNodeModel) {
        this.currentNodeModel = currentNodeModel;
    }

    public TurboException getException() {
        return exception;
    }

    public void setException(TurboException exception) {
        this.exception = exception;
    }
}
