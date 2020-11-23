package com.xiaoju.uemc.turbo.engine.executor;

import com.xiaoju.uemc.turbo.engine.bo.NodeInstanceBO;
import com.xiaoju.uemc.turbo.engine.common.Constants;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.common.NodeInstanceStatus;
import com.xiaoju.uemc.turbo.engine.common.RuntimeContext;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import com.xiaoju.uemc.turbo.engine.exception.SuspendException;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.util.FlowModelUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by Stefanie on 2019/12/1.
 */

@Service
public class UserTaskExecutor extends ElementExecutor {

    @Override
    protected void doExecute(RuntimeContext runtimeContext) throws Exception {
        NodeInstanceBO currentNodeInstance = runtimeContext.getCurrentNodeInstance();
        if (currentNodeInstance.getStatus() == NodeInstanceStatus.COMPLETED) {
            LOGGER.warn("doExecute reentrant: currentNodeInstance is completed.||runtimeContext={}", runtimeContext);
            return;
        }

        if (currentNodeInstance.getStatus() != NodeInstanceStatus.ACTIVE) {
            currentNodeInstance.setStatus(NodeInstanceStatus.ACTIVE);
        }
        runtimeContext.getNodeInstanceList().add(currentNodeInstance);

        FlowElement flowElement = runtimeContext.getCurrentNodeModel();
        String nodeName = FlowModelUtil.getElementName(flowElement);
        LOGGER.info("doExecute: userTask to commit.||flowInstanceId={}||nodeInstanceId={}||nodeKey={}||nodeName={}",
                runtimeContext.getFlowInstanceId(), currentNodeInstance.getNodeInstanceId(), flowElement.getKey(), nodeName);
        throw new SuspendException(ErrorEnum.COMMIT_SUSPEND, MessageFormat.format(Constants.NODE_INSTANCE_FORMAT,
                flowElement.getKey(), nodeName, currentNodeInstance.getNodeInstanceId()));
    }

    @Override
    protected void preCommit(RuntimeContext runtimeContext) throws Exception {
        String flowInstanceId = runtimeContext.getFlowInstanceId();
        NodeInstanceBO suspendNodeInstance = runtimeContext.getSuspendNodeInstance();
        String nodeInstanceId = suspendNodeInstance.getNodeInstanceId();
        int status = suspendNodeInstance.getStatus();
        FlowElement flowElement = runtimeContext.getCurrentNodeModel();
        String nodeName = FlowModelUtil.getElementName(flowElement);
        String nodeKey = flowElement.getKey();

        NodeInstanceBO currentNodeInstance = new NodeInstanceBO();
        BeanUtils.copyProperties(suspendNodeInstance, currentNodeInstance);
        runtimeContext.setCurrentNodeInstance(currentNodeInstance);

        //invalid commit node
        if (!suspendNodeInstance.getNodeKey().equals(nodeKey)) {
            LOGGER.warn("preCommit: invalid nodeKey to commit.||flowInstanceId={}||nodeInstanceId={}||nodeKey={}||nodeName={}",
                    flowInstanceId, nodeInstanceId, nodeKey, nodeName);
            throw new ProcessException(ErrorEnum.COMMIT_FAILED, MessageFormat.format(Constants.NODE_INSTANCE_FORMAT,
                    flowElement.getKey(), nodeName, currentNodeInstance.getNodeInstanceId()));
        }

        //reentrant: completed
        if (status == NodeInstanceStatus.COMPLETED) {
            LOGGER.warn("preCommit: userTask is completed.||flowInstanceId={}||nodeInstanceId={}||nodeKey={}",
                    flowInstanceId, nodeInstanceId, nodeKey);
            return;
        }

        //invalid status
        if (status != NodeInstanceStatus.ACTIVE) {
            LOGGER.warn("preCommit: invalid status to commit.||flowInstanceId={}||status={}||nodeInstanceId={}||nodeKey={}",
                    flowInstanceId, status, nodeInstanceId, nodeKey);
            throw new ProcessException(ErrorEnum.COMMIT_FAILED, MessageFormat.format(Constants.NODE_INSTANCE_FORMAT,
                    flowElement.getKey(), nodeName, currentNodeInstance.getNodeInstanceId()));
        }
    }

    @Override
    protected void postCommit(RuntimeContext runtimeContext) throws Exception {
        NodeInstanceBO currentNodeInstance = runtimeContext.getCurrentNodeInstance();
        if (currentNodeInstance.getStatus() != NodeInstanceStatus.COMPLETED) {
            currentNodeInstance.setStatus(NodeInstanceStatus.COMPLETED);
            runtimeContext.getNodeInstanceList().add(currentNodeInstance);
        }
    }

    /**
     * Rollback: turn status to DISABLED.
     * If nodeInstance.status is COMPLETED, create new nodeInstance as currentNodeInstance.
     * <p>
     * SuspendException: while need suspend and status is COMPLETED
     */
    @Override
    protected void doRollback(RuntimeContext runtimeContext) throws Exception {

        NodeInstanceBO currentNodeInstance = runtimeContext.getCurrentNodeInstance();
        int currentStatus = currentNodeInstance.getStatus();
        currentNodeInstance.setStatus(NodeInstanceStatus.DISABLED);
        runtimeContext.getNodeInstanceList().add(currentNodeInstance);
        if (currentStatus == NodeInstanceStatus.COMPLETED) {
            NodeInstanceBO newNodeInstanceBO = new NodeInstanceBO();
            BeanUtils.copyProperties(currentNodeInstance, newNodeInstanceBO);
            // TODO: 2019/12/31 to insert new record
            newNodeInstanceBO.setId(null);
            String newNodeInstanceId = genId();
            newNodeInstanceBO.setNodeInstanceId(newNodeInstanceId);
            newNodeInstanceBO.setStatus(NodeInstanceStatus.ACTIVE);
            runtimeContext.setCurrentNodeInstance(newNodeInstanceBO);
            runtimeContext.getNodeInstanceList().add(newNodeInstanceBO);
            throw new SuspendException(ErrorEnum.ROLLBACK_SUSPEND, MessageFormat.format(Constants.NODE_INSTANCE_FORMAT,
                    newNodeInstanceBO.getNodeKey(),
                    FlowModelUtil.getFlowElement(runtimeContext.getFlowElementMap(), newNodeInstanceBO.getNodeKey()),
                    currentNodeInstance.getNodeInstanceId()));
        }
        LOGGER.info("doRollback.||currentNodeInstance={}||nodeKey={}", currentNodeInstance, currentNodeInstance.getNodeKey());
    }

    @Override
    protected void postRollback(RuntimeContext runtimeContext) throws Exception {
        //do nothing
    }


    /**
     * Calculate unique outgoing
     * Case1.unique outgoing;
     * Case2.calculateOne from multiple outgoings as exclusiveGateway
     * Calculate expression: one of flowElement's properties
     * Calculate input: data map
     *
     * @return
     * @throws Exception
     */
    @Override
    protected RuntimeExecutor getExecuteExecutor(RuntimeContext runtimeContext) throws Exception {
        FlowElement currentFlowElement = runtimeContext.getCurrentNodeModel();
        Map<String, FlowElement> flowElementMap = runtimeContext.getFlowElementMap();

        FlowElement nextNode;
        if (currentFlowElement.getOutgoing().size() == 1) {
            //case1. unique outgoing
            nextNode = getUniqueNextNode(currentFlowElement, flowElementMap);
        } else {
            //case2. multiple outgoings and calculate the next node with instanceDataMap
            nextNode = calculateNextNode(currentFlowElement, flowElementMap, runtimeContext.getInstanceDataMap());
        }
        LOGGER.info("getExecuteExecutor.||nextNode={}||runtimeContext={}", nextNode, runtimeContext);

        runtimeContext.setCurrentNodeModel(nextNode);

        return executorFactory.getElementExecutor(nextNode);
    }
}
