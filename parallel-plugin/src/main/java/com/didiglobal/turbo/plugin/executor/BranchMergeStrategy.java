package com.didiglobal.turbo.plugin.executor;

import com.didiglobal.turbo.engine.bo.NodeInstanceBO;
import com.didiglobal.turbo.engine.common.InstanceDataType;
import com.didiglobal.turbo.engine.common.NodeInstanceType;
import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.dao.InstanceDataDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceLogDAO;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.plugin.IdGeneratorPlugin;
import com.didiglobal.turbo.engine.plugin.manager.PluginManager;
import com.didiglobal.turbo.engine.util.IdGenerator;
import com.didiglobal.turbo.engine.util.InstanceDataUtil;
import com.didiglobal.turbo.engine.util.StrongUuidGenerator;
import com.didiglobal.turbo.plugin.util.ExecutorUtil;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.Set;

public abstract class BranchMergeStrategy {

    @Resource
    protected InstanceDataDAO instanceDataDAO;

    @Resource
    protected NodeInstanceDAO nodeInstanceDAO;

    @Resource
    protected NodeInstanceLogDAO nodeInstanceLogDAO;

    @Resource
    protected PluginManager pluginManager;

    protected IdGenerator ID_GENERATOR;



    /**
     * first arrival
     */
    abstract void joinFirst(RuntimeContext runtimeContext, NodeInstancePO forkNodeInstancePo, NodeInstanceBO currentNodeInstance,
                            String parentExecuteId, String currentExecuteId, Set<String> executeIds, DataMergeStrategy dataMergeStrategy);

    /**
     * non-first arrival
     */
    abstract void joinMerge(RuntimeContext runtimeContext, NodeInstancePO joinNodeInstance, NodeInstanceBO currentNodeInstance,
                            String parentExecuteId, String currentExecuteId, Set<String> allExecuteIdSet, DataMergeStrategy dataMergeStrategy);

    protected String genId() {
        if (null == ID_GENERATOR) {
            List<IdGeneratorPlugin> idGeneratorPlugins = pluginManager.getPluginsFor(IdGeneratorPlugin.class);
            if (!idGeneratorPlugins.isEmpty()) {
                ID_GENERATOR = idGeneratorPlugins.get(0).getIdGenerator();
            } else {
                ID_GENERATOR = new StrongUuidGenerator();
            }
        }
        return ID_GENERATOR.getNextId();
    }

    protected InstanceDataPO buildInstanceDataPO(RuntimeContext runtimeContext, NodeInstanceBO currentNodeInstance, String flowInstanceId) {
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

    protected NodeInstancePO buildNodeInstancePO(RuntimeContext runtimeContext, NodeInstanceBO nodeInstanceBO) {
        NodeInstancePO nodeInstancePO = new NodeInstancePO();
        BeanUtils.copyProperties(nodeInstanceBO, nodeInstancePO);
        nodeInstancePO.setFlowDeployId(runtimeContext.getFlowDeployId());
        nodeInstancePO.setFlowInstanceId(runtimeContext.getFlowInstanceId());
        nodeInstancePO.setCaller(runtimeContext.getCaller());
        nodeInstancePO.setTenant(runtimeContext.getTenant());
        Date currentTime = new Date();
        if (null == nodeInstancePO.getCreateTime()) {
            nodeInstancePO.setCreateTime(currentTime);
        }
        nodeInstancePO.setModifyTime(currentTime);
        return nodeInstancePO;
    }

    protected NodeInstanceLogPO buildNodeInstanceLogPO(NodeInstancePO nodeInstancePO) {
        NodeInstanceLogPO nodeInstanceLogPO = new NodeInstanceLogPO();
        BeanUtils.copyProperties(nodeInstancePO, nodeInstanceLogPO);
        nodeInstanceLogPO.setId(null);
        nodeInstanceLogPO.setType(NodeInstanceType.EXECUTE);
        return nodeInstanceLogPO;
    }

    protected void buildParallelNodeInstancePo(NodeInstancePO joinNodeInstancePo, NodeInstanceBO currentNodeInstance, int status) {
        String sourceNodeInstanceId = joinNodeInstancePo.getSourceNodeInstanceId();
        String sourceNodeKey = joinNodeInstancePo.getSourceNodeKey();
        String executeId = (String) joinNodeInstancePo.get("executeId");
        joinNodeInstancePo.setSourceNodeInstanceId(ExecutorUtil.append(sourceNodeInstanceId, currentNodeInstance.getSourceNodeInstanceId()));
        joinNodeInstancePo.setSourceNodeKey(ExecutorUtil.append(sourceNodeKey, currentNodeInstance.getSourceNodeKey()));
        String newExecuteId = ExecutorUtil.append(executeId, ExecutorUtil.getCurrentExecuteId((String) currentNodeInstance.get("executeId")));
        joinNodeInstancePo.put("executeId", newExecuteId);
        joinNodeInstancePo.setStatus(status);
        joinNodeInstancePo.setModifyTime(new Date());
    }

    protected NodeInstanceLogPO buildCurrentNodeInstanceLogPO(NodeInstanceBO currentNodeInstance, String executeId, NodeInstancePO nodeInstancePO) {
        NodeInstanceLogPO nodeInstanceLogPO = new NodeInstanceLogPO();
        BeanUtils.copyProperties(nodeInstancePO, nodeInstanceLogPO);
        nodeInstanceLogPO.setId(null);
        nodeInstanceLogPO.setType(NodeInstanceType.EXECUTE);
        nodeInstanceLogPO.setNodeInstanceId(currentNodeInstance.getNodeInstanceId());
        nodeInstancePO.setNodeKey(currentNodeInstance.getNodeKey());
        nodeInstancePO.put("executeId", executeId);
        return nodeInstanceLogPO;
    }
}