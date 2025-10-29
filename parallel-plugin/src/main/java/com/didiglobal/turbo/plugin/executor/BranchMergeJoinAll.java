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
        // 1. 获取当前分支数据
        InstanceDataPO joinInstanceData = instanceDataDAO.select(runtimeContext.getFlowInstanceId(), runtimeContext.getInstanceDataId());

        // 2. 获取已合并的数据（之前所有分支的合并结果）
        InstanceDataPO mergedData = instanceDataDAO.select(runtimeContext.getFlowInstanceId(), joinNodeInstancePo.getInstanceDataId());
        if (mergedData == null) {
            mergedData = new InstanceDataPO();
            mergedData.setInstanceDataId(genId());
            joinNodeInstancePo.setInstanceDataId(mergedData.getInstanceDataId());
        }

        // 3. 合并数据
        InstanceDataPO mergePo = dataMergeStrategy.merge(runtimeContext, mergedData, joinInstanceData);

        // 4. 根据是否所有分支都到达，决定完成还是等待
        boolean allArrived = ExecutorUtil.allArrived(allExecuteIdSet, arrivedExecuteIds);
        int status = allArrived ? NodeInstanceStatus.COMPLETED : ParallelNodeInstanceStatus.WAITING;

        // 5. 保存合并后的数据
        if (StringUtils.isBlank(mergedData.getInstanceDataId())) {
            instanceDataDAO.insert(mergePo);
        } else {
            instanceDataDAO.updateData(mergePo);
        }

        // 6. 更新节点实例状态
        buildParallelNodeInstancePo(joinNodeInstancePo, currentNodeInstance, status);
        nodeInstanceDAO.updateById(joinNodeInstancePo);
        nodeInstanceLogDAO.insert(allArrived ?
                buildCurrentNodeInstanceLogPO(currentNodeInstance, currentExecuteId, joinNodeInstancePo) :
                buildNodeInstanceLogPO(joinNodeInstancePo));

        // 7. 如果不是所有分支都到达，则挂起
        if (!allArrived) {
            throw new SuspendException(ParallelErrorEnum.WAITING_SUSPEND.getErrNo(), MessageFormat.format(Constants.NODE_INSTANCE_FORMAT,
                    runtimeContext.getCurrentNodeModel().getKey(),
                    runtimeContext.getCurrentNodeModel().getProperties().getOrDefault(Constants.ELEMENT_PROPERTIES.NAME, StringUtils.EMPTY),
                    currentNodeInstance.getNodeInstanceId()));
        }
    }

    private boolean isNotEmpty(String instanceData) {
        return StringUtils.isNotBlank(instanceData) && StringUtils.equals("{}",instanceData.replace(" ",""));
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