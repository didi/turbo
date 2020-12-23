package com.didiglobal.turbo.engine.processor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.didiglobal.turbo.engine.bo.*;
import com.didiglobal.turbo.engine.common.*;
import com.didiglobal.turbo.engine.dao.FlowDeploymentDAO;
import com.didiglobal.turbo.engine.dao.InstanceDataDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import com.didiglobal.turbo.engine.entity.FlowInstancePO;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.exception.ReentrantException;
import com.didiglobal.turbo.engine.exception.TurboException;
import com.didiglobal.turbo.engine.executor.FlowExecutor;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.param.CommitTaskParam;
import com.didiglobal.turbo.engine.param.RollbackTaskParam;
import com.didiglobal.turbo.engine.param.StartProcessParam;
import com.didiglobal.turbo.engine.result.*;
import com.didiglobal.turbo.engine.util.FlowModelUtil;
import com.didiglobal.turbo.engine.util.InstanceDataUtil;
import com.didiglobal.turbo.engine.validator.ParamValidator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class RuntimeProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeProcessor.class);

    @Resource
    private FlowDeploymentDAO flowDeploymentDAO;

    @Resource
    private ProcessInstanceDAO processInstanceDAO;

    @Resource
    private NodeInstanceDAO nodeInstanceDAO;

    @Resource
    private InstanceDataDAO instanceDataDAO;

    @Resource
    private FlowExecutor flowExecutor;

    ////////////////////////////////////////startProcess////////////////////////////////////////

    public StartProcessResult startProcess(StartProcessParam startProcessParam) {
        RuntimeContext runtimeContext = null;
        try {
            //1.param validate
            ParamValidator.validate(startProcessParam);

            //2.getFlowInfo
            FlowInfo flowInfo = getFlowInfo(startProcessParam);

            //3.init context for runtime
            runtimeContext = buildStartProcessContext(flowInfo, startProcessParam.getVariables());

            //4.process
            flowExecutor.execute(runtimeContext);

            //5.build result
            return buildStartProcessResult(runtimeContext);
        } catch (TurboException e) {
            if (!ErrorEnum.isSuccess(e.getErrNo())) {
                LOGGER.warn("startProcess ProcessException.||startProcessParam={}||runtimeContext={}, ",
                        startProcessParam, runtimeContext, e);
            }
            return buildStartProcessResult(runtimeContext, e);
        }
    }

    private FlowInfo getFlowInfo(StartProcessParam startProcessParam) throws ProcessException {
        if (StringUtils.isNotBlank(startProcessParam.getFlowDeployId())) {
            return getFlowInfoByFlowDeployId(startProcessParam.getFlowDeployId());
        } else {
            return getFlowInfoByFlowModuleId(startProcessParam.getFlowModuleId());
        }
    }

    /**
     * Init runtimeContext for startProcess:
     * 1.flowInfo: flowDeployId, flowModuleId, tenantId, flowModel(FlowElementList)
     * 2.variables: inputDataList fr. param
     */
    private RuntimeContext buildStartProcessContext(FlowInfo flowInfo, List<InstanceData> variables) {
        return buildRuntimeContext(flowInfo, variables);
    }

    private StartProcessResult buildStartProcessResult(RuntimeContext runtimeContext) {
        StartProcessResult startProcessResult = new StartProcessResult();
        BeanUtils.copyProperties(runtimeContext, startProcessResult);
        return (StartProcessResult) fillRuntimeResult(startProcessResult, runtimeContext);
    }

    private StartProcessResult buildStartProcessResult(RuntimeContext runtimeContext, TurboException e) {
        StartProcessResult startProcessResult = new StartProcessResult();
        BeanUtils.copyProperties(runtimeContext, startProcessResult);
        return (StartProcessResult) fillRuntimeResult(startProcessResult, runtimeContext, e);
    }

    ////////////////////////////////////////commit////////////////////////////////////////

    public CommitTaskResult commit(CommitTaskParam commitTaskParam) {
        RuntimeContext runtimeContext = null;
        try {
            //1.param validate
            ParamValidator.validate(commitTaskParam);

            //2.get flowInstance
            FlowInstanceBO flowInstanceBO = getFlowInstanceBO(commitTaskParam.getFlowInstanceId());
            if (flowInstanceBO == null) {
                LOGGER.warn("commit failed: cannot find flowInstanceBO.||flowInstanceId={}", commitTaskParam.getFlowInstanceId());
                throw new ProcessException(ErrorEnum.GET_FLOW_INSTANCE_FAILED);
            }

            //3.check status
            if (flowInstanceBO.getStatus() == FlowInstanceStatus.TERMINATED) {
                LOGGER.warn("commit failed: flowInstance has been completed.||commitTaskParam={}", commitTaskParam);
                throw new ProcessException(ErrorEnum.COMMIT_REJECTRD);
            }
            if (flowInstanceBO.getStatus() == FlowInstanceStatus.COMPLETED) {
                LOGGER.warn("commit: reentrant process.||commitTaskParam={}", commitTaskParam);
                throw new ReentrantException(ErrorEnum.REENTRANT_WARNING);
            }
            String flowDeployId = flowInstanceBO.getFlowDeployId();

            //4.getFlowInfo
            FlowInfo flowInfo = getFlowInfoByFlowDeployId(flowDeployId);

            //5.init runtimeContext
            runtimeContext = buildCommitContext(commitTaskParam, flowInfo, flowInstanceBO.getStatus());

            //6.process
            flowExecutor.commit(runtimeContext);

            //7.build result
            return buildCommitTaskResult(runtimeContext);
        } catch (TurboException e) {
            if (!ErrorEnum.isSuccess(e.getErrNo())) {
                LOGGER.warn("commit ProcessException.||commitTaskParam={}||runtimeContext={}, ", commitTaskParam, runtimeContext, e);
            }
            return buildCommitTaskResult(runtimeContext, e);
        }
    }

    private RuntimeContext buildCommitContext(CommitTaskParam commitTaskParam, FlowInfo flowInfo, int flowInstanceStatus) {
        //1. set flow info
        RuntimeContext runtimeContext = buildRuntimeContext(flowInfo, commitTaskParam.getVariables());

        //2. init flowInstance with flowInstanceId
        runtimeContext.setFlowInstanceId(commitTaskParam.getFlowInstanceId());
        runtimeContext.setFlowInstanceStatus(flowInstanceStatus);

        //3. set suspendNodeInstance with taskInstance in param
        NodeInstanceBO suspendNodeInstance = new NodeInstanceBO();
        suspendNodeInstance.setNodeInstanceId(commitTaskParam.getTaskInstanceId());
        runtimeContext.setSuspendNodeInstance(suspendNodeInstance);

        return runtimeContext;
    }

    private CommitTaskResult buildCommitTaskResult(RuntimeContext runtimeContext) {
        CommitTaskResult commitTaskResult = new CommitTaskResult();
        return (CommitTaskResult) fillRuntimeResult(commitTaskResult, runtimeContext);
    }

    private CommitTaskResult buildCommitTaskResult(RuntimeContext runtimeContext, TurboException e) {
        CommitTaskResult commitTaskResult = new CommitTaskResult();
        return (CommitTaskResult) fillRuntimeResult(commitTaskResult, runtimeContext, e);
    }

    ////////////////////////////////////////rollback////////////////////////////////////////

    /**
     * Rollback: rollback node process from param.taskInstance to the last taskInstance to suspend
     *
     * @param rollbackTaskParam: flowInstanceId + taskInstanceId(nodeInstanceId)
     * @return rollbackTaskResult: runtimeResult, flowInstanceId + activeTaskInstance(nodeInstanceId,nodeKey,status) + dataMap
     * @throws Exception
     */
    public RollbackTaskResult rollback(RollbackTaskParam rollbackTaskParam) {
        RuntimeContext runtimeContext = null;
        try {
            //1.param validate
            ParamValidator.validate(rollbackTaskParam);

            //2.get flowInstance
            FlowInstanceBO flowInstanceBO = getFlowInstanceBO(rollbackTaskParam.getFlowInstanceId());
            if (flowInstanceBO == null) {
                LOGGER.warn("rollback failed: flowInstanceBO is null.||flowInstanceId={}", rollbackTaskParam.getFlowInstanceId());
                throw new ProcessException(ErrorEnum.GET_FLOW_INSTANCE_FAILED);
            }

            //3.check status
            if (flowInstanceBO.getStatus() != FlowInstanceStatus.RUNNING) {
                LOGGER.warn("rollback failed: invalid status to rollback.||rollbackTaskParam={}||status={}",
                        rollbackTaskParam, flowInstanceBO.getStatus());
                throw new ProcessException(ErrorEnum.ROLLBACK_REJECTRD);
            }
            String flowDeployId = flowInstanceBO.getFlowDeployId();

            //4.getFlowInfo
            FlowInfo flowInfo = getFlowInfoByFlowDeployId(flowDeployId);

            //5.init runtimeContext
            runtimeContext = buildRollbackContext(rollbackTaskParam, flowInfo, flowInstanceBO.getStatus());

            //6.process
            flowExecutor.rollback(runtimeContext);

            //7.build result
            return buildRollbackTaskResult(runtimeContext);
        } catch (TurboException e) {
            if (!ErrorEnum.isSuccess(e.getErrNo())) {
                LOGGER.warn("rollback ProcessException.||rollbackTaskParam={}||runtimeContext={}, ", rollbackTaskParam, runtimeContext, e);
            }
            return buildRollbackTaskResult(runtimeContext, e);
        }
    }

    private RuntimeContext buildRollbackContext(RollbackTaskParam rollbackTaskParam, FlowInfo flowInfo, int flowInstanceStatus) {
        //1. set flow info
        RuntimeContext runtimeContext = buildRuntimeContext(flowInfo);

        //2. init flowInstance with flowInstanceId
        runtimeContext.setFlowInstanceId(rollbackTaskParam.getFlowInstanceId());
        runtimeContext.setFlowInstanceStatus(flowInstanceStatus);

        //3. set suspendNodeInstance with taskInstance in param
        NodeInstanceBO suspendNodeInstance = new NodeInstanceBO();
        suspendNodeInstance.setNodeInstanceId(rollbackTaskParam.getTaskInstanceId());
        runtimeContext.setSuspendNodeInstance(suspendNodeInstance);

        return runtimeContext;
    }

    private RollbackTaskResult buildRollbackTaskResult(RuntimeContext runtimeContext) {
        RollbackTaskResult rollbackTaskResult = new RollbackTaskResult();
        return (RollbackTaskResult) fillRuntimeResult(rollbackTaskResult, runtimeContext);
    }

    private RollbackTaskResult buildRollbackTaskResult(RuntimeContext runtimeContext, TurboException e) {
        RollbackTaskResult rollbackTaskResult = new RollbackTaskResult();
        return (RollbackTaskResult) fillRuntimeResult(rollbackTaskResult, runtimeContext, e);
    }

    ////////////////////////////////////////terminate////////////////////////////////////////

    public TerminateResult terminateProcess(String flowInstanceId) {
        TerminateResult terminateResult;
        try {
            int flowInstanceStatus;

            FlowInstancePO flowInstancePO = processInstanceDAO.selectByFlowInstanceId(flowInstanceId);
            if (flowInstancePO.getStatus() == FlowInstanceStatus.COMPLETED) {
                LOGGER.warn("terminateProcess: flowInstance is completed.||flowInstanceId={}", flowInstanceId);
                flowInstanceStatus = FlowInstanceStatus.COMPLETED;
            } else {
                processInstanceDAO.updateStatus(flowInstancePO, FlowInstanceStatus.TERMINATED);
                flowInstanceStatus = FlowInstanceStatus.TERMINATED;
            }

            terminateResult = new TerminateResult(ErrorEnum.SUCCESS);
            terminateResult.setFlowInstanceId(flowInstanceId);
            terminateResult.setStatus(flowInstanceStatus);
        } catch (Exception e) {
            LOGGER.error("terminateProcess exception.||flowInstanceId={}, ", flowInstanceId, e);
            terminateResult = new TerminateResult(ErrorEnum.SYSTEM_ERROR);
            terminateResult.setFlowInstanceId(flowInstanceId);
        }
        return terminateResult;
    }

    ////////////////////////////////////////getHistoryUserTaskList////////////////////////////////////////

    public NodeInstanceListResult getHistoryUserTaskList(String flowInstanceId) {

        //1.get nodeInstanceList by flowInstanceId order by id desc
        List<NodeInstancePO> historyNodeInstanceList = getDescHistoryNodeInstanceList(flowInstanceId);

        //2.init result
        NodeInstanceListResult historyListResult = new NodeInstanceListResult(ErrorEnum.SUCCESS);
        historyListResult.setNodeInstanceList(Lists.newArrayList());

        try {

            if (CollectionUtils.isEmpty(historyNodeInstanceList)) {
                LOGGER.warn("getHistoryUserTaskList: historyNodeInstanceList is empty.||flowInstanceId={}", flowInstanceId);
                return historyListResult;
            }

            //3.get flow info
            String flowDeployId = historyNodeInstanceList.get(0).getFlowDeployId();
            Map<String, FlowElement> flowElementMap = getFlowElementMap(flowDeployId);

            //4.pick out userTask and build result
            List<NodeInstance> userTaskList = historyListResult.getNodeInstanceList();//empty list

            for (NodeInstancePO nodeInstancePO : historyNodeInstanceList) {
                //ignore noneffective nodeInstance
                if (!isEffectiveNodeInstance(nodeInstancePO.getStatus())) {
                    continue;
                }

                //ignore un-userTask instance
                if (!isUserTask(nodeInstancePO.getNodeKey(), flowElementMap)) {
                    continue;
                }

                //build effective userTask instance
                NodeInstance nodeInstance = new NodeInstance();
                //set instanceId & status
                BeanUtils.copyProperties(nodeInstancePO, nodeInstance);

                //set ElementModel info
                FlowElement flowElement = FlowModelUtil.getFlowElement(flowElementMap, nodeInstancePO.getNodeKey());
                nodeInstance.setModelKey(flowElement.getKey());
                nodeInstance.setModelName(FlowModelUtil.getElementName(flowElement));
                if (MapUtils.isNotEmpty(flowElement.getProperties())) {
                    nodeInstance.setProperties(flowElement.getProperties());
                } else {
                    nodeInstance.setProperties(Maps.newHashMap());
                }
                userTaskList.add(nodeInstance);
            }
        } catch (ProcessException e) {
            historyListResult.setErrCode(e.getErrNo());
            historyListResult.setErrMsg(e.getErrMsg());
        }
        return historyListResult;
    }

    private Map<String, FlowElement> getFlowElementMap(String flowDeployId) throws ProcessException {
        FlowInfo flowInfo = getFlowInfoByFlowDeployId(flowDeployId);
        String flowModel = flowInfo.getFlowModel();
        return FlowModelUtil.getFlowElementMap(flowModel);
    }

    private boolean isEffectiveNodeInstance(int status) {
        return status == NodeInstanceStatus.COMPLETED || status == NodeInstanceStatus.ACTIVE;
    }

    private boolean isUserTask(String nodeKey, Map<String, FlowElement> flowElementMap) throws ProcessException {
        if (!flowElementMap.containsKey(nodeKey)) {
            LOGGER.warn("isUserTask: invalid nodeKey which is not in flowElementMap.||nodeKey={}||flowElementMap={}",
                    nodeKey, flowElementMap);
            throw new ProcessException(ErrorEnum.GET_NODE_FAILED);
        }
        FlowElement flowElement = flowElementMap.get(nodeKey);
        return flowElement.getType() == FlowElementType.USER_TASK;
    }

    ////////////////////////////////////////getHistoryElementList////////////////////////////////////////

    public ElementInstanceListResult getHistoryElementList(String flowInstanceId) {
        //1.getHistoryNodeList
        List<NodeInstancePO> historyNodeInstanceList = getHistoryNodeInstanceList(flowInstanceId);

        //2.init
        ElementInstanceListResult elementInstanceListResult = new ElementInstanceListResult(ErrorEnum.SUCCESS);
        elementInstanceListResult.setElementInstanceList(Lists.newArrayList());

        try {
            if (CollectionUtils.isEmpty(historyNodeInstanceList)) {
                LOGGER.warn("getHistoryElementList: historyNodeInstanceList is empty.||flowInstanceId={}", flowInstanceId);
                return elementInstanceListResult;
            }

            //3.get flow info
            String flowDeployId = historyNodeInstanceList.get(0).getFlowDeployId();
            Map<String, FlowElement> flowElementMap = getFlowElementMap(flowDeployId);

            //4.calculate elementInstanceMap: key=elementKey, value(lasted)=ElementInstance(elementKey, status)
            List<ElementInstance> elementInstanceList = elementInstanceListResult.getElementInstanceList();
            for (NodeInstancePO nodeInstancePO : historyNodeInstanceList) {
                String nodeKey = nodeInstancePO.getNodeKey();
                String sourceNodeKey = nodeInstancePO.getSourceNodeKey();
                int nodeStatus = nodeInstancePO.getStatus();

                //4.1 build the source sequenceFlow instance
                if (StringUtils.isNotBlank(sourceNodeKey)) {
                    FlowElement sourceFlowElement = FlowModelUtil.getSequenceFlow(flowElementMap, sourceNodeKey, nodeKey);
                    if (sourceFlowElement == null) {
                        LOGGER.error("getHistoryElementList failed: sourceFlowElement is null."
                                + "||nodeKey={}||sourceNodeKey={}||flowElementMap={}", nodeKey, sourceNodeKey, flowElementMap);
                        throw new ProcessException(ErrorEnum.MODEL_UNKNOWN_ELEMENT_KEY);
                    }

                    //build ElementInstance
                    int sourceSequenceFlowStatus = nodeStatus;
                    if (nodeStatus == NodeInstanceStatus.ACTIVE) {
                        sourceSequenceFlowStatus = NodeInstanceStatus.COMPLETED;
                    }
                    ElementInstance sequenceFlowInstance = new ElementInstance(sourceFlowElement.getKey(), sourceSequenceFlowStatus);
                    elementInstanceList.add(sequenceFlowInstance);
                }

                //4.2 build nodeInstance
                ElementInstance nodeInstance = new ElementInstance(nodeKey, nodeStatus);
                elementInstanceList.add(nodeInstance);
            }
        } catch (ProcessException e) {
            elementInstanceListResult.setErrCode(e.getErrNo());
            elementInstanceListResult.setErrMsg(e.getErrMsg());
        }
        return elementInstanceListResult;
    }

    private List<NodeInstancePO> getHistoryNodeInstanceList(String flowInstanceId) {
        return nodeInstanceDAO.selectByFlowInstanceId(flowInstanceId);
    }

    private List<NodeInstancePO> getDescHistoryNodeInstanceList(String flowInstanceId) {
        return nodeInstanceDAO.selectDescByFlowInstanceId(flowInstanceId);
    }

    public NodeInstanceResult getNodeInstance(String flowInstanceId, String nodeInstanceId) {
        NodeInstanceResult nodeInstanceResult = new NodeInstanceResult();
        try {
            NodeInstancePO nodeInstancePO = nodeInstanceDAO.selectByNodeInstanceId(flowInstanceId, nodeInstanceId);
            String flowDeployId = nodeInstancePO.getFlowDeployId();
            Map<String, FlowElement> flowElementMap = getFlowElementMap(flowDeployId);
            NodeInstance nodeInstance = new NodeInstance();
            BeanUtils.copyProperties(nodeInstancePO, nodeInstance);
            FlowElement flowElement = FlowModelUtil.getFlowElement(flowElementMap, nodeInstancePO.getNodeKey());
            nodeInstance.setModelKey(flowElement.getKey());
            nodeInstance.setModelName(FlowModelUtil.getElementName(flowElement));
            if (MapUtils.isNotEmpty(flowElement.getProperties())) {
                nodeInstance.setProperties(flowElement.getProperties());
            } else {
                nodeInstance.setProperties(Maps.newHashMap());
            }
            nodeInstanceResult.setNodeInstance(nodeInstance);
            nodeInstanceResult.setErrCode(ErrorEnum.SUCCESS.getErrNo());
            nodeInstanceResult.setErrMsg(ErrorEnum.SUCCESS.getErrMsg());
        } catch (ProcessException e) {
            nodeInstanceResult.setErrCode(e.getErrNo());
            nodeInstanceResult.setErrMsg(e.getErrMsg());
        }
        return nodeInstanceResult;
    }

    ////////////////////////////////////////getInstanceData////////////////////////////////////////
    public InstanceDataListResult getInstanceData(String flowInstanceId) {
        InstanceDataPO instanceDataPO = instanceDataDAO.selectRecentOne(flowInstanceId);

        TypeReference<List<InstanceData>> typeReference = new TypeReference<List<InstanceData>>() {
        };
        List<InstanceData> instanceDataList = JSONObject.parseObject(instanceDataPO.getInstanceData(), typeReference);
        if (CollectionUtils.isEmpty(instanceDataList)) {
            instanceDataList =  Lists.newArrayList();
        }

        InstanceDataListResult instanceDataListResult = new InstanceDataListResult(ErrorEnum.SUCCESS);
        instanceDataListResult.setVariables(instanceDataList);
        return instanceDataListResult;
    }

    ////////////////////////////////////////common////////////////////////////////////////

    private FlowInfo getFlowInfoByFlowDeployId(String flowDeployId) throws ProcessException {

        FlowDeploymentPO flowDeploymentPO = flowDeploymentDAO.selectByDeployId(flowDeployId);
        if (flowDeploymentPO == null) {
            LOGGER.warn("getFlowInfoByFlowDeployId failed.||flowDeployId={}", flowDeployId);
            throw new ProcessException(ErrorEnum.GET_FLOW_DEPLOYMENT_FAILED);
        }
        FlowInfo flowInfo = new FlowInfo();
        BeanUtils.copyProperties(flowDeploymentPO, flowInfo);

        return flowInfo;
    }

    private FlowInfo getFlowInfoByFlowModuleId(String flowModuleId) throws ProcessException {
        //get from db directly
        FlowDeploymentPO flowDeploymentPO = flowDeploymentDAO.selectRecentByFlowModuleId(flowModuleId);
        if (flowDeploymentPO == null) {
            LOGGER.warn("getFlowInfoByFlowModuleId failed.||flowModuleId={}", flowModuleId);
            throw new ProcessException(ErrorEnum.GET_FLOW_DEPLOYMENT_FAILED);
        }

        FlowInfo flowInfo = new FlowInfo();
        BeanUtils.copyProperties(flowDeploymentPO, flowInfo);

        return flowInfo;
    }

    private FlowInstanceBO getFlowInstanceBO(String flowInstanceId) throws ProcessException {
        //get from db
        FlowInstancePO flowInstancePO = processInstanceDAO.selectByFlowInstanceId(flowInstanceId);
        if (flowInstancePO == null) {
            LOGGER.warn("getFlowInstancePO failed: cannot find flowInstancePO from db.||flowInstanceId={}", flowInstanceId);
            throw new ProcessException(ErrorEnum.GET_FLOW_INSTANCE_FAILED);
        }
        FlowInstanceBO flowInstanceBO = new FlowInstanceBO();
        BeanUtils.copyProperties(flowInstancePO, flowInstanceBO);

        return flowInstanceBO;
    }

    private RuntimeContext buildRuntimeContext(FlowInfo flowInfo) {
        RuntimeContext runtimeContext = new RuntimeContext();
        BeanUtils.copyProperties(flowInfo, runtimeContext);
        runtimeContext.setFlowElementMap(FlowModelUtil.getFlowElementMap(flowInfo.getFlowModel()));
        return runtimeContext;
    }

    private RuntimeContext buildRuntimeContext(FlowInfo flowInfo, List<InstanceData> variables) {
        RuntimeContext runtimeContext = buildRuntimeContext(flowInfo);
        Map<String, InstanceData> instanceDataMap = InstanceDataUtil.getInstanceDataMap(variables);
        runtimeContext.setInstanceDataMap(instanceDataMap);
        return runtimeContext;
    }

    private RuntimeResult fillRuntimeResult(RuntimeResult runtimeResult, RuntimeContext runtimeContext) {
        if (runtimeContext.getProcessStatus() == ProcessStatus.SUCCESS) {
            return fillRuntimeResult(runtimeResult, runtimeContext, ErrorEnum.SUCCESS);
        }
        return fillRuntimeResult(runtimeResult, runtimeContext, ErrorEnum.FAILED);
    }

    private RuntimeResult fillRuntimeResult(RuntimeResult runtimeResult, RuntimeContext runtimeContext, ErrorEnum errorEnum) {
        return fillRuntimeResult(runtimeResult, runtimeContext, errorEnum.getErrNo(), errorEnum.getErrMsg());
    }

    private RuntimeResult fillRuntimeResult(RuntimeResult runtimeResult, RuntimeContext runtimeContext, TurboException e) {
        return fillRuntimeResult(runtimeResult, runtimeContext, e.getErrNo(), e.getErrMsg());
    }

    private RuntimeResult fillRuntimeResult(RuntimeResult runtimeResult, RuntimeContext runtimeContext, int errNo, String errMsg) {
        runtimeResult.setErrCode(errNo);
        runtimeResult.setErrMsg(errMsg);

        if (runtimeContext != null) {
            runtimeResult.setFlowInstanceId(runtimeContext.getFlowInstanceId());
            runtimeResult.setStatus(runtimeContext.getFlowInstanceStatus());
            runtimeResult.setActiveTaskInstance(buildActiveTaskInstance(runtimeContext.getSuspendNodeInstance(), runtimeContext));
            runtimeResult.setVariables(InstanceDataUtil.getInstanceDataList(runtimeContext.getInstanceDataMap()));
        }
        return runtimeResult;
    }

    private NodeInstance buildActiveTaskInstance(NodeInstanceBO nodeInstanceBO, RuntimeContext runtimeContext) {
        NodeInstance activeNodeInstance = new NodeInstance();
        BeanUtils.copyProperties(nodeInstanceBO, activeNodeInstance);
        activeNodeInstance.setModelKey(nodeInstanceBO.getNodeKey());
        FlowElement flowElement = runtimeContext.getFlowElementMap().get(nodeInstanceBO.getNodeKey());
        activeNodeInstance.setModelName(FlowModelUtil.getElementName(flowElement));
        activeNodeInstance.setProperties(flowElement.getProperties());

        return activeNodeInstance;
    }
}
