package com.xiaoju.uemc.turbo.engine.engine;


import com.xiaoju.uemc.turbo.engine.dto.*;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.*;

import java.util.List;
import java.util.Map;

/**
 * The ProcessEngine mainly provides the ability of
 * "defining process, and executing process according to process definition",
 * and uses appearance design pattern, external exposure, creating process,
 * executing process and so on.
 *
 * Created by Stefanie on 2019/11/22.
 */
public interface ProcessEngine {

    CreateFlowDTO createFlow(CreateFlowParam createFlowParam) throws Exception;

    boolean updateFlow(UpdateFlowParam updateFlowParam) throws Exception;

    DeployFlowDTO deployFlow(DeployFlowParam deployFlowParam) throws Exception;

    FlowModuleDTO getFlowModule(String flowModuleId) throws Exception;

    FlowModuleDTO getFlowModule(String flowModuleId, String flowDeployId) throws Exception;

    /**
     * Entry point for creating process instances
     *
     * According to the specified process model and the data necessary for the execution,
     * the ProcessEngine is executed from the start node, through the flow and calculation in the middle,
     * and finally it is suspended at the user node or completed at the end node.
     *
     * @param startProcessParam Set the runtime data, module ID or deployment ID
     * @return StartProcessDTO include flowInstance ID, activeInstance, instance data and so on
     * @throws Exception
     */
    StartProcessDTO startProcess(StartProcessParam startProcessParam) throws Exception;

    /**
     * Entry point for commiting user task
     *
     * According to the specified process instance, node instance and the data necessary for the execution,
     * the ProcessEngine starts from the current specified user node. After flow and calculation,
     * it will be suspended at the user node or completed at the end node.
     *
     * @param commitTaskParam Set the runtime data, flowInstance ID and nodeInstance ID
     * @return CommitTaskDTO include flowInstance ID, activeInstance, instance data and so on
     * @throws Exception
     */
    CommitTaskDTO commitTask(CommitTaskParam commitTaskParam) throws Exception;

    /**
     * Entry point for rollbacking user task
     *
     * According to the specified process instance and node instance,
     * the ProcessEngine will back off from the current specified user node,
     * according to the order of historical execution nodes, the ProcessEngine will suspend
     * at the nearest user node or throw an exception at the start node.
     *
     * @param recallTaskParam Set flowInstance ID and nodeInstance ID
     * @return RecallTaskDTO include flowInstance ID, activeInstance, instance data and so on
     * @throws Exception
     */
    RecallTaskDTO recallTask(RecallTaskParam recallTaskParam) throws Exception;

    /**
     * Force termination of process instance.
     *
     * If the current process instance has been completed, we will not do anything.
     * Otherwise, set the status of the process instance to terminated
     *
     * @param flowInstanceId flowInstance ID
     * @return TerminateDTO include flowInstance ID, activeInstance, instance data and so on
     * @throws Exception
     */
    TerminateDTO terminateProcess(String flowInstanceId) throws Exception;

    /**
     * According to the process instance, the list of valid user nodes that have been executed in history is obtained,
     * and the latest user nodes are put in the first place according to the database primary key.
     *
     * A valid user node is a completed or activated user node,
     * excluding the rolled back revoked user node
     *
     * @param flowInstanceId flowInstance ID
     * @return NodeInstanceListDTO the list of user nodes executed in history
     * @throws Exception
     */
    NodeInstanceListDTO getHistoryUserTaskList(String flowInstanceId) throws Exception;

    /**
     * According to the process instance and execution order,
     * the node list of historical execution is obtained,
     * which can be used to view the execution snapshot of a process instance
     *
     * @param flowInstanceId flowInstance ID
     * @returnElementInstanceListDTO the list of nodes executed in history
     * @throws Exception
     */
    ElementInstanceListDTO getHistoryElementList(String flowInstanceId) throws Exception;

    /**
     * According to the process instance, get the latest data information
     *
     * @param flowInstanceId flowInstance ID
     * @return List<InstanceData> recent data info
     * @throws Exception
     */
    List<InstanceData> getInstanceData(String flowInstanceId) throws Exception;

    /**
     * According to the process instance, update the latest data information
     *
     * empty implement up to new
     *
     * @param flowInstanceId flowInstance ID
     * @param dataMap data info
     */
    void updateData(String flowInstanceId, Map<String, Object> dataMap);

    /**
     * According to the process instance and node instance, get the specified node instance information
     *
     * @param flowInstanceId flowInstance ID
     * @param nodeInstanceId nodeInstance ID
     * @return node instance info
     * @throws Exception
     */
    NodeInstanceDTO getNodeInstance(String flowInstanceId, String nodeInstanceId) throws Exception;

}

