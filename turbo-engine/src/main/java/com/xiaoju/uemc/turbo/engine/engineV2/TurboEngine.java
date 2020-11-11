package com.xiaoju.uemc.turbo.engine.engineV2;

import com.xiaoju.uemc.turbo.engine.dto.*;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.*;
import com.xiaoju.uemc.turbo.engine.processor.DefinitionProcessor;
import com.xiaoju.uemc.turbo.engine.processor.RuntimeProcessor;
import com.xiaoju.uemc.turbo.engine.validator.ParamValidator;

import java.util.List;
import java.util.Map;

/**
 * Created by Stefanie on 2020/11/11.
 */
public class TurboEngine implements ITurboEngine {

    protected DefinitionProcessor definitionProcessor;

    protected RuntimeProcessor runtimeProcessor;

    @Override
    public CreateFlowDTO createFlow(CreateFlowParam createFlowParam) throws Exception {
        ParamValidator.validate(createFlowParam);
        return definitionProcessor.create(createFlowParam);
    }

    @Override
    public boolean updateFlow(UpdateFlowParam updateFlowParam) throws Exception {
        ParamValidator.validate(updateFlowParam);
        return definitionProcessor.update(updateFlowParam);
    }

    @Override
    public DeployFlowDTO deployFlow(DeployFlowParam deployFlowParam) throws Exception {
        ParamValidator.validate(deployFlowParam);
        return definitionProcessor.deploy(deployFlowParam);
    }

    @Override
    public FlowModuleDTO getFlowModule(String flowModuleId) throws Exception {
        return getFlowModule(flowModuleId, null);
    }

    @Override
    public FlowModuleDTO getFlowModule(String flowModuleId, String flowDeployId) throws Exception {
        ParamValidator.validate(flowModuleId, flowDeployId);
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
