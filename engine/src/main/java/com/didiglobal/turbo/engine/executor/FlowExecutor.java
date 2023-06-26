package com.didiglobal.turbo.engine.executor;

import com.didiglobal.turbo.engine.bo.NodeInstanceBO;
import com.didiglobal.turbo.engine.common.*;
import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.engine.entity.FlowInstancePO;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.exception.ReentrantException;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.service.FlowInstanceService;
import com.didiglobal.turbo.engine.util.FlowModelUtil;
import com.didiglobal.turbo.engine.util.InstanceDataUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FlowExecutor extends RuntimeExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowExecutor.class);

    @Resource
    private ProcessInstanceDAO processInstanceDAO;

    ////////////////////////////////////////execute////////////////////////////////////////

    @Override
    public void execute(RuntimeContext runtimeContext) throws ProcessException {
        int processStatus = ProcessStatus.SUCCESS;
        try {
            preExecute(runtimeContext);
            doExecute(runtimeContext);
        } catch (ProcessException pe) {
            if (!ErrorEnum.isSuccess(pe.getErrNo())) {
                processStatus = ProcessStatus.FAILED;
            }
            throw pe;
        } finally {
            runtimeContext.setProcessStatus(processStatus);
            postExecute(runtimeContext);
        }
    }

    /**
     * Fill runtimeContext:
     * 1. Generate flowInstanceId and insert FlowInstancePO into db
     * 2. Generate instanceDataId and insert InstanceDataPO into db
     * 3. Update runtimeContext: flowInstanceId, flowInstanceStatus, instanceDataId, nodeInstanceList, suspendNodeInstance
     *
     * @throws Exception
     */
    private void preExecute(RuntimeContext runtimeContext) throws ProcessException {
        //1.save FlowInstancePO into db
        FlowInstancePO flowInstancePO = saveFlowInstance(runtimeContext);

        //2.save InstanceDataPO into db
        String instanceDataId = saveInstanceData(flowInstancePO, runtimeContext.getInstanceDataMap());

        //3.update runtimeContext
        fillExecuteContext(runtimeContext, flowInstancePO.getFlowInstanceId(), instanceDataId);
    }

    private FlowInstancePO saveFlowInstance(RuntimeContext runtimeContext) throws ProcessException {
        FlowInstancePO flowInstancePO = buildFlowInstancePO(runtimeContext);
        int result = processInstanceDAO.insert(flowInstancePO);
        if (result == 1) {
            return flowInstancePO;
        }
        LOGGER.warn("saveFlowInstancePO: insert failed.||flowInstancePO={}", flowInstancePO);
        throw new ProcessException(ErrorEnum.SAVE_FLOW_INSTANCE_FAILED);
    }

    private FlowInstancePO buildFlowInstancePO(RuntimeContext runtimeContext) {
        FlowInstancePO flowInstancePO = new FlowInstancePO();
        // copy flow info
        BeanUtils.copyProperties(runtimeContext, flowInstancePO);
        // generate flowInstanceId
        flowInstancePO.setFlowInstanceId(genId());
        RuntimeContext parentRuntimeContext = runtimeContext.getParentRuntimeContext();
        if (parentRuntimeContext != null) {
            flowInstancePO.setParentFlowInstanceId(parentRuntimeContext.getFlowInstanceId());
        }
        flowInstancePO.setStatus(FlowInstanceStatus.RUNNING);
        Date currentTime = new Date();
        flowInstancePO.setCreateTime(currentTime);
        flowInstancePO.setModifyTime(currentTime);
        return flowInstancePO;
    }

    private String saveInstanceData(FlowInstancePO flowInstancePO, Map<String, InstanceData> instanceDataMap) throws ProcessException {
        if (MapUtils.isEmpty(instanceDataMap)) {
            return StringUtils.EMPTY;
        }

        InstanceDataPO instanceDataPO = buildInstanceDataPO(flowInstancePO, instanceDataMap);
        int result = instanceDataDAO.insert(instanceDataPO);
        if (result == 1) {
            return instanceDataPO.getInstanceDataId();
        }

        LOGGER.warn("saveInstanceDataPO: insert failed.||instanceDataPO={}", instanceDataPO);
        throw new ProcessException(ErrorEnum.SAVE_INSTANCE_DATA_FAILED);
    }

    private InstanceDataPO buildInstanceDataPO(FlowInstancePO flowInstancePO, Map<String, InstanceData> instanceDataMap) {
        InstanceDataPO instanceDataPO = new InstanceDataPO();
        // copy flow info & flowInstanceId
        BeanUtils.copyProperties(flowInstancePO, instanceDataPO);

        // generate instanceDataId
        instanceDataPO.setInstanceDataId(genId());
        instanceDataPO.setInstanceData(InstanceDataUtil.getInstanceDataListStr(instanceDataMap));

        instanceDataPO.setNodeInstanceId(StringUtils.EMPTY);
        instanceDataPO.setNodeKey(StringUtils.EMPTY);
        instanceDataPO.setCreateTime(new Date());
        instanceDataPO.setType(InstanceDataType.INIT);
        return instanceDataPO;
    }

    private void fillExecuteContext(RuntimeContext runtimeContext, String flowInstanceId, String instanceDataId) throws ProcessException {
        runtimeContext.setFlowInstanceId(flowInstanceId);
        runtimeContext.setFlowInstanceStatus(FlowInstanceStatus.RUNNING);

        runtimeContext.setInstanceDataId(instanceDataId);

        runtimeContext.setNodeInstanceList(Lists.newArrayList());

        //set startEvent into suspendNodeInstance as the first node to process
        Map<String, FlowElement> flowElementMap = runtimeContext.getFlowElementMap();
        FlowElement startEvent = FlowModelUtil.getStartEvent(flowElementMap);
        if (startEvent == null) {
            LOGGER.warn("fillExecuteContext failed: cannot get startEvent.||flowInstance={}||flowDeployId={}",
                    runtimeContext.getFlowInstanceId(), runtimeContext.getFlowDeployId());
            throw new ProcessException(ErrorEnum.GET_NODE_FAILED);
        }
        NodeInstanceBO suspendNodeInstance = new NodeInstanceBO();
        suspendNodeInstance.setNodeKey(startEvent.getKey());
        suspendNodeInstance.setStatus(NodeInstanceStatus.ACTIVE);
        suspendNodeInstance.setSourceNodeInstanceId(StringUtils.EMPTY);
        suspendNodeInstance.setSourceNodeKey(StringUtils.EMPTY);
        runtimeContext.setSuspendNodeInstance(suspendNodeInstance);

        runtimeContext.setCurrentNodeModel(startEvent);
    }

    private void doExecute(RuntimeContext runtimeContext) throws ProcessException {
        RuntimeExecutor runtimeExecutor = getExecuteExecutor(runtimeContext);
        while (runtimeExecutor != null) {
            runtimeExecutor.execute(runtimeContext);
            runtimeExecutor = runtimeExecutor.getExecuteExecutor(runtimeContext);
        }
    }

    private void postExecute(RuntimeContext runtimeContext) throws ProcessException {

        //1.update context with processStatus
        if (runtimeContext.getProcessStatus() == ProcessStatus.SUCCESS) {
            //SUCCESS: update runtimeContext: update suspendNodeInstance
            if (runtimeContext.getCurrentNodeInstance() != null) {
                runtimeContext.setSuspendNodeInstance(runtimeContext.getCurrentNodeInstance());
            }
        }

        //2.save nodeInstanceList to db
        saveNodeInstanceList(runtimeContext, NodeInstanceType.EXECUTE);

        //3.update flowInstance status while completed
        if (isCompleted(runtimeContext)) {
            if (isSubFlowInstance(runtimeContext)) {
                processInstanceDAO.updateStatus(runtimeContext.getFlowInstanceId(), FlowInstanceStatus.END);
                runtimeContext.setFlowInstanceStatus(FlowInstanceStatus.END);
            } else {
                processInstanceDAO.updateStatus(runtimeContext.getFlowInstanceId(), FlowInstanceStatus.COMPLETED);
                runtimeContext.setFlowInstanceStatus(FlowInstanceStatus.COMPLETED);
            }
            LOGGER.info("postExecute: flowInstance process completely.||flowInstanceId={}", runtimeContext.getFlowInstanceId());
        }
    }


    ////////////////////////////////////////commit////////////////////////////////////////

    @Override
    public void commit(RuntimeContext runtimeContext) throws ProcessException {
        int processStatus = ProcessStatus.SUCCESS;
        try {
            preCommit(runtimeContext);
            doCommit(runtimeContext);
        } catch (ReentrantException re) {
            //ignore
        } catch (ProcessException pe) {
            if (!ErrorEnum.isSuccess(pe.getErrNo())) {
                processStatus = ProcessStatus.FAILED;
            }
            throw pe;
        } finally {
            runtimeContext.setProcessStatus(processStatus);
            postCommit(runtimeContext);
        }
    }

    /**
     * Fill runtimeContext:
     * 1. Get instanceData from db firstly
     * 2. merge and save instanceData while commitData is not empty
     * 3. Update runtimeContext: instanceDataId, instanceDataMap, nodeInstanceList, suspendNodeInstance
     *
     * @throws Exception
     */
    private void preCommit(RuntimeContext runtimeContext) throws ProcessException {
        String flowInstanceId = runtimeContext.getFlowInstanceId();
        NodeInstanceBO suspendNodeInstance = runtimeContext.getSuspendNodeInstance();
        String nodeInstanceId = suspendNodeInstance.getNodeInstanceId();

        //1.get instanceData from db
        NodeInstancePO nodeInstancePO = nodeInstanceDAO.selectByNodeInstanceId(flowInstanceId, nodeInstanceId);
        if (nodeInstancePO == null) {
            LOGGER.warn("preCommit failed: cannot find nodeInstancePO from db.||flowInstanceId={}||nodeInstanceId={}",
                    flowInstanceId, nodeInstanceId);
            throw new ProcessException(ErrorEnum.GET_NODE_INSTANCE_FAILED);
        }

        //unexpected: flowInstance is completed
        if (isCompleted(runtimeContext)) {
            LOGGER.warn("preExecute warning: reentrant process. FlowInstance has been processed completely.||runtimeContext={}", runtimeContext);
            runtimeContext.setFlowInstanceStatus(FlowInstanceStatus.COMPLETED);
            suspendNodeInstance.setId(nodeInstancePO.getId());
            suspendNodeInstance.setNodeKey(nodeInstancePO.getNodeKey());
            suspendNodeInstance.setSourceNodeInstanceId(nodeInstancePO.getSourceNodeInstanceId());
            suspendNodeInstance.setSourceNodeKey(nodeInstancePO.getSourceNodeKey());
            suspendNodeInstance.setInstanceDataId(nodeInstancePO.getInstanceDataId());
            suspendNodeInstance.setStatus(nodeInstancePO.getStatus());
            throw new ReentrantException(ErrorEnum.REENTRANT_WARNING);
        }
        Map<String, InstanceData> instanceDataMap;
        String instanceDataId = nodeInstancePO.getInstanceDataId();
        if (StringUtils.isBlank(instanceDataId)) {
            instanceDataMap = Maps.newHashMap();
        } else {
            InstanceDataPO instanceDataPO = instanceDataDAO.select(flowInstanceId, instanceDataId);
            if (instanceDataPO == null) {
                LOGGER.warn("preCommit failed: cannot find instanceDataPO from db." +
                    "||flowInstanceId={}||instanceDataId={}", flowInstanceId, instanceDataId);
                throw new ProcessException(ErrorEnum.GET_INSTANCE_DATA_FAILED);
            }
            instanceDataMap = InstanceDataUtil.getInstanceDataMap(instanceDataPO.getInstanceData());
        }

        //2.merge data while commitDataMap is not empty
        Map<String, InstanceData> commitDataMap = runtimeContext.getInstanceDataMap();
        boolean isCallActivityNode = FlowModelUtil.isElementType(nodeInstancePO.getNodeKey(), runtimeContext.getFlowElementMap(), FlowElementType.CALL_ACTIVITY);
        if (isCallActivityNode) {
            // commit callActivity not allow merge data
            instanceDataMap = commitDataMap;
        } else if (MapUtils.isNotEmpty(commitDataMap)) {
            instanceDataId = genId();
            instanceDataMap.putAll(commitDataMap);

            InstanceDataPO commitInstanceDataPO = buildCommitInstanceData(runtimeContext, nodeInstanceId,
                nodeInstancePO.getNodeKey(), instanceDataId, instanceDataMap);
            instanceDataDAO.insert(commitInstanceDataPO);
        }

        //3.update runtimeContext
        fillCommitContext(runtimeContext, nodeInstancePO, instanceDataId, instanceDataMap);
    }

    private InstanceDataPO buildCommitInstanceData(RuntimeContext runtimeContext, String nodeInstanceId, String nodeKey,
                                                   String newInstanceDataId, Map<String, InstanceData> instanceDataMap) {
        InstanceDataPO instanceDataPO = new InstanceDataPO();
        BeanUtils.copyProperties(runtimeContext, instanceDataPO);

        instanceDataPO.setNodeInstanceId(nodeInstanceId);
        instanceDataPO.setNodeKey(nodeKey);
        instanceDataPO.setType(InstanceDataType.COMMIT);
        instanceDataPO.setCreateTime(new Date());

        instanceDataPO.setInstanceDataId(newInstanceDataId);
        instanceDataPO.setInstanceData(InstanceDataUtil.getInstanceDataListStr(instanceDataMap));

        return instanceDataPO;
    }

    private void fillCommitContext(RuntimeContext runtimeContext, NodeInstancePO nodeInstancePO, String instanceDataId,
                                   Map<String, InstanceData> instanceDataMap) throws ProcessException {

        runtimeContext.setInstanceDataId(instanceDataId);
        runtimeContext.setInstanceDataMap(instanceDataMap);

        updateSuspendNodeInstanceBO(runtimeContext.getSuspendNodeInstance(), nodeInstancePO, instanceDataId);

        setCurrentFlowModel(runtimeContext);

        runtimeContext.setNodeInstanceList(Lists.newArrayList());
    }

    private void doCommit(RuntimeContext runtimeContext) throws ProcessException {
        RuntimeExecutor runtimeExecutor = getExecuteExecutor(runtimeContext);
        runtimeExecutor.commit(runtimeContext);

        runtimeExecutor = runtimeExecutor.getExecuteExecutor(runtimeContext);
        while (runtimeExecutor != null) {
            runtimeExecutor.execute(runtimeContext);
            runtimeExecutor = runtimeExecutor.getExecuteExecutor(runtimeContext);
        }
    }

    private void postCommit(RuntimeContext runtimeContext) throws ProcessException {
        if (runtimeContext.getProcessStatus() == ProcessStatus.SUCCESS && runtimeContext.getCurrentNodeInstance() != null) {
            runtimeContext.setSuspendNodeInstance(runtimeContext.getCurrentNodeInstance());
        }
        //update FlowInstancePO to db
        saveNodeInstanceList(runtimeContext, NodeInstanceType.COMMIT);

        if (isCompleted(runtimeContext)) {
            if (isSubFlowInstance(runtimeContext)) {
                processInstanceDAO.updateStatus(runtimeContext.getFlowInstanceId(), FlowInstanceStatus.END);
                runtimeContext.setFlowInstanceStatus(FlowInstanceStatus.END);
            } else {
                processInstanceDAO.updateStatus(runtimeContext.getFlowInstanceId(), FlowInstanceStatus.COMPLETED);
                runtimeContext.setFlowInstanceStatus(FlowInstanceStatus.COMPLETED);
            }

            LOGGER.info("postCommit: flowInstance process completely.||flowInstanceId={}", runtimeContext.getFlowInstanceId());
        }
    }

    ////////////////////////////////////////rollback////////////////////////////////////////

    @Override
    public void rollback(RuntimeContext runtimeContext) throws ProcessException {
        int processStatus = ProcessStatus.SUCCESS;
        try {
            preRollback(runtimeContext);
            doRollback(runtimeContext);
        } catch (ReentrantException re) {
            //ignore
        } catch (ProcessException pe) {
            if (!ErrorEnum.isSuccess(pe.getErrNo())) {
                processStatus = ProcessStatus.FAILED;
            }
            throw pe;
        } finally {
            runtimeContext.setProcessStatus(processStatus);
            postRollback(runtimeContext);
        }
    }

    private void preRollback(RuntimeContext runtimeContext) throws ProcessException {
        String flowInstanceId = runtimeContext.getFlowInstanceId();

        //1.check node: only the latest enabled(ACTIVE or COMPLETED) nodeInstance can be rollbacked.
        String suspendNodeInstanceId = runtimeContext.getSuspendNodeInstance().getNodeInstanceId();
        NodeInstancePO rollbackNodeInstancePO = getActiveNodeForRollback(flowInstanceId, suspendNodeInstanceId,
            runtimeContext.getFlowElementMap());
        if (rollbackNodeInstancePO == null) {
            LOGGER.warn("preRollback failed: cannot rollback.||runtimeContext={}", runtimeContext);
            throw new ProcessException(ErrorEnum.ROLLBACK_FAILED);
        }

        //2.check status: flowInstance is completed
        if (isCompleted(runtimeContext)) {
            LOGGER.warn("invalid preRollback: FlowInstance has been processed completely."
                + "||flowInstanceId={}||flowDeployId={}", flowInstanceId, runtimeContext.getFlowDeployId());
            NodeInstanceBO suspendNodeInstance = new NodeInstanceBO();
            BeanUtils.copyProperties(rollbackNodeInstancePO, suspendNodeInstance);
            runtimeContext.setSuspendNodeInstance(suspendNodeInstance);
            runtimeContext.setFlowInstanceStatus(FlowInstanceStatus.COMPLETED);
            throw new ProcessException(ErrorEnum.ROLLBACK_FAILED);
        }

        //3.get instanceData
        String instanceDataId = rollbackNodeInstancePO.getInstanceDataId();
        Map<String, InstanceData> instanceDataMap;
        if (StringUtils.isBlank(instanceDataId)) {
            instanceDataMap = Maps.newHashMap();
        } else {
            InstanceDataPO instanceDataPO = instanceDataDAO.select(flowInstanceId, instanceDataId);
            if (instanceDataPO == null) {
                LOGGER.warn("preRollback failed: cannot find instanceDataPO from db."
                    + "||flowInstanceId={}||instanceDataId={}", flowInstanceId, instanceDataId);
                throw new ProcessException(ErrorEnum.GET_INSTANCE_DATA_FAILED);
            }
            instanceDataMap = InstanceDataUtil.getInstanceDataMap(instanceDataPO.getInstanceData());
        }

        //4.update runtimeContext
        fillRollbackContext(runtimeContext, rollbackNodeInstancePO, instanceDataMap);
    }

    // if(canRollback): only the active Node or the lasted completed Node can be rollback
    private NodeInstancePO getActiveNodeForRollback(String flowInstanceId, String suspendNodeInstanceId,
                                                    Map<String, FlowElement> flowElementMap) {
        List<NodeInstancePO> nodeInstancePOList = nodeInstanceDAO.selectDescByFlowInstanceId(flowInstanceId);
        if (CollectionUtils.isEmpty(nodeInstancePOList)) {
            LOGGER.warn("getActiveNodeForRollback: nodeInstancePOList is empty."
                + "||flowInstanceId={}||suspendNodeInstanceId={}", flowInstanceId, suspendNodeInstanceId);
            return null;
        }

        for (NodeInstancePO nodeInstancePO : nodeInstancePOList) {
            int elementType = FlowModelUtil.getElementType(nodeInstancePO.getNodeKey(), flowElementMap);
            if (elementType != FlowElementType.USER_TASK
                && elementType != FlowElementType.END_EVENT
                && elementType != FlowElementType.CALL_ACTIVITY) {
                LOGGER.info("getActiveNodeForRollback: ignore un-userTask or un-endEvent or un-callActivity nodeInstance.||flowInstanceId={}"
                    + "||suspendNodeInstanceId={}||nodeKey={}", flowInstanceId, suspendNodeInstanceId, nodeInstancePO.getNodeKey());
                continue;
            }

            if (nodeInstancePO.getStatus() == NodeInstanceStatus.ACTIVE) {
                if (nodeInstancePO.getNodeInstanceId().equals(suspendNodeInstanceId)) {
                    LOGGER.info("getActiveNodeForRollback: roll back the active Node."
                        + "||flowInstanceId={}||suspendNodeInstanceId={}", flowInstanceId, suspendNodeInstanceId);
                    return nodeInstancePO;
                }
            } else if (nodeInstancePO.getStatus() == NodeInstanceStatus.COMPLETED) {
                if (nodeInstancePO.getNodeInstanceId().equals(suspendNodeInstanceId)) {
                    LOGGER.info("getActiveNodeForRollback: roll back the lasted completed Node."
                            + "||flowInstanceId={}||suspendNodeInstanceId={}||activeNodeInstanceId={}",
                        flowInstanceId, suspendNodeInstanceId, nodeInstancePO);
                    return nodeInstancePO;
                }

                LOGGER.warn("getActiveNodeForRollback: cannot rollback the Node."
                    + "||flowInstanceId={}||suspendNodeInstanceId={}", flowInstanceId, suspendNodeInstanceId);
                return null;
            }
            LOGGER.info("getActiveNodeForRollback: ignore disabled Node instance.||flowInstanceId={}"
                + "||suspendNodeInstanceId={}||status={}", flowInstanceId, suspendNodeInstanceId, nodeInstancePO.getStatus());

        }
        LOGGER.warn("getActiveNodeForRollback: cannot rollback the suspendNodeInstance."
            + "||flowInstanceId={}||suspendNodeInstanceId={}", flowInstanceId, suspendNodeInstanceId);
        return null;
    }

    private void doRollback(RuntimeContext runtimeContext) throws ProcessException {
        RuntimeExecutor runtimeExecutor = getRollbackExecutor(runtimeContext);
        while (runtimeExecutor != null) {
            runtimeExecutor.rollback(runtimeContext);
            runtimeExecutor = runtimeExecutor.getRollbackExecutor(runtimeContext);
        }
    }

    private void postRollback(RuntimeContext runtimeContext) {

        if (runtimeContext.getProcessStatus() != ProcessStatus.SUCCESS) {
            LOGGER.warn("postRollback: ignore while process failed.||runtimeContext={}", runtimeContext);
            return;
        }
        if (runtimeContext.getCurrentNodeInstance() != null) {
            runtimeContext.setSuspendNodeInstance(runtimeContext.getCurrentNodeInstance());
        }

        //update FlowInstancePO to db
        saveNodeInstanceList(runtimeContext, NodeInstanceType.ROLLBACK);

        if (FlowModelUtil.isElementType(runtimeContext.getCurrentNodeModel().getKey(), runtimeContext.getFlowElementMap(), FlowElementType.START_EVENT)) {
            runtimeContext.setFlowInstanceStatus(FlowInstanceStatus.TERMINATED);
            processInstanceDAO.updateStatus(runtimeContext.getFlowInstanceId(), FlowInstanceStatus.TERMINATED);
        } else if (runtimeContext.getFlowInstanceStatus() == FlowInstanceStatus.END) {
            runtimeContext.setFlowInstanceStatus(FlowInstanceStatus.RUNNING);
            processInstanceDAO.updateStatus(runtimeContext.getFlowInstanceId(), FlowInstanceStatus.RUNNING);
        }
    }

    private void fillRollbackContext(RuntimeContext runtimeContext, NodeInstancePO nodeInstancePO,
                                     Map<String, InstanceData> instanceDataMap) throws ProcessException {
        runtimeContext.setInstanceDataId(nodeInstancePO.getInstanceDataId());
        runtimeContext.setInstanceDataMap(instanceDataMap);
        runtimeContext.setNodeInstanceList(Lists.newArrayList());
        NodeInstanceBO suspendNodeInstanceBO = buildSuspendNodeInstanceBO(nodeInstancePO);
        runtimeContext.setSuspendNodeInstance(suspendNodeInstanceBO);
        setCurrentFlowModel(runtimeContext);
    }

    private NodeInstanceBO buildSuspendNodeInstanceBO(NodeInstancePO nodeInstancePO) {
        NodeInstanceBO suspendNodeInstanceBO = new NodeInstanceBO();
        BeanUtils.copyProperties(nodeInstancePO, suspendNodeInstanceBO);
        return suspendNodeInstanceBO;
    }

    private void updateSuspendNodeInstanceBO(NodeInstanceBO suspendNodeInstanceBO, NodeInstancePO nodeInstancePO, String
            instanceDataId) {
        suspendNodeInstanceBO.setId(nodeInstancePO.getId());
        suspendNodeInstanceBO.setNodeKey(nodeInstancePO.getNodeKey());
        suspendNodeInstanceBO.setStatus(nodeInstancePO.getStatus());
        suspendNodeInstanceBO.setSourceNodeInstanceId(nodeInstancePO.getSourceNodeInstanceId());
        suspendNodeInstanceBO.setSourceNodeKey(nodeInstancePO.getSourceNodeKey());
        suspendNodeInstanceBO.setInstanceDataId(instanceDataId);
    }

    //suspendNodeInstanceBO is necessary
    private void setCurrentFlowModel(RuntimeContext runtimeContext) throws ProcessException {
        NodeInstanceBO suspendNodeInstanceBO = runtimeContext.getSuspendNodeInstance();
        FlowElement currentNodeModel = FlowModelUtil.getFlowElement(runtimeContext.getFlowElementMap(), suspendNodeInstanceBO.getNodeKey());
        if (currentNodeModel == null) {
            LOGGER.warn("setCurrentFlowModel failed: cannot get currentNodeModel.||flowInstance={}||flowDeployId={}||nodeKey={}",
                    runtimeContext.getFlowInstanceId(), runtimeContext.getFlowDeployId(), suspendNodeInstanceBO.getNodeKey());
            throw new ProcessException(ErrorEnum.GET_NODE_FAILED);
        }
        runtimeContext.setCurrentNodeModel(currentNodeModel);
    }

    @Override
    protected boolean isCompleted(RuntimeContext runtimeContext) throws ProcessException {
        if (runtimeContext.getFlowInstanceStatus() == FlowInstanceStatus.COMPLETED) {
            return true;
        }
        if (runtimeContext.getFlowInstanceStatus() == FlowInstanceStatus.END) {
            return false;
        }
        NodeInstanceBO suspendNodeInstance = runtimeContext.getSuspendNodeInstance();
        if (suspendNodeInstance == null) {
            LOGGER.warn("suspendNodeInstance is null.||runtimeContext={}", runtimeContext);
            return false;
        }

        if (suspendNodeInstance.getStatus() != NodeInstanceStatus.COMPLETED) {
            return false;
        }

        String nodeKey = suspendNodeInstance.getNodeKey();
        Map<String, FlowElement> flowElementMap = runtimeContext.getFlowElementMap();
        if (FlowModelUtil.getFlowElement(flowElementMap, nodeKey).getType() == FlowElementType.END_EVENT) {
            return true;
        }
        return false;
    }

    @Override
    protected RuntimeExecutor getExecuteExecutor(RuntimeContext runtimeContext) throws ProcessException {
        return getElementExecutor(runtimeContext);
    }

    @Override
    protected RuntimeExecutor getRollbackExecutor(RuntimeContext runtimeContext) throws ProcessException {
        return getElementExecutor(runtimeContext);
    }

    private RuntimeExecutor getElementExecutor(RuntimeContext runtimeContext) throws ProcessException {
        //if process completed, return null
        if (isCompleted(runtimeContext)) {
            return null;
        }
        return executorFactory.getElementExecutor(runtimeContext.getCurrentNodeModel());
    }

    ////////////////////////////////////////common////////////////////////////////////////

    private void saveNodeInstanceList(RuntimeContext runtimeContext, int nodeInstanceType) {

        List<NodeInstanceBO> processNodeList = runtimeContext.getNodeInstanceList();

        if (CollectionUtils.isEmpty(processNodeList)) {
            LOGGER.warn("saveNodeInstanceList: processNodeList is empty,||flowInstanceId={}||nodeInstanceType={}",
                    runtimeContext.getFlowInstanceId(), nodeInstanceType);
            return;
        }

        List<NodeInstancePO> nodeInstancePOList = Lists.newArrayList();
        List<NodeInstanceLogPO> nodeInstanceLogPOList = Lists.newArrayList();

        processNodeList.forEach(nodeInstanceBO -> {
            NodeInstancePO nodeInstancePO = buildNodeInstancePO(runtimeContext, nodeInstanceBO);
            if (nodeInstancePO != null) {
                nodeInstancePOList.add(nodeInstancePO);

                //build nodeInstance log
                NodeInstanceLogPO nodeInstanceLogPO = buildNodeInstanceLogPO(nodeInstancePO, nodeInstanceType);
                nodeInstanceLogPOList.add(nodeInstanceLogPO);
            }
        });
        nodeInstanceDAO.insertOrUpdateList(nodeInstancePOList);
        nodeInstanceLogDAO.insertList(nodeInstanceLogPOList);
    }

    private NodeInstancePO buildNodeInstancePO(RuntimeContext runtimeContext, NodeInstanceBO nodeInstanceBO) {
        if (runtimeContext.getProcessStatus() == ProcessStatus.FAILED) {
            //set status=FAILED unless it is origin processNodeInstance(suspendNodeInstance)
            if (nodeInstanceBO.getNodeKey().equals(runtimeContext.getSuspendNodeInstance().getNodeKey())) {
                //keep suspendNodeInstance's status while process failed.
                return null;
            }
            nodeInstanceBO.setStatus(NodeInstanceStatus.FAILED);
        }

        NodeInstancePO nodeInstancePO = new NodeInstancePO();
        BeanUtils.copyProperties(nodeInstanceBO, nodeInstancePO);
        nodeInstancePO.setFlowInstanceId(runtimeContext.getFlowInstanceId());
        nodeInstancePO.setFlowDeployId(runtimeContext.getFlowDeployId());
        nodeInstancePO.setTenant(runtimeContext.getTenant());
        nodeInstancePO.setCaller(runtimeContext.getCaller());
        Date currentTime = new Date();
        nodeInstancePO.setCreateTime(currentTime);
        nodeInstancePO.setModifyTime(currentTime);
        return nodeInstancePO;
    }

    private NodeInstanceLogPO buildNodeInstanceLogPO(NodeInstancePO nodeInstancePO, int nodeInstanceType) {
        NodeInstanceLogPO nodeInstanceLogPO = new NodeInstanceLogPO();
        BeanUtils.copyProperties(nodeInstancePO, nodeInstanceLogPO);
        nodeInstanceLogPO.setType(nodeInstanceType);
        return nodeInstanceLogPO;
    }

}
