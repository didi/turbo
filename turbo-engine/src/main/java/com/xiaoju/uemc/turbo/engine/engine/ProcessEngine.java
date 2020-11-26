package com.xiaoju.uemc.turbo.engine.engine;


import com.xiaoju.uemc.turbo.engine.dto.*;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.*;

import java.util.List;
import java.util.Map;

/**
 * The entrance of Turbo
 *
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
     * Entrance to start a process
     *
     * According to the specified flow model with the necessary data for the execution,
     * the ProcessEngine is executed from the StartEvent node, finally it is suspended at
     * the UserTask node or completed at the EndEvent node.
     *
     * @param startProcessParam include the necessary data, flowModule ID or flowDeploy ID
     * @return StartProcessDTO include flowInstance ID, activeInstance, instance data and so on
     * @throws Exception
     */
    StartProcessDTO startProcess(StartProcessParam startProcessParam) throws Exception;

    /**
     * Entrance to commit UserTask node
     *
     * According to the specified flow instance, node instance with the necessary data for the execution,
     * the ProcessEngine starts from the current specified UserTask node. After flow and calculation,
     * it will be suspended at the UserTask node or completed at the EndEvent node.
     *
     * @param commitTaskParam include the necessary data, flowInstance ID and nodeInstance ID
     * @return CommitTaskDTO include flowInstance ID, activeInstance, instance data and so on
     * @throws Exception
     */
    CommitTaskDTO commitTask(CommitTaskParam commitTaskParam) throws Exception;

    /**
     * Entrance to rollback UserTask node
     *
     * According to the specified flow instance and node instance,
     * the ProcessEngine will back off from the current specified UserTask node,
     * according to the order of historical execution nodes, the ProcessEngine will suspend
     * at the nearest UserTask node or throw an exception at the StartEvent node.
     *
     * @param recallTaskParam include flowInstance ID and nodeInstance ID
     * @return RecallTaskDTO include flowInstance ID, activeInstance, instance data and so on
     * @throws Exception
     */
    RecallTaskDTO recallTask(RecallTaskParam recallTaskParam) throws Exception;

    /**
     * Force termination of process instance.
     *
     * If the current flow instance has been completed, we will not do anything.
     * Otherwise, set the status of the process instance to terminated
     *
     * @param flowInstanceId flowInstance ID
     * @return TerminateDTO include flowInstance ID, activeInstance, instance data and so on
     * @throws Exception
     */
    TerminateDTO terminateProcess(String flowInstanceId) throws Exception;

    /**
     * According to the flow instance, the list of valid UserTask nodes that have been executed
     * in history is obtained, and the latest UserTask nodes are put in the first place
     * according to the database primary key.
     *
     * A valid UserTask node is a completed or active UserTask node, excluding the disabled
     * UserTask node.
     *
     * @param flowInstanceId flowInstance ID
     * @return NodeInstanceListDTO the list of UserTask nodes executed in history
     * @throws Exception
     */
    NodeInstanceListDTO getHistoryUserTaskList(String flowInstanceId) throws Exception;

    /**
     * According to the flow instance and execution order,
     * the node list of historical execution is obtained,
     * which can be used to view the execution snapshot of a flow instance
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
     * empty implement up to now
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

