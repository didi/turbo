package com.didiglobal.turbo.engine.common;

import com.didiglobal.turbo.engine.bo.NodeInstanceBO;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.result.RuntimeResult;
import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class RuntimeContext implements Serializable {

    //0.parent info
    private RuntimeContext parentRuntimeContext;

    //1.flow info
    private String flowDeployId;
    private String flowModuleId;
    private String tenant;
    private String caller;
    private Map<String, FlowElement> flowElementMap;

    //2.runtime info
    //2.1 flowInstance info
    private String flowInstanceId;
    private int flowInstanceStatus;
    private NodeInstanceBO suspendNodeInstance; //point to the userTaskInstance to commit/rollback
    private List<NodeInstanceBO> nodeInstanceList = new ArrayList<>();  //processed nodeInstance list
    private Stack<String> suspendNodeInstanceStack; // suspendNodeInstance Stack: commitNode > ... > currentNode

    //2.2 current info
    private FlowElement currentNodeModel;
    private NodeInstanceBO currentNodeInstance;

    //2.3 data info
    private String instanceDataId;
    private Map<String, InstanceData> instanceDataMap;

    //2.4 process status
    private int processStatus;

    //2.5 transparent transmission field
    private String callActivityFlowModuleId; // from top to bottom transmit callActivityFlowModuleId
    private List<RuntimeResult> callActivityRuntimeResultList; // from bottom to top transmit callActivityRuntimeResultList

    // 3.other
    private Map<String, Object> extendProperties = new HashMap<>(16);
    private List<ExtendRuntimeContext> extendRuntimeContextList = new ArrayList<>();

    public RuntimeContext getParentRuntimeContext() {
        return parentRuntimeContext;
    }

    public void setParentRuntimeContext(RuntimeContext parentRuntimeContext) {
        this.parentRuntimeContext = parentRuntimeContext;
    }

    public String getFlowDeployId() {
        return flowDeployId;
    }

    public void setFlowDeployId(String flowDeployId) {
        this.flowDeployId = flowDeployId;
    }

    public String getFlowModuleId() {
        return flowModuleId;
    }

    public void setFlowModuleId(String flowModuleId) {
        this.flowModuleId = flowModuleId;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public Map<String, FlowElement> getFlowElementMap() {
        return flowElementMap;
    }

    public void setFlowElementMap(Map<String, FlowElement> flowElementMap) {
        this.flowElementMap = flowElementMap;
    }

    public String getFlowInstanceId() {
        return flowInstanceId;
    }

    public void setFlowInstanceId(String flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }

    public int getFlowInstanceStatus() {
        return flowInstanceStatus;
    }

    public void setFlowInstanceStatus(int flowInstanceStatus) {
        this.flowInstanceStatus = flowInstanceStatus;
    }

    public NodeInstanceBO getSuspendNodeInstance() {
        return suspendNodeInstance;
    }

    public void setSuspendNodeInstance(NodeInstanceBO suspendNodeInstance) {
        this.suspendNodeInstance = suspendNodeInstance;
    }

    public List<NodeInstanceBO> getNodeInstanceList() {
        return nodeInstanceList;
    }

    public void setNodeInstanceList(List<NodeInstanceBO> nodeInstanceList) {
        this.nodeInstanceList = nodeInstanceList;
    }

    public Stack<String> getSuspendNodeInstanceStack() {
        return suspendNodeInstanceStack;
    }

    public void setSuspendNodeInstanceStack(Stack<String> suspendNodeInstanceStack) {
        this.suspendNodeInstanceStack = suspendNodeInstanceStack;
    }

    public FlowElement getCurrentNodeModel() {
        return currentNodeModel;
    }

    public void setCurrentNodeModel(FlowElement currentNodeModel) {
        this.currentNodeModel = currentNodeModel;
    }

    public NodeInstanceBO getCurrentNodeInstance() {
        return currentNodeInstance;
    }

    public void setCurrentNodeInstance(NodeInstanceBO currentNodeInstance) {
        this.currentNodeInstance = currentNodeInstance;
    }

    public String getInstanceDataId() {
        return instanceDataId;
    }

    public void setInstanceDataId(String instanceDataId) {
        this.instanceDataId = instanceDataId;
    }

    public Map<String, InstanceData> getInstanceDataMap() {
        return instanceDataMap;
    }

    public void setInstanceDataMap(Map<String, InstanceData> instanceDataMap) {
        this.instanceDataMap = instanceDataMap;
    }

    public int getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(int processStatus) {
        this.processStatus = processStatus;
    }

    public String getCallActivityFlowModuleId() {
        return callActivityFlowModuleId;
    }

    public void setCallActivityFlowModuleId(String callActivityFlowModuleId) {
        this.callActivityFlowModuleId = callActivityFlowModuleId;
    }

    public List<RuntimeResult> getCallActivityRuntimeResultList() {
        return callActivityRuntimeResultList;
    }

    public void setCallActivityRuntimeResultList(List<RuntimeResult> callActivityRuntimeResultList) {
        this.callActivityRuntimeResultList = callActivityRuntimeResultList;
    }

    public Map<String, Object> getExtendProperties() {
        return extendProperties;
    }

    public void setExtendProperties(Map<String, Object> extendProperties) {
        this.extendProperties = extendProperties;
    }

    public List<ExtendRuntimeContext> getExtendRuntimeContextList() {
        return extendRuntimeContextList;
    }

    public void setExtendRuntimeContextList(List<ExtendRuntimeContext> extendRuntimeContextList) {
        this.extendRuntimeContextList = extendRuntimeContextList;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("flowDeployId", flowDeployId)
            .add("flowModuleId", flowModuleId)
            .add("tenant", tenant)
            .add("caller", caller)
            .add("flowElementMap", flowElementMap)
            .add("flowInstanceId", flowInstanceId)
                .add("flowInstanceStatus", flowInstanceStatus)
                .add("suspendNodeInstance", suspendNodeInstance)
                .add("nodeInstanceList", nodeInstanceList)
                .add("currentNodeModel", currentNodeModel)
                .add("currentNodeInstance", currentNodeInstance)
                .add("instanceDataId", instanceDataId)
                .add("instanceDataMap", instanceDataMap)
                .add("processStatus", processStatus)
                .toString();
    }
}
