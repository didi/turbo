package com.xiaoju.uemc.turbo.engine.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaoju.uemc.modules.support.jedis.RedisUtils;
import com.xiaoju.uemc.turbo.engine.bo.FlowInfo;
import com.xiaoju.uemc.turbo.engine.bo.FlowInstanceBO;
import com.xiaoju.uemc.turbo.engine.bo.NodeInstanceBO;
import com.xiaoju.uemc.turbo.engine.common.*;
import com.xiaoju.uemc.turbo.engine.dao.FlowDeploymentDAO;
import com.xiaoju.uemc.turbo.engine.dao.InstanceDataDAO;
import com.xiaoju.uemc.turbo.engine.dao.NodeInstanceDAO;
import com.xiaoju.uemc.turbo.engine.dao.ProcessInstanceDAO;
import com.xiaoju.uemc.turbo.engine.dto.*;
import com.xiaoju.uemc.turbo.engine.entity.FlowDeploymentPO;
import com.xiaoju.uemc.turbo.engine.entity.FlowInstancePO;
import com.xiaoju.uemc.turbo.engine.entity.InstanceDataPO;
import com.xiaoju.uemc.turbo.engine.entity.NodeInstancePO;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import com.xiaoju.uemc.turbo.engine.exception.ReentrantException;
import com.xiaoju.uemc.turbo.engine.executor.FlowExecutor;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.CommitTaskParam;
import com.xiaoju.uemc.turbo.engine.param.RecallTaskParam;
import com.xiaoju.uemc.turbo.engine.param.StartProcessParam;
import com.xiaoju.uemc.turbo.engine.util.FlowModelUtil;
import com.xiaoju.uemc.turbo.engine.util.InstanceDataUtil;
import com.xiaoju.uemc.turbo.engine.validator.ParamValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by Stefanie on 2019/12/1.
 */
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


    private final RedisUtils redisClient = RedisUtils.getInstance();

    ////////////////////////////////////////startProcess////////////////////////////////////////

    /**
     * start process entry
     *
     * @param startProcessParam param
     * @return
     * @throws Exception
     */
    public StartProcessDTO startProcess(StartProcessParam startProcessParam) throws Exception {
        RuntimeContext runtimeContext = null;
        try {
            //1.param validate
            ParamValidator.validate(startProcessParam);

            //2.getFlowInfo
            FlowInfo flowInfo = getFlowInfo(startProcessParam);

            //3.init context for runtime, but not all data
            runtimeContext = buildStartProcessContext(flowInfo, startProcessParam.getVariables());

            //4.process
            flowExecutor.execute(runtimeContext);

            //5.build result
            return buildStartProcessDTO(runtimeContext);
        } catch (ProcessException e) {
            if (!ErrorEnum.isSuccess(e.getErrNo())) {
                LOGGER.warn("startProcess ProcessException.||startProcessParam={}||runtimeContext={}, ",
                        startProcessParam, runtimeContext, e);
            }
            return buildStartProcessDTO(runtimeContext, e);
        }
    }

    /**
     * get flowInfo from db by using flowDeployId or flowModuleId.
     * We use flowDeployId first.
     *
     * @param startProcessParam
     * @return flowInfo
     * @throws Exception
     */
    private FlowInfo getFlowInfo(StartProcessParam startProcessParam) throws Exception {
        if (StringUtils.isNotBlank(startProcessParam.getFlowDeployId())) {
            return getFlowInfoByFlowDeployId(startProcessParam.getFlowDeployId());
        } else {
            return getFlowInfoByFlowModuleId(startProcessParam.getFlowModuleId());
        }
    }

    /**
     * Init runtimeContext for startProcess
     *
     * @param flowInfo flowDeployId, flowModuleId, flowModel(FlowElementList), tenant, caller
     * @param variables inputDataList fr. param
     * @return runtime context
     */
    private RuntimeContext buildStartProcessContext(FlowInfo flowInfo, List<InstanceData> variables) {
        return buildRuntimeContext(flowInfo, variables);
    }

    private StartProcessDTO buildStartProcessDTO(RuntimeContext runtimeContext) {
        StartProcessDTO startProcessDTO = new StartProcessDTO();
        BeanUtils.copyProperties(runtimeContext, startProcessDTO);
        return (StartProcessDTO) fillRuntimeDTO(startProcessDTO, runtimeContext);
    }

    private StartProcessDTO buildStartProcessDTO(RuntimeContext runtimeContext, ProcessException e) {
        StartProcessDTO startProcessDTO = new StartProcessDTO();
        BeanUtils.copyProperties(runtimeContext, startProcessDTO);
        return (StartProcessDTO) fillRuntimeDTO(startProcessDTO, runtimeContext, e);
    }

    ////////////////////////////////////////commit////////////////////////////////////////

    /**
     * commit user task
     * only user task can commit
     *
     * @param commitTaskParam
     * @return
     * @throws Exception
     */
    public CommitTaskDTO commit(CommitTaskParam commitTaskParam) throws Exception {
        RuntimeContext runtimeContext = null;
        try {
            //1.param validate
            ParamValidator.validate(commitTaskParam);

            //2.get flowInstance from cache or db
            FlowInstanceBO flowInstanceBO = getFlowInstanceBO(commitTaskParam.getFlowInstanceId());
            if (flowInstanceBO == null) {
                LOGGER.warn("commit failed: cannot find flowInstanceBO.||flowInstanceId={}", commitTaskParam.getFlowInstanceId());
                throw new ProcessException(ErrorEnum.GET_FLOW_INSTANCE_FAILED);
            }

            //3.check status, FlowInstanceStatus not allow terminated or completed
            if (flowInstanceBO.getStatus() == FlowInstanceStatus.TERMINATED) {
                LOGGER.warn("commit failed: flowInstance has been completed.||commitTaskParam={}", commitTaskParam);
                throw new ProcessException(ErrorEnum.TERMINATE_CANNOT_COMMIT);
            }
            if (flowInstanceBO.getStatus() == FlowInstanceStatus.COMPLETED) {
                LOGGER.warn("commit: reentrant process.||commitTaskParam={}", commitTaskParam);
                throw new ReentrantException(ErrorEnum.REENTRANT_WARNING);
            }
            String flowDeployId = flowInstanceBO.getFlowDeployId();

            //4.get flowInfo from db
            FlowInfo flowInfo = getFlowInfoByFlowDeployId(flowDeployId);

            //5.init runtimeContext
            runtimeContext = buildCommitContext(commitTaskParam, flowInfo, flowInstanceBO.getStatus());

            //6.process
            flowExecutor.commit(runtimeContext);

            //7.build result
            return buildCommitTaskDTO(runtimeContext);
        } catch (ProcessException e) {
            if (!ErrorEnum.isSuccess(e.getErrNo())) {
                LOGGER.warn("commit ProcessException.||commitTaskParam={}||runtimeContext={}, ", commitTaskParam, runtimeContext, e);
            }
            return buildCommitTaskDTO(runtimeContext, e);
        }
    }

    /**
     * build commit context
     * set flowInstance, suspendNodeInstance
     * @param commitTaskParam
     * @param flowInfo
     * @param flowInstanceStatus
     * @return
     */
    private RuntimeContext buildCommitContext(CommitTaskParam commitTaskParam, FlowInfo flowInfo, int flowInstanceStatus) {
        //1. set flow info
        RuntimeContext runtimeContext = buildRuntimeContext(flowInfo, commitTaskParam.getVariables());

        //2. init flowInstance with flowInstanceId
        runtimeContext.setFlowInstanceId(commitTaskParam.getFlowInstanceId());
        runtimeContext.setFlowInstanceStatus(flowInstanceStatus);

        //3. set suspendNodeInstance with taskInstanceId.
        // Notice, this is a empty object unless taskInstanceId, so we will query db to fill object later
        NodeInstanceBO suspendNodeInstance = new NodeInstanceBO();
        suspendNodeInstance.setNodeInstanceId(commitTaskParam.getTaskInstanceId());
        runtimeContext.setSuspendNodeInstance(suspendNodeInstance);

        return runtimeContext;
    }

    private CommitTaskDTO buildCommitTaskDTO(RuntimeContext runtimeContext) {
        CommitTaskDTO commitTaskDTO = new CommitTaskDTO();
        return (CommitTaskDTO) fillRuntimeDTO(commitTaskDTO, runtimeContext);
    }

    private CommitTaskDTO buildCommitTaskDTO(RuntimeContext runtimeContext, ProcessException e) {
        CommitTaskDTO commitTaskDTO = new CommitTaskDTO();
        return (CommitTaskDTO) fillRuntimeDTO(commitTaskDTO, runtimeContext, e);
    }

    ////////////////////////////////////////recall////////////////////////////////////////

    /**
     * Recall: rollback node process from param.taskInstance to the last taskInstance to suspend
     *
     * @param recallTaskParam: flowInstanceId + taskInstanceId(nodeInstanceId)
     * @return recallTaskDTO: runtimeDTO, flowInstanceId + activeTaskInstance(nodeInstanceId,nodeKey,status) + dataMap
     * @throws Exception
     */
    public RecallTaskDTO recall(RecallTaskParam recallTaskParam) throws Exception {
        RuntimeContext runtimeContext = null;
        try {
            //1.param validate
            ParamValidator.validate(recallTaskParam);

            //2.get flowInstance
            FlowInstanceBO flowInstanceBO = getFlowInstanceBO(recallTaskParam.getFlowInstanceId());
            if (flowInstanceBO == null) {
                LOGGER.warn("recall failed: flowInstanceBO is null.||flowInstanceId={}", recallTaskParam.getFlowInstanceId());
                throw new ProcessException(ErrorEnum.GET_FLOW_INSTANCE_FAILED);
            }

            //3.check status
            if (flowInstanceBO.getStatus() != FlowInstanceStatus.RUNNING) {
                LOGGER.warn("recall failed: invalid status to recall.||recallTaskParam={}||status={}", recallTaskParam, flowInstanceBO.getStatus());
                throw new ProcessException(ErrorEnum.FLOW_INSTANCE_CANNOT_ROLLBACK);
            }
            String flowDeployId = flowInstanceBO.getFlowDeployId();

            //4.getFlowInfo
            FlowInfo flowInfo = getFlowInfoByFlowDeployId(flowDeployId);

            //5.init runtimeContext
            runtimeContext = buildRecallContext(recallTaskParam, flowInfo, flowInstanceBO.getStatus());

            //6.process
            flowExecutor.rollback(runtimeContext);

            //7.build result
            return buildRecallTaskDTO(runtimeContext);
        } catch (ProcessException e) {
            if (!ErrorEnum.isSuccess(e.getErrNo())) {
                LOGGER.warn("recall ProcessException.||recallTaskParam={}||runtimeContext={}, ", recallTaskParam, runtimeContext, e);
            }
            return buildRecallTaskDTO(runtimeContext, e);
        }
    }

    private RuntimeContext buildRecallContext(RecallTaskParam recallTaskParam, FlowInfo flowInfo, int flowInstanceStatus) {
        //1. set flow info
        RuntimeContext runtimeContext = buildRuntimeContext(flowInfo);

        //2. init flowInstance with flowInstanceId
        runtimeContext.setFlowInstanceId(recallTaskParam.getFlowInstanceId());
        runtimeContext.setFlowInstanceStatus(flowInstanceStatus);

        //3. set suspendNodeInstance with taskInstance in param
        NodeInstanceBO suspendNodeInstance = new NodeInstanceBO();
        suspendNodeInstance.setNodeInstanceId(recallTaskParam.getTaskInstanceId());
        runtimeContext.setSuspendNodeInstance(suspendNodeInstance);

        return runtimeContext;
    }

    private RecallTaskDTO buildRecallTaskDTO(RuntimeContext runtimeContext) {
        RecallTaskDTO recallTaskDTO = new RecallTaskDTO();
        return (RecallTaskDTO) fillRuntimeDTO(recallTaskDTO, runtimeContext);
    }

    private RecallTaskDTO buildRecallTaskDTO(RuntimeContext runtimeContext, ProcessException e) {
        RecallTaskDTO recallTaskDTO = new RecallTaskDTO();
        return (RecallTaskDTO) fillRuntimeDTO(recallTaskDTO, runtimeContext, e);
    }

    ////////////////////////////////////////terminate////////////////////////////////////////

    /**
     * force terminate process
     *
     * @param flowInstanceId
     * @return
     * @throws Exception
     */
    public TerminateDTO terminateProcess(String flowInstanceId) throws Exception {
        TerminateDTO terminateDTO;
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

            terminateDTO = new TerminateDTO(ErrorEnum.SUCCESS);
            terminateDTO.setFlowInstanceId(flowInstanceId);
            terminateDTO.setStatus(flowInstanceStatus);
        } catch (Exception e) {
            LOGGER.error("terminateProcess exception.||flowInstanceId={}, ", flowInstanceId, e);
            terminateDTO = new TerminateDTO(ErrorEnum.SYSTEM_ERROR);
            terminateDTO.setFlowInstanceId(flowInstanceId);
        } finally {
            //the status is unknown while exception occurs, clear it as well
            redisClient.del(RedisConstants.FLOW_INSTANCE + flowInstanceId);
        }
        return terminateDTO;
    }

    ////////////////////////////////////////updateInstanceData////////////////////////////////////////

    /**
     * ？？？
     * @param flowInstanceId
     * @param dataMap
     * @return
     * @throws Exception
     */
    public CommonDTO updateInstanceData(String flowInstanceId, Map<String, InstanceData> dataMap) throws Exception {
        InstanceDataPO instanceDataPO = instanceDataDAO.selectRecentOne(flowInstanceId);
        CommonDTO commonDTO = new CommonDTO(ErrorEnum.SUCCESS);
        return commonDTO;
    }

    ////////////////////////////////////////getHistoryUserTaskList////////////////////////////////////////

    /**
     * get user task list by flowInstanceId desc
     * @param flowInstanceId
     * @return
     * @throws Exception
     */
    public NodeInstanceListDTO getHistoryUserTaskList(String flowInstanceId) throws Exception {

        //1.get nodeInstanceList by flowInstanceId order by id desc
        List<NodeInstancePO> historyNodeInstanceList = getDescHistoryNodeInstanceList(flowInstanceId);

        //2.init dto
        NodeInstanceListDTO historyListDTO = new NodeInstanceListDTO(ErrorEnum.SUCCESS);
        historyListDTO.setNodeInstanceDTOList(Lists.newArrayList());
        if (CollectionUtils.isEmpty(historyNodeInstanceList)) {
            LOGGER.warn("getHistoryUserTaskList: historyNodeInstanceList is empty.||flowInstanceId={}", flowInstanceId);
            return historyListDTO;
        }

        //3.get flow info
        String flowDeployId = historyNodeInstanceList.get(0).getFlowDeployId();
        Map<String, FlowElement> flowElementMap = getFlowElementMap(flowDeployId);

        //4.pick out userTask and build dto
        List<NodeInstanceDTO> userTaskDTOList = historyListDTO.getNodeInstanceDTOList();//empty list

        for (NodeInstancePO nodeInstancePO : historyNodeInstanceList) {
            //ignore noneffective nodeInstance
            if (!isEffectiveNodeInstance(nodeInstancePO.getStatus())) {
                continue;
            }

            //ignore un-userTask instance
            if (!isUserTask(nodeInstancePO.getNodeKey(), flowElementMap)) {
                continue;
            }

            //build effective userTask instance DTO
            NodeInstanceDTO nodeInstanceDTO = new NodeInstanceDTO();
            //set instanceId & status
            BeanUtils.copyProperties(nodeInstancePO, nodeInstanceDTO);

            //set ElementModel info
            FlowElement flowElement = FlowModelUtil.getFlowElement(flowElementMap, nodeInstancePO.getNodeKey());
            nodeInstanceDTO.setModelKey(flowElement.getKey());
            nodeInstanceDTO.setModelName(FlowModelUtil.getElementName(flowElement));
            if (MapUtils.isNotEmpty(flowElement.getProperties())) {
                nodeInstanceDTO.setProperties(flowElement.getProperties());
            } else {
                nodeInstanceDTO.setProperties(Maps.newHashMap());
            }
            userTaskDTOList.add(nodeInstanceDTO);
        }

        return historyListDTO;
    }

    /**
     * get flowElement map by flowDeployId
     * @param flowDeployId
     * @return
     * @throws Exception
     */
    private Map<String, FlowElement> getFlowElementMap(String flowDeployId) throws Exception {
        FlowInfo flowInfo = getFlowInfoByFlowDeployId(flowDeployId);
        String flowModel = flowInfo.getFlowModel();
        return FlowModelUtil.getFlowElementMap(flowModel);
    }

    /**
     * completed or active is effective only
     * @param status
     * @return
     */
    private boolean isEffectiveNodeInstance(int status) {
        return status == NodeInstanceStatus.COMPLETED || status == NodeInstanceStatus.ACTIVE;
    }

    /**
     * check exist and judge type is user task or not
     *
     * @param nodeKey
     * @param flowElementMap
     * @return
     * @throws Exception
     */
    private boolean isUserTask(String nodeKey, Map<String, FlowElement> flowElementMap) throws Exception {
        if (!flowElementMap.containsKey(nodeKey)) {
            LOGGER.warn("isUserTask: invalid nodeKey which is not in flowElementMap.||nodeKey={}||flowElementMap={}",
                    nodeKey, flowElementMap);
            throw new ProcessException(ErrorEnum.GET_NODE_FAILED);
        }
        FlowElement flowElement = flowElementMap.get(nodeKey);
        return flowElement.getType() == FlowElementType.USER_TASK;
    }

    ////////////////////////////////////////getHistoryElementList////////////////////////////////////////

    /**
     * get history element list by flowInstanceId in order to see snapshot
     * because our db don't save sequenceFlow, so there wo will calculate sequenceFlow between sourceNode and targetNode
     * @param flowInstanceId
     * @return
     * @throws Exception
     */
    public ElementInstanceListDTO getHistoryElementList(String flowInstanceId) throws Exception {
        //1.getHistoryNodeList
        List<NodeInstancePO> historyNodeInstanceList = getHistoryNodeInstanceList(flowInstanceId);

        //2.init DTO
        ElementInstanceListDTO elementInstanceListDTO = new ElementInstanceListDTO(ErrorEnum.SUCCESS);
        elementInstanceListDTO.setElementInstanceDTOList(Lists.newArrayList());

        if (CollectionUtils.isEmpty(historyNodeInstanceList)) {
            LOGGER.warn("getHistoryElementList: historyNodeInstanceList is empty.||flowInstanceId={}", flowInstanceId);
            return elementInstanceListDTO;
        }

        //3.get flow info
        String flowDeployId = historyNodeInstanceList.get(0).getFlowDeployId();
        Map<String, FlowElement> flowElementMap = getFlowElementMap(flowDeployId);

        //4.calculate elementInstanceMap: key=elementKey, value(lasted)=ElementInstanceDTO(elementKey, status)
        List<ElementInstanceDTO> elementInstanceDTOList = elementInstanceListDTO.getElementInstanceDTOList();
        for (NodeInstancePO nodeInstancePO : historyNodeInstanceList) {
            String nodeKey = nodeInstancePO.getNodeKey();
            String sourceNodeKey = nodeInstancePO.getSourceNodeKey();
            int nodeStatus = nodeInstancePO.getStatus();

            //4.1 build the source sequenceFlow instance DTO
            if (StringUtils.isNotBlank(sourceNodeKey)) {
                FlowElement sourceFlowElement = FlowModelUtil.getSequenceFlow(flowElementMap, sourceNodeKey, nodeKey);
                if (sourceFlowElement == null) {
                    LOGGER.error("getHistoryElementList failed: sourceFlowElement is null."
                            + "||nodeKey={}||sourceNodeKey={}||flowElementMap={}", nodeKey, sourceNodeKey, flowElementMap);
                    throw new ProcessException(ErrorEnum.MODEL_UNKNOWN_ELEMENT_KEY);
                }

                //build ElementInstanceDTO
                int sourceSequenceFlowStatus = nodeStatus;
                if (nodeStatus == NodeInstanceStatus.ACTIVE) {
                    sourceSequenceFlowStatus = NodeInstanceStatus.COMPLETED;
                }
                ElementInstanceDTO sequenceFlowInstanceDTO = new ElementInstanceDTO(sourceFlowElement.getKey(), sourceSequenceFlowStatus);
                elementInstanceDTOList.add(sequenceFlowInstanceDTO);
            }

            //4.2 build nodeInstance DTO
            ElementInstanceDTO nodeInstanceDTO = new ElementInstanceDTO(nodeKey, nodeStatus);
            elementInstanceDTOList.add(nodeInstanceDTO);
        }

        return elementInstanceListDTO;
    }

    /**
     * query nodeInstanceList from db by flowInstanceId
     * the order is asc
     *
     * @param flowInstanceId
     * @return
     * @throws Exception
     */
    private List<NodeInstancePO> getHistoryNodeInstanceList(String flowInstanceId) throws Exception {
        return nodeInstanceDAO.selectByFlowInstanceId(flowInstanceId);
    }

    /**
     * query nodeInstanceList from db by flowInstanceId
     * the order is desc
     *
     * @param flowInstanceId
     * @return
     * @throws Exception
     */
    private List<NodeInstancePO> getDescHistoryNodeInstanceList(String flowInstanceId) throws Exception {
        return nodeInstanceDAO.selectDescByFlowInstanceId(flowInstanceId);
    }

    /**
     * get node instance by flowInstanceId and nodeInstanceId
     *
     * @param flowInstanceId
     * @param nodeInstanceId
     * @return
     * @throws Exception
     */
    public NodeInstanceDTO getNodeInstance(String flowInstanceId, String nodeInstanceId) throws Exception {
        NodeInstancePO nodeInstancePO = nodeInstanceDAO.selectByNodeInstanceId(flowInstanceId, nodeInstanceId);
        String flowDeployId = nodeInstancePO.getFlowDeployId();
        Map<String, FlowElement> flowElementMap = getFlowElementMap(flowDeployId);
        NodeInstanceDTO nodeInstanceDTO = new NodeInstanceDTO();
        BeanUtils.copyProperties(nodeInstancePO, nodeInstanceDTO);
        FlowElement flowElement = FlowModelUtil.getFlowElement(flowElementMap, nodeInstancePO.getNodeKey());
        nodeInstanceDTO.setModelKey(flowElement.getKey());
        nodeInstanceDTO.setModelName(FlowModelUtil.getElementName(flowElement));
        if (MapUtils.isNotEmpty(flowElement.getProperties())) {
            nodeInstanceDTO.setProperties(flowElement.getProperties());
        } else {
            nodeInstanceDTO.setProperties(Maps.newHashMap());
        }
        return nodeInstanceDTO;
    }

    ////////////////////////////////////////getInstanceData////////////////////////////////////////

    /**
     * get most new InstanceDataList by flowInstanceId
     * @param flowInstanceId
     * @return
     * @throws Exception
     */
    public List<InstanceData> getInstanceData(String flowInstanceId) throws Exception {
        InstanceDataPO instanceDataPO = instanceDataDAO.selectRecentOne(flowInstanceId);

        TypeReference<List<InstanceData>> typeReference = new TypeReference<List<InstanceData>>() {
        };
        List<InstanceData> instanceDataList = JSONObject.parseObject(instanceDataPO.getInstanceData(), typeReference);
        if (CollectionUtils.isEmpty(instanceDataList)) {
            return Lists.newArrayList();
        }

        return instanceDataList;
    }

    ////////////////////////////////////////common////////////////////////////////////////

    /**
     * get flowDeploymentPO by using flowDeployId
     * and copy flowDeploymentPO' properties to flowInfo by BeanUtils
     * query cache first, if data is null, query db.
     *
     * @param flowDeployId
     * @return
     * @throws Exception
     */
    private FlowInfo getFlowInfoByFlowDeployId(String flowDeployId) throws Exception {
//        //get from cache firstly
//        String redisKey = RedisConstants.FLOW_INFO + flowDeployId;
//        String flowInfoStr = redisClient.get(redisKey);
//        if (StringUtils.isNotBlank(flowInfoStr)) {
//            //TODO:临时逻辑,兼容redis中存的数据是+tenant/caller字段之前的老数据,到2020/10/25可下掉
//            FlowInfo flowInfo = JSONObject.parseObject(flowInfoStr, FlowInfo.class);
//            if (StringUtils.isNotBlank(flowInfo.getTenant()) && StringUtils.isNotBlank(flowInfo.getCaller())) {
//                LOGGER.info("getFlowInfoByFlowDeployId from cache.||flowDeployId={}", flowDeployId);
//                return flowInfo;
//            }
//        }

        //get from db
        FlowDeploymentPO flowDeploymentPO = flowDeploymentDAO.selectByDeployId(flowDeployId);
        if (flowDeploymentPO == null) {
            LOGGER.warn("getFlowInfoByFlowDeployId failed.||flowDeployId={}", flowDeployId);
            throw new ProcessException(ErrorEnum.GET_FLOW_DEPLOYMENT_FAILED);
        }
        FlowInfo flowInfo = new FlowInfo();
        BeanUtils.copyProperties(flowDeploymentPO, flowInfo);

        //set into cache
//        redisClient.setex(redisKey, RedisConstants.FLOW_EXPIRED_SECOND, JSON.toJSONString(flowInfo));
        return flowInfo;
    }

    /**
     * get recent flowDeploymentPO by using flowModuleId
     * and copy flowDeploymentPO' properties to flowInfo by BeanUtils
     * final, set data to cache with expire time
     *
     * @param flowModuleId
     * @return
     * @throws Exception
     */
    private FlowInfo getFlowInfoByFlowModuleId(String flowModuleId) throws Exception {
        //get from db directly
        FlowDeploymentPO flowDeploymentPO = flowDeploymentDAO.selectRecentByFlowModuleId(flowModuleId);
        if (flowDeploymentPO == null) {
            LOGGER.warn("getFlowInfoByFlowModuleId failed.||flowModuleId={}", flowModuleId);
            throw new ProcessException(ErrorEnum.GET_FLOW_DEPLOYMENT_FAILED);
        }

        FlowInfo flowInfo = new FlowInfo();
        BeanUtils.copyProperties(flowDeploymentPO, flowInfo);

        //set into cache
        redisClient.setex(RedisConstants.FLOW_INFO + flowInfo.getFlowDeployId(), RedisConstants.FLOW_EXPIRED_SECOND,
                JSON.toJSONString(flowInfo));
        return flowInfo;
    }

    private FlowInstanceBO getFlowInstanceBO(String flowInstanceId) throws Exception {
        //get from cache firstly
        String redisKey = RedisConstants.FLOW_INSTANCE + flowInstanceId;
        String flowInstanceStr = redisClient.get(redisKey);
        if (StringUtils.isNotBlank(flowInstanceStr)) {
            LOGGER.info("getFlowInstanceBO from cache.||flowInstanceId={}||flowInstanceStr={}", flowInstanceId, flowInstanceStr);
            return JSONObject.parseObject(flowInstanceStr, FlowInstanceBO.class);
        }

        //get from db
        FlowInstancePO flowInstancePO = processInstanceDAO.selectByFlowInstanceId(flowInstanceId);
        if (flowInstancePO == null) {
            LOGGER.warn("getFlowInstancePO failed: cannot find flowInstancePO from db.||flowInstanceId={}", flowInstanceId);
            throw new ProcessException(ErrorEnum.GET_FLOW_INSTANCE_FAILED);
        }
        FlowInstanceBO flowInstanceBO = new FlowInstanceBO();
        BeanUtils.copyProperties(flowInstancePO, flowInstanceBO);

        //set into cache
        redisClient.setex(RedisConstants.FLOW_INSTANCE + flowInstanceBO.getFlowInstanceId(),
                RedisConstants.FLOW_INSTANCE_EXPIRED_SECOND, JSON.toJSONString(flowInstanceBO));
        return flowInstanceBO;
    }

    /**
     * build runtime context object
     * 1.copy flowInfo's properties to runtimeContext by BeanUtils
     * 2.set flowElementMap
     * up to now, flowInfo of runtimeContext is complete
     *
     * @param flowInfo
     * @return
     */
    private RuntimeContext buildRuntimeContext(FlowInfo flowInfo) {
        RuntimeContext runtimeContext = new RuntimeContext();
        BeanUtils.copyProperties(flowInfo, runtimeContext);
        runtimeContext.setFlowElementMap(FlowModelUtil.getFlowElementMap(flowInfo.getFlowModel()));
        return runtimeContext;
    }

    private RuntimeContext buildRuntimeContext(FlowInfo flowInfo, List<InstanceData> variables) {
        // init flow info is done
        RuntimeContext runtimeContext = buildRuntimeContext(flowInfo);
        Map<String, InstanceData> instanceDataMap = InstanceDataUtil.getInstanceDataMap(variables);
        // init data info is not done, this init instanceDataMap only expect instanceDataId
        runtimeContext.setInstanceDataMap(instanceDataMap);
        return runtimeContext;
    }

    private RuntimeDTO fillRuntimeDTO(RuntimeDTO runtimeDTO, RuntimeContext runtimeContext) {
        if (runtimeContext.getProcessStatus() == ProcessStatus.SUCCESS) {
            return fillRuntimeDTO(runtimeDTO, runtimeContext, ErrorEnum.SUCCESS);
        }
        return fillRuntimeDTO(runtimeDTO, runtimeContext, ErrorEnum.FAILED);
    }

    private RuntimeDTO fillRuntimeDTO(RuntimeDTO runtimeDTO, RuntimeContext runtimeContext, ErrorEnum errorEnum) {
        return fillRuntimeDTO(runtimeDTO, runtimeContext, errorEnum.getErrNo(), errorEnum.getErrMsg());
    }

    private RuntimeDTO fillRuntimeDTO(RuntimeDTO runtimeDTO, RuntimeContext runtimeContext, ProcessException e) {
        return fillRuntimeDTO(runtimeDTO, runtimeContext, e.getErrNo(), e.getErrMsg());
    }

    private RuntimeDTO fillRuntimeDTO(RuntimeDTO runtimeDTO, RuntimeContext runtimeContext, int errNo, String errMsg) {
        runtimeDTO.setErrCode(errNo);
        runtimeDTO.setErrMsg(errMsg);

        if (runtimeContext != null) {
            runtimeDTO.setFlowInstanceId(runtimeContext.getFlowInstanceId());
            runtimeDTO.setStatus(runtimeContext.getFlowInstanceStatus());
            runtimeDTO.setActiveTaskInstance(buildActiveTaskInstanceDTO(runtimeContext.getSuspendNodeInstance(), runtimeContext));
            runtimeDTO.setVariables(InstanceDataUtil.getInstanceDataList(runtimeContext.getInstanceDataMap()));
        }
        return runtimeDTO;
    }

    private NodeInstanceDTO buildActiveTaskInstanceDTO(NodeInstanceBO nodeInstanceBO, RuntimeContext runtimeContext) {
        NodeInstanceDTO activeNodeInstanceDTO = new NodeInstanceDTO();
        BeanUtils.copyProperties(nodeInstanceBO, activeNodeInstanceDTO);
        activeNodeInstanceDTO.setModelKey(nodeInstanceBO.getNodeKey());
        FlowElement flowElement = runtimeContext.getFlowElementMap().get(nodeInstanceBO.getNodeKey());
        activeNodeInstanceDTO.setModelName(FlowModelUtil.getElementName(flowElement));
        activeNodeInstanceDTO.setProperties(flowElement.getProperties());

        return activeNodeInstanceDTO;
    }
}
