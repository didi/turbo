package com.xiaoju.uemc.turbo.engine.engine;


import com.xiaoju.uemc.turbo.engine.bo.NodeInstance;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.*;
import com.xiaoju.uemc.turbo.engine.result.*;

/**
 * The entrance of Turbo
 * <p>
 * It mainly provides abilities to:
 * 1.Describe and deploy a process called flow;
 * 2.Process and drive a deployed flow.
 * <p>
 * Created by Stefanie on 2019/11/22.
 */
public interface ProcessEngine {

    /**
     * Create a flow({@link com.xiaoju.uemc.turbo.engine.entity.FlowDefinitionPO}) with flowKey and descriptive info.
     * Attention: The {@link com.xiaoju.uemc.turbo.engine.model.FlowModel} of the flow is empty.
     *
     * @param createFlowParam flowKey: business key for the flow
     *                        flowName/operator/remark: describe the flow
     * @return {@link CreateFlowParam} mainly includes flowModuleId to indicate an unique flow.
     */
    CreateFlowResult createFlow(CreateFlowParam createFlowParam);

    /**
     * Update a flow by flowModuleId. Set/update flowModel or update descriptive info.
     *
     * @param updateFlowParam flowModuleId: specify the flow to update
     *                        flowKey/flowName/flowModel/remark: content to update
     */
    UpdateFlowResult updateFlow(UpdateFlowParam updateFlowParam);

    /**
     * Deploy a flow by flowModuleId.
     * <p>
     * Create a {@link com.xiaoju.uemc.turbo.engine.entity.FlowDeploymentPO} every time.
     * A flow can be started to process only after deployed.
     *
     * @param deployFlowParam flowModuleId: specify the flow to deploy
     * @return {@link DeployFlowResult} mainly contains flowDeployId to indicate an unique record of the deployment.
     */
    DeployFlowResult deployFlow(DeployFlowParam deployFlowParam);

    /**
     * Get flow info includes flowModel content, status and descriptive info.
     * <p>
     * It'll query by flowDeployId while the flowDeployId is not blank. Otherwise, it'll query by flowModuleId.
     *
     * @param getFlowModuleParam flowModuleId specify the flow and get info from {@link com.xiaoju.uemc.turbo.engine.entity.FlowDefinitionPO}
     *                           flowDeployId specify the flow and get info from {@link com.xiaoju.uemc.turbo.engine.entity.FlowDeploymentPO}
     */
    FlowModuleResult getFlowModule(GetFlowModuleParam getFlowModuleParam);

    /**
     * Start process
     * <p>
     * 1.Create a flow instance({@link com.xiaoju.uemc.turbo.engine.entity.FlowInstancePO}) according to the specified
     * flow for the execution every time;
     * 2.Process the flow instance from the unique {@link com.xiaoju.uemc.turbo.engine.model.StartEvent} node
     * until it reaches an {@link com.xiaoju.uemc.turbo.engine.model.UserTask} node or
     * an {@link com.xiaoju.uemc.turbo.engine.model.EndEvent} node.
     *
     * @param startProcessParam flowDeployId / flowModuleId: specify the flow to process
     *                          variables: input data to drive the process if required
     * @return {@link StartProcessResult} mainly contains flowInstanceId and activeTaskInstance({@link NodeInstance})
     * to describe the userTask to be committed or the EndEvent node instance.
     */
    StartProcessResult startProcess(StartProcessParam startProcessParam);

    /**
     * Commit suspended userTask of the flow instance previously created specified by flowInstanceId and continue to process.
     *
     * @param commitTaskParam flowInstanceId: specify the flowInstance of the task
     *                        nodeInstanceId: specify the task to commit
     *                        variables: input data to drive the process if required
     * @return {@link CommitTaskResult} similar to {@link #startProcess(StartProcessParam)}
     */
    CommitTaskResult commitTask(CommitTaskParam commitTaskParam);

    /**
     * Rollback task
     * <p>
     * According to the historical node instance list, it'll rollback the suspended userTask of the flow instance
     * specified by flowInstanceId forward until it reaches an UserTask node or an StartEvent node.
     *
     * @param rollbackTaskParam flowInstanceId / nodeInstanceId similar to {@link #commitTask(CommitTaskParam)}
     * @return {@link RollbackTaskResult} similar to {@link #commitTask(CommitTaskParam)}
     */
    RollbackTaskResult rollbackTask(RollbackTaskParam rollbackTaskParam);

    /**
     * Terminate process
     * <p>
     * If the specified flow instance has been completed, ignore. Otherwise, set status to terminated of the flow instance.
     *
     * @param flowInstanceId
     * @return {@link TerminateResult} similar to {@link #commitTask(CommitTaskParam)} without activeTaskInstance.
     */
    TerminateResult terminateProcess(String flowInstanceId);

    /**
     * Get historical UserTask list
     * <p>
     * Get the list of processed UserTask of the specified flow instance order by processed time desc.
     * Attention: it'll include active userTask(s) and completed userTask(s) in the list without disabled userTask(s).
     *
     * @param flowInstanceId
     */
    NodeInstanceListResult getHistoryUserTaskList(String flowInstanceId);

    /**
     * Get processed element instance list for the specified flow instance, and mainly used to show the view of the snapshot.
     *
     * @param flowInstanceId flowInstance ID
     * @return {@link ElementInstanceListResult} the list of nodes executed in history
     */
    ElementInstanceListResult getHistoryElementList(String flowInstanceId);

    /**
     * Get latest {@link InstanceData} list of the specified flow instance.
     *
     * @param flowInstanceId
     */
    InstanceDataListResult getInstanceData(String flowInstanceId);

    /**
     * According to the flow instance and node instance given in, get node instance info.
     *
     * @param flowInstanceId
     * @param nodeInstanceId
     */
    NodeInstanceResult getNodeInstance(String flowInstanceId, String nodeInstanceId);

}

