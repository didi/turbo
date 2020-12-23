package com.didiglobal.turbo.engine.engine.impl;

import com.didiglobal.turbo.engine.result.*;
import com.didiglobal.turbo.engine.engine.ProcessEngine;
import com.didiglobal.turbo.engine.param.*;
import com.didiglobal.turbo.engine.processor.DefinitionProcessor;
import com.didiglobal.turbo.engine.processor.RuntimeProcessor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ProcessEngineImpl implements ProcessEngine {

    @Resource
    private DefinitionProcessor definitionProcessor;

    @Resource
    private RuntimeProcessor runtimeProcessor;

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
        return runtimeProcessor.commit(commitTaskParam);
    }

    @Override
    public RollbackTaskResult rollbackTask(RollbackTaskParam rollbackTaskParam) {
        return runtimeProcessor.rollback(rollbackTaskParam);
    }

    @Override
    public TerminateResult terminateProcess(String flowInstanceId) {
        return runtimeProcessor.terminateProcess(flowInstanceId);
    }

    @Override
    public NodeInstanceListResult getHistoryUserTaskList(String flowInstanceId) {
        return runtimeProcessor.getHistoryUserTaskList(flowInstanceId);
    }

    @Override
    public ElementInstanceListResult getHistoryElementList(String flowInstanceId) {
        return runtimeProcessor.getHistoryElementList(flowInstanceId);
    }

    @Override
    public InstanceDataListResult getInstanceData(String flowInstanceId) {
        return runtimeProcessor.getInstanceData(flowInstanceId);
    }

    @Override
    public NodeInstanceResult getNodeInstance(String flowInstanceId, String nodeInstanceId) {
        return runtimeProcessor.getNodeInstance(flowInstanceId, nodeInstanceId);
    }
}
