package com.xiaoju.uemc.turbo.engine.engine.impl;

import com.xiaoju.uemc.turbo.engine.dto.*;
import com.xiaoju.uemc.turbo.engine.engine.ProcessEngine;
import com.xiaoju.uemc.turbo.engine.param.*;
import com.xiaoju.uemc.turbo.engine.processor.DefinitionProcessor;
import com.xiaoju.uemc.turbo.engine.processor.RuntimeProcessor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Stefanie on 2019/11/22.
 */
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
    public FlowModuleResult getFlowModule(String flowModuleId, String flowDeployId) {
        return definitionProcessor.getFlowModule(flowModuleId, flowDeployId);
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
    public RecallTaskResult recallTask(RecallTaskParam recallTaskParam) {
        return runtimeProcessor.recall(recallTaskParam);
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
