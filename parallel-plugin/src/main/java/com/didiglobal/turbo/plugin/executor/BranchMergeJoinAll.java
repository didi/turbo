package com.didiglobal.turbo.plugin.executor;

import com.didiglobal.turbo.engine.bo.NodeInstanceBO;
import com.didiglobal.turbo.engine.common.Constants;
import com.didiglobal.turbo.engine.common.InstanceDataType;
import com.didiglobal.turbo.engine.common.NodeInstanceStatus;
import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.exception.SuspendException;
import com.didiglobal.turbo.plugin.common.ParallelErrorEnum;
import com.didiglobal.turbo.plugin.common.ParallelNodeInstanceStatus;
import com.didiglobal.turbo.plugin.util.ExecutorUtil;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Set;

@Component
public class BranchMergeJoinAll extends BranchMergeStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(BranchMergeJoinAll.class);


    @Override
    public void joinFirst(RuntimeContext runtimeContext, NodeInstancePO forkNodeInstancePo, NodeInstanceBO currentNodeInstance,
                          String parentExecuteId, String currentExecuteId, Set<String> executeIds, DataMergeStrategy dataMergeStrategy) {
        InstanceDataPO joinInstanceData = buildInstanceDataPO(runtimeContext, currentNodeInstance, runtimeContext.getFlowInstanceId());
        InstanceDataPO forkInstanceData = instanceDataDAO.select(runtimeContext.getFlowInstanceId(), forkNodeInstancePo.getInstanceDataId());
        if (ExecutorUtil.allArrived(executeIds, Sets.newHashSet(currentExecuteId))) {
            merge(runtimeContext, forkInstanceData, currentNodeInstance, parentExecuteId, currentExecuteId, dataMergeStrategy, joinInstanceData, NodeInstanceStatus.COMPLETED);
        } else {
            merge(runtimeContext, forkInstanceData, currentNodeInstance, parentExecuteId, currentExecuteId, dataMergeStrategy, joinInstanceData, ParallelNodeInstanceStatus.WAITING);

            throw new SuspendException(ParallelErrorEnum.WAITING_SUSPEND.getErrNo(), ParallelErrorEnum.WAITING_SUSPEND.getErrMsg());
        }
    }

    private void merge(RuntimeContext runtimeContext, InstanceDataPO forkInstanceData, NodeInstanceBO currentNodeInstance,
                       String parentExecuteId, String currentExecuteId, DataMergeStrategy dataMergeStrategy, InstanceDataPO instanceDataPo, int status) {
        InstanceDataPO mergePo = dataMergeStrategy.merge(runtimeContext, forkInstanceData, instanceDataPo);
        instanceDataDAO.insertOrUpdate(mergePo);
        currentNodeInstance.setStatus(status);
        currentNodeInstance.setInstanceDataId(mergePo.getInstanceDataId());
        currentNodeInstance.put("executeId", ExecutorUtil.genExecuteIdWithParent(parentExecuteId, currentExecuteId));
        NodeInstancePO joinPo = buildNodeInstancePO(runtimeContext, currentNodeInstance);
        nodeInstanceDAO.insert(joinPo);
        nodeInstanceLogDAO.insert(buildNodeInstanceLogPO(joinPo));
    }

    @Override
    public void joinMerge(RuntimeContext runtimeContext, NodeInstancePO joinNodeInstancePo, NodeInstanceBO currentNodeInstance,
                          String parentExecuteId, String currentExecuteId, Set<String> allExecuteIdSet, DataMergeStrategy dataMergeStrategy) {
        Set<String> arrivedExecuteIds = ExecutorUtil.getExecuteIdSet((String) joinNodeInstancePo.get("executeId"));
        arrivedExecuteIds.add(currentExecuteId);
        // 当前分支产生的数据
        InstanceDataPO currentBranchData = instanceDataDAO.select(runtimeContext.getFlowInstanceId(), runtimeContext.getInstanceDataId());
        // 已在 join 节点累计的数据（如果已有）
        InstanceDataPO accumulatedJoinData = null;
        String instanceDataId = joinNodeInstancePo.getInstanceDataId();
        if (StringUtils.isNotBlank(instanceDataId)) {
            accumulatedJoinData = instanceDataDAO.select(runtimeContext.getFlowInstanceId(), instanceDataId);
        }
        if (ExecutorUtil.allArrived(allExecuteIdSet, arrivedExecuteIds)) {
            // All arrived
            InstanceDataPO mergePo = dataMergeStrategy.merge(runtimeContext, accumulatedJoinData, currentBranchData);
            if ((mergePo.getId() == null || StringUtils.isBlank(instanceDataId)) && isNotEmpty(mergePo.getInstanceData())) {
                String newInstanceDataId = genId();
                joinNodeInstancePo.setInstanceDataId(newInstanceDataId);
                fillMergePo(runtimeContext, mergePo, newInstanceDataId);
                instanceDataDAO.insert(mergePo);
            } else {
                if(StringUtils.isNotBlank(instanceDataId) && accumulatedJoinData != null){
                    // 确保更新的是 join 节点的累计行，而不是当前分支的行
                    mergePo.setId(accumulatedJoinData.getId());
                    mergePo.setInstanceDataId(instanceDataId);
                    instanceDataDAO.updateData(mergePo);
                }else {
                    LOGGER.warn("There is no data to be merged.");
                }
            }
            buildParallelNodeInstancePo(joinNodeInstancePo, currentNodeInstance, NodeInstanceStatus.COMPLETED);
            runtimeContext.setInstanceDataId(mergePo.getInstanceDataId());
            nodeInstanceDAO.updateById(joinNodeInstancePo);
            nodeInstanceLogDAO.insert(buildCurrentNodeInstanceLogPO(currentNodeInstance, currentExecuteId, joinNodeInstancePo));
        } else {
            // Not all arrived
            InstanceDataPO mergePo = dataMergeStrategy.merge(runtimeContext, accumulatedJoinData, currentBranchData);
            if (StringUtils.isNotBlank(instanceDataId) && accumulatedJoinData != null) {
                // 确保更新的是 join 节点的累计行，而不是当前分支的行
                mergePo.setId(accumulatedJoinData.getId());
                mergePo.setInstanceDataId(instanceDataId);
                instanceDataDAO.updateData(mergePo);
            } else if (isNotEmpty(mergePo.getInstanceData())) {
                String newInstanceDataId = genId();
                joinNodeInstancePo.setInstanceDataId(newInstanceDataId);
                fillMergePo(runtimeContext, mergePo, newInstanceDataId);
                instanceDataDAO.insert(mergePo);
            } // else 无需写入空数据
            buildParallelNodeInstancePo(joinNodeInstancePo, currentNodeInstance, ParallelNodeInstanceStatus.WAITING);
            nodeInstanceDAO.updateById(joinNodeInstancePo);
            nodeInstanceLogDAO.insert(buildNodeInstanceLogPO(joinNodeInstancePo));

            throw new SuspendException(ParallelErrorEnum.WAITING_SUSPEND.getErrNo(), MessageFormat.format(Constants.NODE_INSTANCE_FORMAT,
                runtimeContext.getCurrentNodeModel().getKey(),
                runtimeContext.getCurrentNodeModel().getProperties().getOrDefault(Constants.ELEMENT_PROPERTIES.NAME, StringUtils.EMPTY),
                currentNodeInstance.getNodeInstanceId()));
        }
    }

    private boolean isNotEmpty(String instanceData) {
        return StringUtils.isNotBlank(instanceData) && !StringUtils.equals("{}",instanceData.replace(" ",""));
    }

    private static void fillMergePo(RuntimeContext runtimeContext, InstanceDataPO mergePo, String instanceDataId) {
        mergePo.setInstanceDataId(instanceDataId);
        mergePo.setCreateTime(new Date());
        mergePo.setNodeInstanceId(runtimeContext.getCurrentNodeInstance().getNodeInstanceId());
        mergePo.setNodeKey(runtimeContext.getCurrentNodeInstance().getNodeKey());
        mergePo.setType(InstanceDataType.COMMIT);
        mergePo.setTenant(runtimeContext.getTenant());
        mergePo.setFlowModuleId(runtimeContext.getFlowModuleId());
        mergePo.setFlowDeployId(runtimeContext.getFlowDeployId());
        mergePo.setCaller(runtimeContext.getCaller());
    }
}