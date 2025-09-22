package com.didiglobal.turbo.engine.executor;

import com.didiglobal.turbo.engine.bo.NodeInstanceBO;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.common.FlowElementType;
import com.didiglobal.turbo.engine.common.NodeInstanceStatus;
import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.exception.ReentrantException;
import com.didiglobal.turbo.engine.exception.SuspendException;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.util.ExpressionCalculator;
import com.didiglobal.turbo.engine.util.FlowModelUtil;
import com.didiglobal.turbo.engine.util.InstanceDataUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;

public abstract class ElementExecutor extends RuntimeExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElementExecutor.class);

    @Resource
    protected ExpressionCalculator expressionCalculator;

    @Override
    public void execute(RuntimeContext runtimeContext) throws ProcessException {
        try {
            preExecute(runtimeContext);
            doExecute(runtimeContext);
        } catch (ReentrantException re) {
            LOGGER.warn("execute ReentrantException: reentrant execute.||runtimeContext={},", runtimeContext, re);
        } catch (SuspendException se) {
            LOGGER.info("execute suspend.||runtimeContext={}", runtimeContext);
            throw se;
        } finally {
            postExecute(runtimeContext);
        }
    }

    /**
     * Init runtimeContext: update currentNodeInstance
     * 1.currentNodeInfo(nodeInstance & nodeKey): currentNode is this.model
     * 2.sourceNodeInfo(nodeInstance & nodeKey): sourceNode is runtimeContext.currentNodeInstance
     */
    protected void preExecute(RuntimeContext runtimeContext) throws ProcessException {

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
        currentNodeInstance.getProperties().putAll(runtimeContext.getExtendProperties());
        currentNodeInstance.setNodeType(runtimeContext.getCurrentNodeModel().getType());
        currentNodeInstance.setInstanceDataId(StringUtils.defaultString(runtimeContext.getInstanceDataId(), StringUtils.EMPTY));

        runtimeContext.setCurrentNodeInstance(currentNodeInstance);
    }

    protected void doExecute(RuntimeContext runtimeContext) throws ProcessException {
    }

    protected void postExecute(RuntimeContext runtimeContext) throws ProcessException {
    }

    @Override
    protected RuntimeExecutor getExecuteExecutor(RuntimeContext runtimeContext) throws ProcessException {
        Map<String, FlowElement> flowElementMap = runtimeContext.getFlowElementMap();
        FlowElement flowElement = getUniqueNextNode(runtimeContext.getCurrentNodeModel(), flowElementMap);
        runtimeContext.setCurrentNodeModel(flowElement);
        return executorFactory.getElementExecutor(flowElement);
    }

    @Override
    public void commit(RuntimeContext runtimeContext) throws ProcessException {
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


    protected void preCommit(RuntimeContext runtimeContext) throws ProcessException {
        LOGGER.warn("preCommit: unsupported element type.||flowInstanceId={}||elementType={}",
                runtimeContext.getFlowInstanceId(), runtimeContext.getCurrentNodeModel().getType());
        throw new ProcessException(ErrorEnum.UNSUPPORTED_ELEMENT_TYPE);
    }

    protected void doCommit(RuntimeContext runtimeContext) throws ProcessException {
    }

    protected void postCommit(RuntimeContext runtimeContext) throws ProcessException {
    }

    @Override
    public void rollback(RuntimeContext runtimeContext) throws ProcessException {
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
    protected void preRollback(RuntimeContext runtimeContext) throws ProcessException {
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

            String currentInstanceDataId = currentNodeInstance.getInstanceDataId();
            runtimeContext.setInstanceDataId(currentInstanceDataId);
            InstanceDataPO instanceDataPO = instanceDataDAO.select(flowInstanceId, currentInstanceDataId);
            Map<String, InstanceData> currentInstanceDataMap = InstanceDataUtil.getInstanceDataMap(instanceDataPO.getInstanceData());
            runtimeContext.setInstanceDataMap(currentInstanceDataMap);
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
    protected void doRollback(RuntimeContext runtimeContext) throws ProcessException {
    }

    /**
     * Update runtimeContext: update currentNodeInstance. Status to DISABLED and add it to nodeInstanceList
     *
     * @throws Exception
     */
    protected void postRollback(RuntimeContext runtimeContext) throws ProcessException {
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
    protected ElementExecutor getRollbackExecutor(RuntimeContext runtimeContext) throws ProcessException {
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
    protected boolean isCompleted(RuntimeContext runtimeContext) throws ProcessException {
        NodeInstanceBO nodeInstance = runtimeContext.getCurrentNodeInstance();
        //case 1.startEvent
        if (nodeInstance == null) {
            return false;
        }

        //case 2.begin to process the node
        if (!runtimeContext.getCurrentNodeModel().getKey().equals(nodeInstance.getNodeKey())) {
            return false;
        }

        //case 3.process completed, case 4.to process
        return nodeInstance.getStatus() == NodeInstanceStatus.COMPLETED;
    }

    protected FlowElement getUniqueNextNode(FlowElement currentFlowElement, Map<String, FlowElement> flowElementMap) {
        List<String> outgoingKeyList = currentFlowElement.getOutgoing();
        String nextElementKey = outgoingKeyList.getFirst();
        FlowElement nextFlowElement = FlowModelUtil.getFlowElement(flowElementMap, nextElementKey);
        while (nextFlowElement.getType() == FlowElementType.SEQUENCE_FLOW) {
            nextFlowElement = getUniqueNextNode(nextFlowElement, flowElementMap);
        }
        return nextFlowElement;
    }

    protected FlowElement calculateNextNode(FlowElement currentFlowElement, Map<String, FlowElement> flowElementMap,
                                            Map<String, InstanceData> instanceDataMap) throws ProcessException {
        FlowElement nextFlowElement = calculateOutgoing(currentFlowElement, flowElementMap, instanceDataMap);

        while (nextFlowElement.getType() == FlowElementType.SEQUENCE_FLOW) {
            nextFlowElement = getUniqueNextNode(nextFlowElement, flowElementMap);
        }
        return nextFlowElement;
    }

    private FlowElement calculateOutgoing(FlowElement flowElement, Map<String, FlowElement> flowElementMap,
                                          Map<String, InstanceData> instanceDataMap) throws ProcessException {
        FlowElement defaultElement = null;

        List<String> outgoingList = flowElement.getOutgoing();
        for (String outgoingKey : outgoingList) {
            FlowElement outgoingSequenceFlow = FlowModelUtil.getFlowElement(flowElementMap, outgoingKey);

            //case1 condition is true, hit the outgoing
            String condition = FlowModelUtil.getConditionFromSequenceFlow(outgoingSequenceFlow);
            if (StringUtils.isNotBlank(condition) && processCondition(condition, instanceDataMap)) {
                return outgoingSequenceFlow;
            }

            if (FlowModelUtil.isDefaultCondition(outgoingSequenceFlow)) {
                defaultElement = outgoingSequenceFlow;
            }
        }
        //case2 return default while it has is configured
        if (defaultElement != null) {
            LOGGER.info("calculateOutgoing: return defaultElement.||nodeKey={}", flowElement.getKey());
            return defaultElement;
        }

        LOGGER.warn("calculateOutgoing failed.||nodeKey={}", flowElement.getKey());
        throw new ProcessException(ErrorEnum.GET_OUTGOING_FAILED);
    }

    protected boolean processCondition(String expression, Map<String, InstanceData> instanceDataMap) throws ProcessException {
        Map<String, Object> dataMap = InstanceDataUtil.parseInstanceDataMap(instanceDataMap);
        return expressionCalculator.calculate(expression, dataMap);
    }
}
