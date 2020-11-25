package com.xiaoju.uemc.turbo.engine.engine.impl;

import com.xiaoju.uemc.turbo.engine.dto.*;
import com.xiaoju.uemc.turbo.engine.engine.ProcessEngine;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.*;
import com.xiaoju.uemc.turbo.engine.processor.DefinitionProcessor;
import com.xiaoju.uemc.turbo.engine.processor.RuntimeProcessor;
import com.xiaoju.uemc.turbo.engine.validator.ParamValidator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    public CreateFlowDTO createFlow(CreateFlowParam createFlowParam) {
        return definitionProcessor.create(createFlowParam);
    }

    @Override
    public UpdateFlowDTO updateFlow(UpdateFlowParam updateFlowParam) {
        return definitionProcessor.update(updateFlowParam);
    }

    @Override
    public DeployFlowDTO deployFlow(DeployFlowParam deployFlowParam) {
        return definitionProcessor.deploy(deployFlowParam);
    }

    @Override
    public FlowModuleDTO getFlowModule(String flowModuleId, String flowDeployId) {
        return definitionProcessor.getFlowModule(flowModuleId, flowDeployId);
    }

    @Override
    public StartProcessDTO startProcess(StartProcessParam startProcessParam) throws Exception {
        return runtimeProcessor.startProcess(startProcessParam);
    }

    @Override
    public CommitTaskDTO commitTask(CommitTaskParam commitTaskParam) throws Exception {
        return runtimeProcessor.commit(commitTaskParam);
    }

    @Override
    public RecallTaskDTO recallTask(RecallTaskParam recallTaskParam) throws Exception {
        return runtimeProcessor.recall(recallTaskParam);
    }

    @Override
    public TerminateDTO terminateProcess(String flowInstanceId) throws Exception {
        return runtimeProcessor.terminateProcess(flowInstanceId);
    }

    @Override
    public NodeInstanceListDTO getHistoryUserTaskList(String flowInstanceId) throws Exception {
        return runtimeProcessor.getHistoryUserTaskList(flowInstanceId);
    }

    @Override
    public ElementInstanceListDTO getHistoryElementList(String flowInstanceId) throws Exception {
        return runtimeProcessor.getHistoryElementList(flowInstanceId);
    }

    @Override
    public List<InstanceData> getInstanceData(String flowInstanceId) throws Exception {
        return runtimeProcessor.getInstanceData(flowInstanceId);
    }

    @Override
    public void updateData(String flowInstanceId, Map<String, Object> dataMap) {
        // TODO: 2019/12/19
    }

    @Override
    public NodeInstanceDTO getNodeInstance(String flowInstanceId, String nodeInstanceId) throws Exception {
        return runtimeProcessor.getNodeInstance(flowInstanceId, nodeInstanceId);
    }
}
