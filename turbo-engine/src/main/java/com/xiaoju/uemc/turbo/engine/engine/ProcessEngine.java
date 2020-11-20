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

    /**
     * start process
     * @param startProcessParam
     * @return
     * @throws Exception
     */
    StartProcessDTO startProcess(StartProcessParam startProcessParam) throws Exception;

    /**
     * commit task
     * @param commitTaskParam
     * @return
     * @throws Exception
     */
    CommitTaskDTO commitTask(CommitTaskParam commitTaskParam) throws Exception;

    /**
     * rollback task
     * @param recallTaskParam
     * @return
     * @throws Exception
     */
    RecallTaskDTO recallTask(RecallTaskParam recallTaskParam) throws Exception;

    /**
     * terminate process
     * @param flowInstanceId
     * @return
     * @throws Exception
     */
    TerminateDTO terminateProcess(String flowInstanceId) throws Exception;

    /**
     * get history user task list desc
     * @param flowInstanceId
     * @return
     * @throws Exception
     */
    NodeInstanceListDTO getHistoryUserTaskList(String flowInstanceId) throws Exception;

    /**
     * get history element list asc
     * @param flowInstanceId
     * @return
     * @throws Exception
     */
    ElementInstanceListDTO getHistoryElementList(String flowInstanceId) throws Exception;

    /**
     * get instance data
     * @param flowInstanceId
     * @return
     * @throws Exception
     */
    List<InstanceData> getInstanceData(String flowInstanceId) throws Exception;

    /**
     * update data
     * @param flowInstanceId
     * @param dataMap
     */
    void updateData(String flowInstanceId, Map<String, Object> dataMap);

    /**
     * get node instacne
     * @param flowInstanceId
     * @param nodeInstanceId
     * @return
     * @throws Exception
     */
    NodeInstanceDTO getNodeInstance(String flowInstanceId, String nodeInstanceId) throws Exception;

}

