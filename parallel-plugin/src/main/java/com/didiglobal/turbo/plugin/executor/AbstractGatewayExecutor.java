package com.didiglobal.turbo.plugin.executor;

import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.common.ExtendRuntimeContext;
import com.didiglobal.turbo.engine.common.FlowInstanceStatus;
import com.didiglobal.turbo.engine.common.ProcessStatus;
import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;
import com.didiglobal.turbo.engine.exception.SuspendException;
import com.didiglobal.turbo.plugin.InclusiveGatewayElementPlugin;
import com.didiglobal.turbo.plugin.ParallelGatewayElementPlugin;
import com.didiglobal.turbo.plugin.common.Constants;
import com.didiglobal.turbo.plugin.common.ParallelErrorEnum;
import com.didiglobal.turbo.plugin.common.ParallelNodeInstanceStatus;
import com.didiglobal.turbo.plugin.common.MergeStrategy;
import com.didiglobal.turbo.plugin.common.ParallelRuntimeContext;
import com.didiglobal.turbo.engine.bo.NodeInstanceBO;
import com.didiglobal.turbo.engine.common.InstanceDataType;
import com.didiglobal.turbo.engine.common.NodeInstanceStatus;
import com.didiglobal.turbo.engine.common.NodeInstanceType;
import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.executor.ElementExecutor;
import com.didiglobal.turbo.engine.executor.RuntimeExecutor;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.util.FlowModelUtil;
import com.didiglobal.turbo.engine.util.InstanceDataUtil;
import com.didiglobal.turbo.plugin.config.ParallelMergeLockConfig;
import com.didiglobal.turbo.plugin.lock.ParallelMergeLock;
import com.didiglobal.turbo.plugin.service.ParallelNodeInstanceService;
import com.didiglobal.turbo.plugin.util.ExecutorUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class AbstractGatewayExecutor extends ElementExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGatewayExecutor.class);

    @Resource
    protected AsynTaskExecutor asynTaskExecutor;

    @Resource
    private MergeStrategyFactory mergeStrategyFactory;

    @Resource
    protected ParallelNodeInstanceService parallelNodeInstanceService;

    @Resource
    private ParallelMergeLock parallelMergeLock;

    /**
     * When parallel gateways and inclusive gateways are used as branch nodes,
     * the processing of exits is different. Here, the actual number of exits needs to be calculated
     */
    protected abstract int calculateOutgoingSize(FlowElement currentNodeModel, Map<String, FlowElement> flowElementMap, Map<String, InstanceData> instanceDataMap);

    protected List<RuntimeExecutor> getExecuteExecutors(RuntimeContext runtimeContext) {
        Pair<String, String> pair = ExecutorUtil.getForkAndJoinNodeKey(runtimeContext.getCurrentNodeModel());
        String nodeKey = runtimeContext.getCurrentNodeInstance().getNodeKey();
        runtimeContext.getExtendProperties().put("parallelRuntimeContextList", new ArrayList<ParallelRuntimeContext>());
        if (ExecutorUtil.isFork(nodeKey, pair)) {
            List<FlowElement> nextNodes = calculateNextNodes(runtimeContext.getCurrentNodeModel(),
                    runtimeContext.getFlowElementMap(), runtimeContext.getInstanceDataMap());
            List<RuntimeExecutor> runtimeExecutors = new ArrayList<>();
            for (FlowElement nextNode : nextNodes) {
                ParallelRuntimeContext context = new ParallelRuntimeContext();
                context.setCurrentNodeModel(nextNode);
                List<ParallelRuntimeContext> parallelRuntimeContextList = (List<ParallelRuntimeContext>) runtimeContext.getExtendProperties().getOrDefault("parallelRuntimeContextList", new ArrayList<ParallelRuntimeContext>());
                parallelRuntimeContextList.add(context);
                runtimeContext.getExtendProperties().put("parallelRuntimeContextList", parallelRuntimeContextList);
                ElementExecutor elementExecutor = executorFactory.getElementExecutor(nextNode);
                runtimeExecutors.add(elementExecutor);
            }
            if (runtimeExecutors.size() <= 1) {
                runtimeContext.setCurrentNodeModel(nextNodes.get(0));
                List<ParallelRuntimeContext> parallelRuntimeContextList = (List<ParallelRuntimeContext>) runtimeContext.getExtendProperties().getOrDefault("parallelRuntimeContextList", new ArrayList<ParallelRuntimeContext>());
                //parallelRuntimeContextList.clear();
            }
            return runtimeExecutors;
        } else if (ExecutorUtil.isJoin(nodeKey, pair)) {
            // select only one outgoing and do not evaluate the expression
            return Lists.newArrayList(super.getExecuteExecutor(runtimeContext));
        } else {
            // not match
            LOGGER.warn("Mismatch between fork and join of node [{}] in flow definition", nodeKey);
            throw new ProcessException(ParallelErrorEnum.FORK_AND_JOIN_NOT_MATCH.getErrNo(), ParallelErrorEnum.FORK_AND_JOIN_NOT_MATCH.getErrMsg());
        }
    }

    @Override
    protected void doExecute(RuntimeContext runtimeContext) {
        FlowElement currentNodeModel = runtimeContext.getCurrentNodeModel();
        Pair<String, String> forkAndJoinNodeKey = ExecutorUtil.getForkAndJoinNodeKey(currentNodeModel);
        String flowInstanceId = runtimeContext.getFlowInstanceId();
        NodeInstanceBO currentNodeInstance = runtimeContext.getCurrentNodeInstance();

        // save and clear node instance list before execute.
        saveAndClearNodeInstanceList(runtimeContext);

        if (ExecutorUtil.isFork(currentNodeModel.getKey(), forkAndJoinNodeKey)) {
            // fork
            forkNodeHandle(runtimeContext, currentNodeModel);
            markCurrentNodeCompleted(runtimeContext);
            //super.preExecute(runtimeContext);
            List<RuntimeExecutor> executeExecutors = getExecuteExecutors(runtimeContext);
            doExecuteByAsyn(runtimeContext, executeExecutors);
        } else if (ExecutorUtil.isJoin(currentNodeModel.getKey(), forkAndJoinNodeKey)) {
            // join
            joinNodeHandle(runtimeContext, currentNodeModel, forkAndJoinNodeKey.getLeft(), flowInstanceId, currentNodeInstance);
        } else {
            LOGGER.error("Missing required element attributes: forkJoinMatch[fork,join]");
            throw new ProcessException(ParallelErrorEnum.REQUIRED_ELEMENT_ATTRIBUTES.getErrNo(), ParallelErrorEnum.REQUIRED_ELEMENT_ATTRIBUTES.getErrMsg());
        }
    }

    private void doExecuteByAsyn(RuntimeContext runtimeContext, List<RuntimeExecutor> runtimeExecutors) {
        List<RuntimeContext> contextList = Lists.newArrayList();
        CompletionService<RuntimeContext> completionService = new ExecutorCompletionService<>(asynTaskExecutor);
        // 建立 Future 和 RuntimeContext 的映射关系
        Map<Future<RuntimeContext>, RuntimeContext> futureContextMap = new HashMap<>();

        AtomicInteger processStatus = new AtomicInteger(ProcessStatus.SUCCESS);
        String parentExecuteIdStr = ExecutorUtil.getParentExecuteId((String) runtimeContext.getExtendProperties().getOrDefault("executeId", ""));
        List<String> executeIds = new ArrayList<>(ExecutorUtil.getExecuteIdSet((String) runtimeContext.getExtendProperties().get("executeId")));
        runtimeContext.getExtendProperties().put("executeId", null);
        for (int i = 0; i < runtimeExecutors.size(); i++) {
            RuntimeExecutor executor = runtimeExecutors.get(i);
            RuntimeContext rc = cloneRuntimeContext(runtimeContext, parentExecuteIdStr, executeIds, i);
            contextList.add(rc);
            Future<RuntimeContext> future = completionService.submit(() -> asynExecute(processStatus, executor, rc));
            futureContextMap.put(future, rc);
        }

        // execute result handle
        asynExecuteResultHandle(runtimeContext, futureContextMap, completionService, executeIds, asynTaskExecutor.getTimeout());
    }

    private void asynExecuteResultHandle(RuntimeContext runtimeContext, Map<Future<RuntimeContext>, RuntimeContext> futureContextMap, CompletionService<RuntimeContext> completionService, List<String> executeIds, long timeout) {
        // system exception, execution exception, suspend exception
        Map<String, ProcessException> em = new HashMap<>();
        String systemErrorNodeKey = null;
        String processExceptionNodeKey = null;
        String suspendExceptionNodeKey = null;
        List<ParallelRuntimeContext> parallelRuntimeContextList = (List<ParallelRuntimeContext>) runtimeContext.getExtendProperties().getOrDefault("parallelRuntimeContextList", new ArrayList<ParallelRuntimeContext>());
        parallelRuntimeContextList.clear();
        
        // 循环获取所有任务的执行结果
        for (int i = 0; i < futureContextMap.size(); i++) {
            ParallelRuntimeContext prc = new ParallelRuntimeContext();
            RuntimeContext actualContext = null;
            try {
                Future<RuntimeContext> future;
                if (timeout > 0) {
                    future = getResultWithTimeout(completionService, timeout);
                } else {
                    future = completionService.take();
                }
                // 通过 Future 获取对应的 RuntimeContext
                actualContext = futureContextMap.get(future);
                future.get();
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof SuspendException) {
                    SuspendException exception = (SuspendException) cause;
                    if (actualContext != null) {
                        suspendExceptionNodeKey = actualContext.getSuspendNodeInstance().getNodeKey();
                        em.put(suspendExceptionNodeKey, exception);
                    }
                    prc.setException(exception);
                } else if (cause instanceof ProcessException) {
                    ProcessException exception = (ProcessException) cause;
                    if (actualContext != null) {
                        processExceptionNodeKey = actualContext.getSuspendNodeInstance().getNodeKey();
                        em.put(processExceptionNodeKey, exception);
                    }
                    prc.setException(exception);
                } else {
                    LOGGER.error("parallel process exception", e);
                    if (actualContext != null) {
                        systemErrorNodeKey = actualContext.getSuspendNodeInstance().getNodeKey();
                        ProcessException exception = new ProcessException(ErrorEnum.SYSTEM_ERROR);
                        em.put(systemErrorNodeKey, exception);
                        prc.setException(exception);
                    } else {
                        ProcessException exception = new ProcessException(ErrorEnum.SYSTEM_ERROR);
                        prc.setException(exception);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("parallel process exception", e);
                if (actualContext != null) {
                    systemErrorNodeKey = actualContext.getSuspendNodeInstance().getNodeKey();
                    ProcessException exception = new ProcessException(ErrorEnum.SYSTEM_ERROR);
                    em.put(systemErrorNodeKey, exception);
                    prc.setException(exception);
                } else {
                    ProcessException exception = new ProcessException(ErrorEnum.SYSTEM_ERROR);
                    prc.setException(exception);
                }
            } finally {
                if (actualContext != null) {
                    prc.setExecuteId((String) actualContext.getExtendProperties().get("executeId"));
                    prc.setBranchExecuteDataMap(actualContext.getInstanceDataMap());
                    prc.setBranchSuspendNodeInstance(actualContext.getSuspendNodeInstance());
                    
                    // 处理嵌套并行网关：如果分支内部还有并行网关，需要将内部的 parallelRuntimeContextList 合并到外层
                    List<ParallelRuntimeContext> nestedParallelContextList = 
                        (List<ParallelRuntimeContext>) actualContext.getExtendProperties().get("parallelRuntimeContextList");
                    if (nestedParallelContextList != null && !nestedParallelContextList.isEmpty()) {
                        // 有嵌套并行网关：只添加内部的并行上下文，不添加当前分支本身的prc
                        parallelRuntimeContextList.addAll(nestedParallelContextList);
                        LOGGER.info("Merge nested parallel context, nested size: {}, current total size: {}", 
                            nestedParallelContextList.size(), parallelRuntimeContextList.size());
                    } else {
                        // 没有嵌套：添加当前分支的prc
                        parallelRuntimeContextList.add(prc);
                    }
                } else {
                    // actualContext为null的异常情况，仍然添加prc以保持列表完整性
                    parallelRuntimeContextList.add(prc);
                }
            }
        }

        //  fixme optimize
        if (null != systemErrorNodeKey) {
            parallelNodeInstanceService.closeParallelSuspendUserTask(runtimeContext, executeIds);
            throw em.get(systemErrorNodeKey);
        }
        if (null != processExceptionNodeKey) {
            parallelNodeInstanceService.closeParallelSuspendUserTask(runtimeContext, executeIds);
            throw em.get(processExceptionNodeKey);
        }
        throw em.get(suspendExceptionNodeKey);
    }

    private Future<RuntimeContext> getResultWithTimeout(CompletionService<RuntimeContext> completionService, long timeout) {
        try {
            Future<RuntimeContext> future = completionService.poll(timeout, TimeUnit.MILLISECONDS);
            if (future == null) {
                LOGGER.warn("Parallel execute timeout, please obtain the latest process execution status through query||timeout={}", timeout);
                throw new ProcessException(ParallelErrorEnum.PARALLEL_EXECUTE_TIMEOUT.getErrNo(), ParallelErrorEnum.PARALLEL_EXECUTE_TIMEOUT.getErrMsg());
            }
            return future;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private RuntimeContext asynExecute(AtomicInteger processStatus, RuntimeExecutor executor, RuntimeContext rc) {
        try {
            doParallelExecute(rc, executor);
        } catch (ProcessException e) {
            if (!ErrorEnum.isSuccess(e.getErrNo())
                && processStatus.get() == ProcessStatus.SUCCESS) {
                processStatus.set(ProcessStatus.FAILED);
            }
            throw e;
        } finally {
            rc.setProcessStatus(processStatus.get());
            doPostParallelExecute(rc);
        }
        return rc;
    }

    private void doPostParallelExecute(RuntimeContext runtimeContext) throws ProcessException {

        // 1.update context with processStatus
        if (runtimeContext.getProcessStatus() == ProcessStatus.SUCCESS) {
            // SUCCESS: update runtimeContext: update suspendNodeInstance
            if (runtimeContext.getCurrentNodeInstance() != null) {
                runtimeContext.setSuspendNodeInstance(runtimeContext.getCurrentNodeInstance());
            }
        }

        // 2.save nodeInstanceList to db
        saveNodeInstanceList(runtimeContext);

        // 3.update flowInstance status while completed
        // 注意：只有在非并行分支执行中才检查流程是否完成
        // 在并行分支中（executeId不为空），不应该检查流程完成状态，因为还有其他分支在执行
        String executeId = (String) runtimeContext.getExtendProperties().get("executeId");
        boolean isInParallelBranch = StringUtils.isNotEmpty(executeId);
        
        if (!isInParallelBranch && isCompletedForParallel(runtimeContext)) {
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

    /**
     * 判断流程是否完成（专门用于并行网关场景）
     * 只有当挂起节点是 EndEvent 且状态为 COMPLETED 时才认为流程完成
     */
    private boolean isCompletedForParallel(RuntimeContext runtimeContext) throws ProcessException {
        // 如果流程已经标记为完成，直接返回 true
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

        // 挂起节点必须是完成状态
        if (suspendNodeInstance.getStatus() != NodeInstanceStatus.COMPLETED) {
            return false;
        }

        // 只有挂起节点是 EndEvent 时，才认为流程完成
        String nodeKey = suspendNodeInstance.getNodeKey();
        Map<String, FlowElement> flowElementMap = runtimeContext.getFlowElementMap();
        FlowElement flowElement = FlowModelUtil.getFlowElement(flowElementMap, nodeKey);
        return flowElement != null && flowElement.getType() == com.didiglobal.turbo.engine.common.FlowElementType.END_EVENT;
    }

    private void doParallelExecute(RuntimeContext runtimeContext, RuntimeExecutor runtimeExecutor) throws ProcessException {
        while (runtimeExecutor != null) {
            runtimeExecutor.execute(runtimeContext);
            runtimeExecutor = super.getExecuteExecutor(runtimeContext);
        }
    }

    private RuntimeContext cloneRuntimeContext(RuntimeContext runtimeContext, String parentExecuteIdStr, List<String> executeIds, int i) {
        RuntimeContext rc = SerializationUtils.clone(runtimeContext);
        String executeId = ExecutorUtil.genExecuteIdWithParent(parentExecuteIdStr, executeIds.get(i));
        rc.getExtendProperties().put("executeId", executeId);
        List<ParallelRuntimeContext> parallelRuntimeContextList = (List<ParallelRuntimeContext>) rc.getExtendProperties().get("parallelRuntimeContextList");
        ParallelRuntimeContext context = parallelRuntimeContextList.get(i);
        rc.setCurrentNodeModel(context.getCurrentNodeModel());
        parallelRuntimeContextList.clear();
        return rc;
    }

    private void markCurrentNodeCompleted(RuntimeContext runtimeContext) {
        NodeInstanceBO currentNodeInstance = runtimeContext.getCurrentNodeInstance();
        currentNodeInstance.setInstanceDataId(runtimeContext.getInstanceDataId());
        currentNodeInstance.setStatus(NodeInstanceStatus.COMPLETED);
    }

    @Override
    protected void preExecute(RuntimeContext runtimeContext) throws ProcessException {
        super.preExecute(runtimeContext);
        NodeInstanceBO currentNodeInstance = runtimeContext.getCurrentNodeInstance();
        String executeId = (String) runtimeContext.getExtendProperties().get("executeId");
        if (StringUtils.isEmpty(executeId)) {
            Object parallelRuntimeContextList = runtimeContext.getExtendProperties().get("parallelRuntimeContextList");
            if (null != parallelRuntimeContextList && !((List<ParallelRuntimeContext>) parallelRuntimeContextList).isEmpty()) {
                executeId = String.valueOf(((List<ParallelRuntimeContext>) parallelRuntimeContextList).get(0).getExecuteId());
                runtimeContext.getExtendProperties().put("executeId", executeId);
                currentNodeInstance.getProperties().put("executeId", executeId);
            }
        }
    }

    @Override
    protected void postExecute(RuntimeContext runtimeContext) {
        List<ExtendRuntimeContext> parallelRuntimeContextList = (List<ExtendRuntimeContext>) runtimeContext.getExtendProperties().get("parallelRuntimeContextList");
        runtimeContext.setExtendRuntimeContextList(parallelRuntimeContextList);
        NodeInstanceBO currentNodeInstance = runtimeContext.getCurrentNodeInstance();
        currentNodeInstance.setInstanceDataId(runtimeContext.getInstanceDataId());
        currentNodeInstance.setStatus(NodeInstanceStatus.COMPLETED);
    }

    @Override
    protected void preRollback(RuntimeContext runtimeContext) throws ProcessException {
        int nodeType = runtimeContext.getCurrentNodeModel().getType();
        // If the flowElementType is a parallel gateway or an inclusive gateway, rollback is disabled.
        if(nodeType == ParallelGatewayElementPlugin.elementType || nodeType == InclusiveGatewayElementPlugin.elementType){
            LOGGER.warn("getRollbackExecutor failed: parallel gateway or inclusive gateway is not support rollback");
            throw new ProcessException(ParallelErrorEnum.NOT_SUPPORT_ROLLBACK.getErrNo(), ParallelErrorEnum.NOT_SUPPORT_ROLLBACK.getErrMsg());
        }
    }

    private void joinNodeHandle(RuntimeContext runtimeContext, FlowElement currentNodeModel, String forkKey, String flowInstanceId,
                                NodeInstanceBO currentNodeInstance) {
        String nodeKey = currentNodeInstance.getNodeKey();
        boolean lockAcquired = false;
        
        try {
            // 使用锁机制防止并发分支覆盖问题，支持重试
            lockAcquired = acquireLockWithRetry(flowInstanceId, nodeKey);
            if (!lockAcquired) {
                LOGGER.error("Failed to acquire lock after retries.||flowInstanceId={}||nodeKey={}", flowInstanceId, nodeKey);
                throw new ProcessException(ErrorEnum.SYSTEM_ERROR);
            }
            
            // current join node info
            String currentExecuteId = ExecutorUtil.getCurrentExecuteId((String) runtimeContext.getExtendProperties().get("executeId"));
            String parentExecuteId = ExecutorUtil.getParentExecuteId((String) runtimeContext.getExtendProperties().get("executeId"));
            // matched fork node info
            NodeInstancePO forkNodeInstancePo = findForkNodeInstancePO(currentExecuteId, flowInstanceId, forkKey);
            if (forkNodeInstancePo == null) {
                LOGGER.error("Not found matched fork node instance||join_node_key={}", currentNodeModel.getKey());
                throw new ProcessException(ParallelErrorEnum.NOT_FOUND_FORK_INSTANCE.getErrNo(), ParallelErrorEnum.NOT_FOUND_FORK_INSTANCE.getErrMsg());
            }
            Set<String> allExecuteIdSet = ExecutorUtil.getExecuteIdSet((String) forkNodeInstancePo.get("executeId"));
            NodeInstancePO joinNodeInstancePo = findJoinNodeInstancePO(allExecuteIdSet, currentExecuteId, flowInstanceId, nodeKey);

            Map<String, Object> properties = currentNodeModel.getProperties();
            String branchMerge = (String) properties.getOrDefault(com.didiglobal.turbo.plugin.common.Constants.ELEMENT_PROPERTIES.BRANCH_MERGE, MergeStrategy.BRANCH_MERGE.JOIN_ALL);
            String dataMerch = (String) properties.getOrDefault(Constants.ELEMENT_PROPERTIES.DATA_MERGE, MergeStrategy.DATA_MERGE.ALL);
            BranchMergeStrategy branchMergeStrategy = mergeStrategyFactory.getBranchMergeStrategy(branchMerge);
            DataMergeStrategy dataMergeStrategy = mergeStrategyFactory.getDataMergeStrategy(dataMerch);

            if (joinNodeInstancePo == null) {
                // branch first arrival
                runtimeContext.getExtendProperties().clear();
                LOGGER.info("execute join first.||nodeKey={}||nodeInstanceId={}||executeId={}||dataMerge={}",
                        currentNodeModel.getKey(), currentNodeInstance.getNodeInstanceId(), runtimeContext.getExtendProperties().get("executeId"), dataMergeStrategy.name());
                branchMergeStrategy.joinFirst(runtimeContext, forkNodeInstancePo, currentNodeInstance, parentExecuteId, currentExecuteId, allExecuteIdSet, dataMergeStrategy);
            } else {
                runtimeContext.getExtendProperties().clear();
                if (joinNodeInstancePo.getStatus() != ParallelNodeInstanceStatus.WAITING) {
                    LOGGER.warn("reentrant warning, arrival branch already exists||joinNodeKey={}||nodeInstanceId={}", joinNodeInstancePo.getNodeKey(), joinNodeInstancePo.getNodeInstanceId());
                    throw new ProcessException(ParallelErrorEnum.PARALLEL_EXECUTE_REENTRY.getErrNo(), ParallelErrorEnum.PARALLEL_EXECUTE_REENTRY.getErrMsg());
                }
                LOGGER.info("execute join other.||nodeKey={}||nodeInstanceId={}||executeId={}||dataMerge={}",
                        currentNodeModel.getKey(), currentNodeInstance.getNodeInstanceId(), runtimeContext.getExtendProperties().get("executeId"), dataMergeStrategy.name());
                branchMergeStrategy.joinMerge(runtimeContext, joinNodeInstancePo, currentNodeInstance, parentExecuteId, currentExecuteId, allExecuteIdSet, dataMergeStrategy);
            }

            // clear parallel context and reset execute id
            runtimeContext.getExtendProperties().put("parallelRuntimeContextList", null);
            runtimeContext.getExtendProperties().put("executeId", parentExecuteId);
        } finally {
            // 确保释放锁
            if (lockAcquired) {
                try {
                    parallelMergeLock.unlock(flowInstanceId, nodeKey);
                } catch (Exception e) {
                    LOGGER.error("Failed to release lock.||flowInstanceId={}||nodeKey={}", flowInstanceId, nodeKey, e);
                }
            }
        }
    }

    /**
     * 带重试的锁获取逻辑
     * 
     * <p>如果获取锁失败，会等待一段时间后重试，直到成功获取或达到最大重试次数
     * 
     * @param flowInstanceId 流程实例ID
     * @param nodeKey 节点key
     * @return true 如果成功获取锁，false 如果达到最大重试次数后仍失败
     */
    private boolean acquireLockWithRetry(String flowInstanceId, String nodeKey) {
        long retryIntervalMs = ParallelMergeLockConfig.getRetryIntervalMs();
        int maxRetryTimes = ParallelMergeLockConfig.getMaxRetryTimes();
        
        for (int retryCount = 0; retryCount < maxRetryTimes; retryCount++) {
            // 每次尝试立即获取锁（waitTimeMs = 0），不等待
            boolean acquired = parallelMergeLock.tryLock(flowInstanceId, nodeKey, 0);
            if (acquired) {
                if (retryCount > 0) {
                    LOGGER.info("Acquired lock after retries.||flowInstanceId={}||nodeKey={}||retryCount={}", 
                        flowInstanceId, nodeKey, retryCount);
                }
                return true;
            }
            
            // 如果未获取到锁，等待后重试（最后一次重试不需要等待）
            if (retryCount < maxRetryTimes - 1) {
                try {
                    Thread.sleep(retryIntervalMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.warn("Interrupted while waiting for lock retry.||flowInstanceId={}||nodeKey={}", 
                        flowInstanceId, nodeKey);
                    return false;
                }
            }
        }
        
        LOGGER.warn("Failed to acquire lock after max retries.||flowInstanceId={}||nodeKey={}||maxRetryTimes={}", 
            flowInstanceId, nodeKey, maxRetryTimes);
        return false;
    }

    private NodeInstancePO findForkNodeInstancePO(String executeId, String flowInstanceId, String nodeKey) {
        List<NodeInstancePO> nodeInstancePOList = nodeInstanceDAO.selectByFlowInstanceIdAndNodeKey(flowInstanceId, nodeKey);
        Optional<NodeInstancePO> nodeInstancePOOptional = nodeInstancePOList.stream()
                .filter(po -> po.get("executeId") != null && ((String) po.get("executeId")).contains(executeId)).findFirst();
        return nodeInstancePOOptional.orElse(null);
    }

    private NodeInstancePO findJoinNodeInstancePO(Set<String> executeIds, String executeId, String flowInstanceId, String nodeKey) {
        List<NodeInstancePO> nodeInstancePOList = nodeInstanceDAO.selectByFlowInstanceIdAndNodeKey(flowInstanceId, nodeKey);
        if (CollectionUtils.isEmpty(nodeInstancePOList)) {
            return null;
        }

        Set<String> executeIdSet = Sets.newHashSet(executeIds);
        executeIdSet.remove(executeId);
        Optional<NodeInstancePO> instanceOptional = nodeInstancePOList.stream().filter(po -> {
            Set<String> currentExecuteIds = ExecutorUtil.getExecuteIdSet((String) po.get("executeId"));
            if (currentExecuteIds.contains(executeId)) {
                LOGGER.warn("reentrant warning, branch of [{}] has been executed.||instanceNodeId={}", executeId, po.getNodeInstanceId());
                throw new ProcessException(ParallelErrorEnum.PARALLEL_EXECUTE_REENTRY.getErrNo(), ParallelErrorEnum.PARALLEL_EXECUTE_REENTRY.getErrMsg());
            }
            Optional<String> any = executeIdSet.stream().filter(currentExecuteIds::contains).findAny();
            return any.isPresent();
        }).findFirst();
        return instanceOptional.orElse(null);
    }

    private void forkNodeHandle(RuntimeContext runtimeContext, FlowElement currentNodeModel) {
        // 1. execute outgoing size
        int outgoingSize = calculateOutgoingSize(currentNodeModel, runtimeContext.getFlowElementMap(), runtimeContext.getInstanceDataMap());
        if (outgoingSize == 0) {
            throw new ProcessException(ErrorEnum.GATEWAY_NO_OUTGOING);
        }
        // 2. gen execute ids
        List<String> executeIdList = getExecuteIdList(outgoingSize);

        NodeInstancePO nodeInstancePO = buildNodeInstancePO(runtimeContext, runtimeContext.getCurrentNodeInstance());
        String executeIds = ExecutorUtil.genExecuteIds((String) runtimeContext.getExtendProperties().get("executeId"), executeIdList);
        runtimeContext.getExtendProperties().put("executeId", executeIds);
        nodeInstancePO.put("executeId", executeIds);
        nodeInstancePO.setStatus(NodeInstanceStatus.COMPLETED);
        nodeInstanceDAO.insert(nodeInstancePO);
        nodeInstanceLogDAO.insert(buildNodeInstanceLogPO(nodeInstancePO));
        instanceDataDAO.insert(buildInstanceDataPO(runtimeContext, runtimeContext.getCurrentNodeInstance(), runtimeContext.getFlowInstanceId()));
    }

    private List<String> getExecuteIdList(int outgoingSize) {
        List<String> executeIdList = Lists.newArrayList();
        for (int i = 0; i < outgoingSize; i++) {
            executeIdList.add(genId());
        }
        return executeIdList;
    }

    private void saveAndClearNodeInstanceList(RuntimeContext runtimeContext) {
        saveNodeInstanceList(runtimeContext);
        runtimeContext.getNodeInstanceList().clear();
    }

    protected List<FlowElement> calculateNextNodes(FlowElement currentFlowElement, Map<String, FlowElement> flowElementMap,
                                                   Map<String, InstanceData> instanceDataMap) throws ProcessException {
        List<FlowElement> nextFlowElements = calculateOutgoings(currentFlowElement, flowElementMap, instanceDataMap);
        return nextFlowElements.stream()
                .map(nextFlowElement -> getUniqueNextNode(nextFlowElement, flowElementMap))
                .collect(Collectors.toList());
    }

    protected List<FlowElement> calculateOutgoings(FlowElement currentFlowElement, Map<String, FlowElement> flowElementMap, Map<String, InstanceData> instanceDataMap) {
        List<FlowElement> flowElements = new ArrayList<>();

        List<String> outgoingList = currentFlowElement.getOutgoing();
        for (String outgoingKey : outgoingList) {
            FlowElement outgoingSequenceFlow = FlowModelUtil.getFlowElement(flowElementMap, outgoingKey);
            if (calculateCondition(outgoingSequenceFlow, instanceDataMap)) {
                flowElements.add(outgoingSequenceFlow);
            }
        }

        if (flowElements.size() > 0) {
            return flowElements;
        }

        LOGGER.warn("calculateOutgoing failed.||nodeKey={}", currentFlowElement.getKey());
        throw new ProcessException(ErrorEnum.GET_OUTGOING_FAILED);
    }

    protected boolean calculateCondition(FlowElement outgoingSequenceFlow, Map<String, InstanceData> instanceDataMap) {
        // case1 condition is true, hit the outgoing
        return true;
    }

    private InstanceDataPO buildInstanceDataPO(RuntimeContext runtimeContext, NodeInstanceBO currentNodeInstance, String flowInstanceId) {
        InstanceDataPO po = new InstanceDataPO();
        po.setFlowInstanceId(flowInstanceId);
        po.setType(InstanceDataType.EXECUTE);
        po.setFlowModuleId(runtimeContext.getFlowModuleId());
        po.setFlowDeployId(runtimeContext.getFlowDeployId());
        po.setCaller(runtimeContext.getCaller());
        po.setInstanceDataId(genId());
        po.setCreateTime(new Date());
        po.setTenant(runtimeContext.getTenant());
        po.setInstanceData(InstanceDataUtil.getInstanceDataListStr(runtimeContext.getInstanceDataMap()));
        po.setNodeInstanceId(currentNodeInstance.getNodeInstanceId());
        po.setNodeKey(currentNodeInstance.getNodeKey());
        return po;
    }

    private void saveNodeInstanceList(RuntimeContext runtimeContext) {
        List<NodeInstanceBO> processNodeList = runtimeContext.getNodeInstanceList();

        if (CollectionUtils.isEmpty(processNodeList)) {
            LOGGER.warn("parallel process saveNodeInstanceList: processNodeList is empty,||flowInstanceId={}||nodeInstanceType={}",
                    runtimeContext.getFlowInstanceId(), NodeInstanceType.EXECUTE);
            return;
        }

        List<NodeInstancePO> nodeInstancePOList = Lists.newArrayList();
        List<NodeInstanceLogPO> nodeInstanceLogPOList = Lists.newArrayList();
        processNodeList.forEach(nodeInstanceBO -> {
            NodeInstancePO nodeInstancePO = buildNodeInstancePO(runtimeContext, nodeInstanceBO);
            nodeInstancePOList.add(nodeInstancePO);
            // build nodeInstance log
            NodeInstanceLogPO nodeInstanceLogPO = buildNodeInstanceLogPO(nodeInstancePO);
            nodeInstanceLogPOList.add(nodeInstanceLogPO);
        });
        nodeInstanceDAO.insertOrUpdateList(nodeInstancePOList);
        nodeInstanceLogDAO.insertList(nodeInstanceLogPOList);
    }

    private NodeInstancePO buildNodeInstancePO(RuntimeContext runtimeContext, NodeInstanceBO nodeInstanceBO) {
        NodeInstancePO nodeInstancePO = new NodeInstancePO();
        BeanUtils.copyProperties(nodeInstanceBO, nodeInstancePO);
        nodeInstancePO.setFlowInstanceId(runtimeContext.getFlowInstanceId());
        nodeInstancePO.setFlowDeployId(runtimeContext.getFlowDeployId());
        nodeInstancePO.setTenant(runtimeContext.getTenant());
        nodeInstancePO.setCaller(runtimeContext.getCaller());
        Date currentTime = new Date();
        if (null == nodeInstancePO.getCreateTime()) {
            nodeInstancePO.setCreateTime(currentTime);
        }
        if (null != runtimeContext.getExtendProperties()
            && runtimeContext.getExtendProperties().containsKey("parallelRuntimeContextList")
            && !((List<ParallelRuntimeContext>) runtimeContext.getExtendProperties().get("parallelRuntimeContextList")).isEmpty()) {
            nodeInstancePO.put("executeId", ((List<ParallelRuntimeContext>) runtimeContext.getExtendProperties().get("parallelRuntimeContextList")).get(0).getExecuteId());
        }
        nodeInstancePO.setModifyTime(currentTime);
        return nodeInstancePO;
    }

    private NodeInstanceLogPO buildNodeInstanceLogPO(NodeInstancePO nodeInstancePO) {
        NodeInstanceLogPO nodeInstanceLogPO = new NodeInstanceLogPO();
        BeanUtils.copyProperties(nodeInstancePO, nodeInstanceLogPO);
        nodeInstanceLogPO.setId(null);
        nodeInstanceLogPO.setType(NodeInstanceType.EXECUTE);
        return nodeInstanceLogPO;
    }
}
