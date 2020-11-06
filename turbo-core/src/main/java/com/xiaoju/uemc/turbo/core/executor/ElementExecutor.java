package com.xiaoju.uemc.turbo.core.executor;

import com.xiaoju.uemc.turbo.core.bo.NodeInstanceBO;
import com.xiaoju.uemc.turbo.core.common.ErrorEnum;
import com.xiaoju.uemc.turbo.core.common.NodeInstanceStatus;
import com.xiaoju.uemc.turbo.core.common.RuntimeContext;
import com.xiaoju.uemc.turbo.core.entity.NodeInstancePO;
import com.xiaoju.uemc.turbo.core.exception.ProcessException;
import com.xiaoju.uemc.turbo.core.exception.ReentrantException;
import com.xiaoju.uemc.turbo.core.exception.SuspendException;
import com.xiaoju.uemc.turbo.core.model.FlowElement;
import com.xiaoju.uemc.turbo.core.util.FlowModelUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.Map;

/**
 * Created by Stefanie on 2019/12/1.
 */
public abstract class ElementExecutor extends RuntimeExecutor {

    @Override
    public void execute(RuntimeContext runtimeContext) throws Exception {
        try {
            preExecute(runtimeContext);
            doExecute(runtimeContext);
        } catch (ReentrantException re) {
            LOGGER.warn("execute ReentrantException: reentrant execute.||runtimeContext={},", runtimeContext, re);
        } catch (SuspendException se) {
            LOGGER.info("execute suspend.||runtimeContext={}", runtimeContext);
            throw se;
        } catch (Throwable t) {
            LOGGER.warn("execute exception.||runtimeContext={},", runtimeContext, t);
            throw t;
        } finally {
            postExecute(runtimeContext);
        }
    }

    /**
     * Init runtimeContext: update currentNodeInstance
     * 1.currentNodeInfo(nodeInstance & nodeKey): currentNode is this.model
     * 2.sourceNodeInfo(nodeInstance & nodeKey): sourceNode is runtimeContext.currentNodeInstance
     */
    protected void preExecute(RuntimeContext runtimeContext) throws Exception {

        NodeInstanceBO currentNodeInstance = new NodeInstanceBO();

        String flowInstanceId = runtimeContext.getFlowInstanceId();
        String nodeKey = runtimeContext.getCurrentNodeModel().getKey();

        //get sourceInfo
        String sourceNodeInstanceId = StringUtils.EMPTY;
        String sourceNodeKey = StringUtils.EMPTY;
        NodeInstanceBO sourceNodeInstance = runtimeContext.getCurrentNodeInstance();
        if (sourceNodeInstance != null) {
            // TODO: 2019/12/30 cache
            NodeInstancePO nodeInstancePO = nodeInstanceDAO.selectBySourceInstanceId(flowInstanceId,
                    sourceNodeInstance.getNodeInstanceId(), nodeKey);
            //reentrant check
            if (nodeInstancePO != null) {
                BeanUtils.copyProperties(nodeInstancePO, currentNodeInstance);
                runtimeContext.setCurrentNodeInstance(currentNodeInstance);
                LOGGER.warn("preExecute reentrant.||nodeInstancePO={}", nodeInstancePO);
                return;
            }
            sourceNodeInstanceId = sourceNodeInstance.getNodeInstanceId();
            sourceNodeKey = sourceNodeInstance.getNodeKey();
        }

        String nodeInstanceId = genId();
        currentNodeInstance.setNodeInstanceId(nodeInstanceId);
        currentNodeInstance.setNodeKey(nodeKey);
        currentNodeInstance.setSourceNodeInstanceId(sourceNodeInstanceId);
        currentNodeInstance.setSourceNodeKey(sourceNodeKey);
        currentNodeInstance.setStatus(NodeInstanceStatus.ACTIVE);
        currentNodeInstance.setInstanceDataId(StringUtils.defaultString(runtimeContext.getInstanceDataId(), StringUtils.EMPTY));

        runtimeContext.setCurrentNodeInstance(currentNodeInstance);
    }

    protected void doExecute(RuntimeContext runtimeContext) throws Exception {
    }

    protected void postExecute(RuntimeContext runtimeContext) throws Exception {
    }

    @Override
    protected RuntimeExecutor getExecuteExecutor(RuntimeContext runtimeContext) throws Exception {
        Map<String, FlowElement> flowElementMap = runtimeContext.getFlowElementMap();
        FlowElement flowElement = FlowModelUtil.getUniqueNextNode(runtimeContext.getCurrentNodeModel(), flowElementMap);
        runtimeContext.setCurrentNodeModel(flowElement);
        return executorFactory.getElementExecutor(flowElement);
    }

    @Override
    public void commit(RuntimeContext runtimeContext) throws Exception {

        preCommit(runtimeContext);

        try {
            doCommit(runtimeContext);
        } catch (SuspendException se) {
            LOGGER.warn("SuspendException.");
            throw se;
        } finally {
            postCommit(runtimeContext);
        }
    }

    protected void preCommit(RuntimeContext runtimeContext) throws Exception {
        LOGGER.warn("preCommit: unsupported element type.||flowInstanceId={}||elementType={}",
                runtimeContext.getFlowInstanceId(), runtimeContext.getCurrentNodeModel().getType());
        throw new ProcessException(ErrorEnum.UNSUPPORTED_ELEMENT_TYPE);
    }

    protected void doCommit(RuntimeContext runtimeContext) throws Exception {
    }

    protected void postCommit(RuntimeContext runtimeContext) throws Exception {
    }

    @Override
    public void rollback(RuntimeContext runtimeContext) throws Exception {
        try {
            preRollback(runtimeContext);
            doRollback(runtimeContext);
        } catch (SuspendException se) {
            LOGGER.warn("SuspendException.");
            throw se;
        } catch (ReentrantException re) {
            LOGGER.warn("ReentrantException: reentrant rollback.");
        } finally {
            postRollback(runtimeContext);
        }
    }

    /**
     * Init runtimeContext: update currentNodeInstance
     * <p>
     * Case1. First node(UserTask) to rollback(there's no currentNodeInstance in runtimeContext):
     * Set newCurrentNodeInstance = suspendNodeInstance
     * <p>
     * Case2. Un-first node to rollback:
     * Set newCurrentNodeInstance = oldCurrentNodeInstance.sourceNodeInstance
     * <p>
     * ReentrantException: while currentNodeInstance is DISABLED
     *
     * @throws Exception
     */
    protected void preRollback(RuntimeContext runtimeContext) throws Exception {
        String flowInstanceId = runtimeContext.getFlowInstanceId();
        String nodeInstanceId, nodeKey;
        NodeInstanceBO currentNodeInstance;
        if (runtimeContext.getCurrentNodeInstance() == null) {
            //case1
            currentNodeInstance = runtimeContext.getSuspendNodeInstance();
        } else {
            //case2
            nodeInstanceId = runtimeContext.getCurrentNodeInstance().getSourceNodeInstanceId();
            NodeInstancePO currentNodeInstancePO = nodeInstanceDAO.selectByNodeInstanceId(flowInstanceId, nodeInstanceId);
            if (currentNodeInstancePO == null) {
                LOGGER.warn("preRollback failed: cannot find currentNodeInstancePO from db."
                        + "||flowInstanceId={}||nodeInstanceId={}", flowInstanceId, nodeInstanceId);
                throw new ProcessException(ErrorEnum.GET_NODE_INSTANCE_FAILED);
            }
            currentNodeInstance = new NodeInstanceBO();
            BeanUtils.copyProperties(currentNodeInstancePO, currentNodeInstance);
            currentNodeInstance.setInstanceDataId(runtimeContext.getInstanceDataId());
        }
        runtimeContext.setCurrentNodeInstance(currentNodeInstance);

        nodeInstanceId = currentNodeInstance.getNodeInstanceId();
        nodeKey = currentNodeInstance.getNodeKey();
        int currentStatus = currentNodeInstance.getStatus();
        if (currentStatus == NodeInstanceStatus.DISABLED) {
            LOGGER.warn("preRollback: reentrant process.||flowInstanceId={}||nodeInstance={}||nodeKey={}", flowInstanceId, nodeInstanceId, nodeKey);
            throw new ReentrantException(ErrorEnum.REENTRANT_WARNING);
        }
        LOGGER.info("preRollback done.||flowInstanceId={}||nodeInstance={}||nodeKey={}", flowInstanceId, nodeInstanceId, nodeKey);
    }

    /**
     * Common rollback: overwrite it in customized elementExecutor or do nothing
     *
     * @throws Exception
     */
    protected void doRollback(RuntimeContext runtimeContext) throws Exception {
    }

    /**
     * Update runtimeContext: update currentNodeInstance.status to DISABLED and add it to nodeInstanceList
     *
     * @throws Exception
     */
    protected void postRollback(RuntimeContext runtimeContext) throws Exception {
        NodeInstanceBO currentNodeInstance = runtimeContext.getCurrentNodeInstance();
        currentNodeInstance.setStatus(NodeInstanceStatus.DISABLED);
        runtimeContext.getNodeInstanceList().add(currentNodeInstance);
    }

    /**
     * Get elementExecutor to rollback:
     * Get sourceNodeInstanceId from currentNodeInstance and get sourceElement
     *
     * @return
     * @throws Exception
     */
    @Override
    protected ElementExecutor getRollbackExecutor(RuntimeContext runtimeContext) throws Exception {
        String flowInstanceId = runtimeContext.getFlowInstanceId();
        NodeInstanceBO currentNodeInstance = runtimeContext.getCurrentNodeInstance();

        String sourceNodeInstanceId = currentNodeInstance.getSourceNodeInstanceId();
        if (StringUtils.isBlank(sourceNodeInstanceId)) {
            LOGGER.warn("getRollbackExecutor: there's no sourceNodeInstance(startEvent)."
                    + "||flowInstanceId={}||nodeInstanceId={}", flowInstanceId, currentNodeInstance.getNodeInstanceId());
            return null;
        }

        // TODO: 2019/12/13 get from cache
        NodeInstancePO sourceNodeInstancePO = nodeInstanceDAO.selectByNodeInstanceId(flowInstanceId, sourceNodeInstanceId);
        if (sourceNodeInstancePO == null) {
            LOGGER.warn("getRollbackExecutor failed: cannot find sourceNodeInstance from db."
                    + "||flowInstanceId={}||sourceNodeInstanceId={}", flowInstanceId, sourceNodeInstanceId);
            throw new ProcessException(ErrorEnum.GET_NODE_INSTANCE_FAILED);
        }

        FlowElement sourceNode = FlowModelUtil.getFlowElement(runtimeContext.getFlowElementMap(),
                sourceNodeInstancePO.getNodeKey());

        // TODO: 2019/12/18
        runtimeContext.setCurrentNodeModel(sourceNode);
        return executorFactory.getElementExecutor(sourceNode);
    }

    @Override
    protected boolean isCompleted(RuntimeContext runtimeContext) {
        NodeInstanceBO nodeInstance = runtimeContext.getCurrentNodeInstance();
        //case 1.startEvent
        if (nodeInstance == null) {
            return false;
        }

        //case 2.begin to process the node
        if (!runtimeContext.getCurrentNodeModel().getKey().equals(nodeInstance.getNodeKey())) {
            return false;
        }

        //case 3.process completed
        if (nodeInstance.getStatus() == NodeInstanceStatus.COMPLETED) {
            return true;
        }

        //case 4.to process
        return false;
    }
}
