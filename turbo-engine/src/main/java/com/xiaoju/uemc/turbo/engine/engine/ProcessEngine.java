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

    CreateFlowDTO createFlow(CreateFlowParam createFlowParam) throws Exception;

    boolean updateFlow(UpdateFlowParam updateFlowParam) throws Exception;

    DeployFlowDTO deployFlow(DeployFlowParam deployFlowParam) throws Exception;

    FlowModuleDTO getFlowModule(String flowModuleId) throws Exception;

    FlowModuleDTO getFlowModule(String flowModuleId, String flowDeployId) throws Exception;

    StartProcessDTO startProcess(StartProcessParam startProcessParam) throws Exception;

    CommitTaskDTO commitTask(CommitTaskParam commitTaskParam) throws Exception;

    RecallTaskDTO recallTask(RecallTaskParam recallTaskParam) throws Exception;

    TerminateDTO terminateProcess(String flowInstanceId) throws Exception;

    NodeInstanceListDTO getHistoryUserTaskList(String flowInstanceId) throws Exception;

    ElementInstanceListDTO getHistoryElementList(String flowInstanceId) throws Exception;

    List<InstanceData> getInstanceData(String flowInstanceId) throws Exception;

    void updateData(String flowInstanceId, Map<String, Object> dataMap);

    NodeInstanceDTO getNodeInstance(String flowInstanceId, String nodeInstanceId) throws Exception;

}

