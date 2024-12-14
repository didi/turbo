package com.didiglobal.turbo.engine.engine.impl;

import com.didiglobal.turbo.engine.core.TurboContext;
import com.didiglobal.turbo.engine.engine.ProcessEngine;
import com.didiglobal.turbo.engine.param.CommitTaskParam;
import com.didiglobal.turbo.engine.param.CreateFlowParam;
import com.didiglobal.turbo.engine.param.DeployFlowParam;
import com.didiglobal.turbo.engine.param.GetFlowModuleParam;
import com.didiglobal.turbo.engine.param.RollbackTaskParam;
import com.didiglobal.turbo.engine.param.StartProcessParam;
import com.didiglobal.turbo.engine.param.UpdateFlowParam;
import com.didiglobal.turbo.engine.processor.DefinitionProcessor;
import com.didiglobal.turbo.engine.processor.RuntimeProcessor;
import com.didiglobal.turbo.engine.result.CommitTaskResult;
import com.didiglobal.turbo.engine.result.CreateFlowResult;
import com.didiglobal.turbo.engine.result.DeployFlowResult;
import com.didiglobal.turbo.engine.result.ElementInstanceListResult;
import com.didiglobal.turbo.engine.result.FlowInstanceResult;
import com.didiglobal.turbo.engine.result.FlowModuleResult;
import com.didiglobal.turbo.engine.result.InstanceDataListResult;
import com.didiglobal.turbo.engine.result.NodeInstanceListResult;
import com.didiglobal.turbo.engine.result.NodeInstanceResult;
import com.didiglobal.turbo.engine.result.RollbackTaskResult;
import com.didiglobal.turbo.engine.result.StartProcessResult;
import com.didiglobal.turbo.engine.result.TerminateResult;
import com.didiglobal.turbo.engine.result.UpdateFlowResult;

public class ProcessEngineImpl implements ProcessEngine {

    private DefinitionProcessor definitionProcessor;

    private RuntimeProcessor runtimeProcessor;

    @Override
    public ProcessEngine configure(TurboContext turboContext) {
        this.definitionProcessor = turboContext.getDefinitionProcessor();
        this.runtimeProcessor = turboContext.getRuntimeProcessor();
        return this;
    }

    @Override
    public CreateFlowResult createFlow(CreateFlowParam createFlowParam) {
        return definitionProcessor.create(createFlowParam);
    }

    @Override
    public UpdateFlowResult updateFlow(UpdateFlowParam updateFlowParam) {
        return definitionProcessor.update(updateFlowParam);
    }

    @Override
    public DeployFlowResult deployFlow(DeployFlowParam deployFlowParam) {
        return definitionProcessor.deploy(deployFlowParam);
    }

    @Override
    public FlowModuleResult getFlowModule(GetFlowModuleParam getFlowModuleParam) {
        return definitionProcessor.getFlowModule(getFlowModuleParam);
    }

    @Override
    public StartProcessResult startProcess(StartProcessParam startProcessParam) {
        return runtimeProcessor.startProcess(startProcessParam);
    }

    @Override
    public CommitTaskResult commitTask(CommitTaskParam commitTaskParam) {
        runtimeProcessor.checkIsSubFlowInstance(commitTaskParam.getFlowInstanceId());
        return runtimeProcessor.commit(commitTaskParam);
    }

    @Override
    public RollbackTaskResult rollbackTask(RollbackTaskParam rollbackTaskParam) {
        runtimeProcessor.checkIsSubFlowInstance(rollbackTaskParam.getFlowInstanceId());
        return runtimeProcessor.rollback(rollbackTaskParam);
    }

    @Override
    public TerminateResult terminateProcess(String flowInstanceId) {
        runtimeProcessor.checkIsSubFlowInstance(flowInstanceId);
        return runtimeProcessor.terminateProcess(flowInstanceId, true);
    }

    @Override
    public TerminateResult terminateProcess(String flowInstanceId, boolean effectiveForSubFlowInstance) {
        runtimeProcessor.checkIsSubFlowInstance(flowInstanceId);
        return runtimeProcessor.terminateProcess(flowInstanceId, effectiveForSubFlowInstance);
    }

    @Override
    public NodeInstanceListResult getHistoryUserTaskList(String flowInstanceId) {
        return runtimeProcessor.getHistoryUserTaskList(flowInstanceId, true);
    }

    @Override
    public NodeInstanceListResult getHistoryUserTaskList(String flowInstanceId, boolean effectiveForSubFlowInstance) {
        return runtimeProcessor.getHistoryUserTaskList(flowInstanceId, effectiveForSubFlowInstance);
    }

    @Override
    public ElementInstanceListResult getHistoryElementList(String flowInstanceId) {
        return runtimeProcessor.getHistoryElementList(flowInstanceId, true);
    }

    @Override
    public ElementInstanceListResult getHistoryElementList(String flowInstanceId, boolean effectiveForSubFlowInstance) {
        return runtimeProcessor.getHistoryElementList(flowInstanceId, effectiveForSubFlowInstance);
    }

    @Override
    public InstanceDataListResult getInstanceData(String flowInstanceId) {
        return runtimeProcessor.getInstanceData(flowInstanceId, true);
    }

    @Override
    public InstanceDataListResult getInstanceData(String flowInstanceId, boolean effectiveForSubFlowInstance) {
        return runtimeProcessor.getInstanceData(flowInstanceId, effectiveForSubFlowInstance);
    }

    @Override
    public NodeInstanceResult getNodeInstance(String flowInstanceId, String nodeInstanceId) {
        return runtimeProcessor.getNodeInstance(flowInstanceId, nodeInstanceId, true);
    }

    @Override
    public NodeInstanceResult getNodeInstance(String flowInstanceId, String nodeInstanceId, boolean effectiveForSubFlowInstance) {
        return runtimeProcessor.getNodeInstance(flowInstanceId, nodeInstanceId, effectiveForSubFlowInstance);
    }

    @Override
    public FlowInstanceResult getFlowInstance(String flowInstanceId) {
        return runtimeProcessor.getFlowInstance(flowInstanceId);
    }

    @Override
    public InstanceDataListResult getInstanceData(String flowInstanceId, String instanceDataId) {
        return runtimeProcessor.getInstanceData(flowInstanceId, instanceDataId, true);
    }

    @Override
    public InstanceDataListResult getInstanceData(String flowInstanceId, String instanceDataId, boolean effectiveForSubFlowInstance) {
        return runtimeProcessor.getInstanceData(flowInstanceId, instanceDataId, effectiveForSubFlowInstance);
    }
}
