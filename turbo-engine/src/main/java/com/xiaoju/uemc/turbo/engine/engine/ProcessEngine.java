package com.xiaoju.uemc.turbo.engine.engine;


import com.xiaoju.uemc.turbo.engine.dto.*;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Stefanie on 2019/11/22.
 */
public interface ProcessEngine {

    CreateFlowDTO createFlow(CreateFlowParam createFlowParam);

    UpdateFlowDTO updateFlow(UpdateFlowParam updateFlowParam);

    DeployFlowDTO deployFlow(DeployFlowParam deployFlowParam);

    FlowModuleDTO getFlowModule(String flowModuleId, String flowDeployId);

    StartProcessDTO startProcess(StartProcessParam startProcessParam);

    CommitTaskDTO commitTask(CommitTaskParam commitTaskParam);

    RecallTaskDTO recallTask(RecallTaskParam recallTaskParam);

    TerminateDTO terminateProcess(String flowInstanceId);

    NodeInstanceListDTO getHistoryUserTaskList(String flowInstanceId) throws Exception;

    ElementInstanceListDTO getHistoryElementList(String flowInstanceId) throws Exception;

    List<InstanceData> getInstanceData(String flowInstanceId);

    NodeInstanceDTO getNodeInstance(String flowInstanceId, String nodeInstanceId) throws Exception;

}

