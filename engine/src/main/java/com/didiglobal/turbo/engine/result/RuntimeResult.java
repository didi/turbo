package com.didiglobal.turbo.engine.result;

import com.didiglobal.turbo.engine.bo.NodeInstance;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RuntimeResult extends CommonResult {
    private String flowInstanceId;
    private int status;
    private List<NodeExecuteResult> nodeExecuteResults;

    private Map<String, Object> extendProperties;

    public RuntimeResult() {
        super();
    }

    public RuntimeResult(ErrorEnum errorEnum) {
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

    // 兼容旧版本
    public NodeInstance getActiveTaskInstance() {
        if (nodeExecuteResults == null || nodeExecuteResults.isEmpty()) {
            return null;
        }
        return nodeExecuteResults.get(0).activeTaskInstance;
    }

    // 兼容旧版本
    public void setActiveTaskInstance(NodeInstance activeTaskInstance) {
        if (nodeExecuteResults == null) {
            this.nodeExecuteResults = new ArrayList<>();
        }
        if (nodeExecuteResults.isEmpty()) {
            this.nodeExecuteResults.add(new NodeExecuteResult());
        }
        this.nodeExecuteResults.get(0).activeTaskInstance = activeTaskInstance;
    }

    // 兼容旧版本
    public List<InstanceData> getVariables() {
        if (nodeExecuteResults == null || nodeExecuteResults.isEmpty()) {
            return null;
        }
        return nodeExecuteResults.get(0).variables;
    }

    // 兼容旧版本
    public void setVariables(List<InstanceData> variables) {
        if (nodeExecuteResults == null) {
            this.nodeExecuteResults = new ArrayList<>();
        }
        if (nodeExecuteResults.isEmpty()) {
            this.nodeExecuteResults.add(new NodeExecuteResult());
        }
        this.nodeExecuteResults.get(0).variables = variables;
    }

    public List<NodeExecuteResult> getNodeExecuteResults() {
        return nodeExecuteResults;
    }

    public void setNodeExecuteResults(List<NodeExecuteResult> nodeExecuteResults) {
        this.nodeExecuteResults = nodeExecuteResults;
    }

    public Map<String, Object> getExtendProperties() {
        return extendProperties;
    }

    public void setExtendProperties(Map<String, Object> extendProperties) {
        this.extendProperties = extendProperties;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("flowInstanceId", flowInstanceId)
            .add("status", status)
            .toString();
    }

    public static class NodeExecuteResult extends CommonResult{
        private NodeInstance activeTaskInstance;
        private List<InstanceData> variables;

        public NodeInstance getActiveTaskInstance() {
            return activeTaskInstance;
        }

        public void setActiveTaskInstance(NodeInstance activeTaskInstance) {
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
                .add("activeTaskInstance", activeTaskInstance)
                .add("variables", variables)
                .toString();
        }
    }
}
