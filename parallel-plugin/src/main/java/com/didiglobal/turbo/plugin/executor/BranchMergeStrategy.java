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
import com.didiglobal.turbo.plugin.dao.mapper.JoinSourceMapper;
import com.didiglobal.turbo.plugin.entity.JoinSourcePO;
import com.didiglobal.turbo.plugin.util.ExecutorUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.Set;

public abstract class BranchMergeStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(BranchMergeStrategy.class);

    @Resource
    protected InstanceDataDAO instanceDataDAO;

    @Resource
    protected NodeInstanceDAO nodeInstanceDAO;

    @Resource
    protected NodeInstanceLogDAO nodeInstanceLogDAO;

    @Resource
    protected PluginManager pluginManager;

    @Resource
    private JoinSourceMapper joinSourceMapper;

    protected IdGenerator ID_GENERATOR;

    /**
     * Records the per-branch source association for a parallel gateway join node
     * into the plugin-owned ei_node_instance_join_source table.
     * This avoids accumulating comma-separated IDs into the core
     * ei_node_instance.source_node_instance_id varchar field, which would
     * overflow for flows with large numbers of parallel branches.
     *
     * @param flowInstanceId       the current flow instance id
     * @param joinNodeInstanceId   the join gateway's persisted nodeInstanceId (J1 in DB)
     * @param sourceNodeInstanceId the arriving branch's source node instance id
     * @param sourceNodeKey        the arriving branch's source node key
     */
    protected void saveJoinSource(String flowInstanceId, String joinNodeInstanceId,
                                  String sourceNodeInstanceId, String sourceNodeKey) {
        if (StringUtils.isBlank(sourceNodeInstanceId)) {
            return;
        }
        try {
            JoinSourcePO joinSourcePO = new JoinSourcePO();
            joinSourcePO.setFlowInstanceId(flowInstanceId);
            joinSourcePO.setJoinNodeInstanceId(joinNodeInstanceId);
            joinSourcePO.setSourceNodeInstanceId(sourceNodeInstanceId);
            joinSourcePO.setSourceNodeKey(sourceNodeKey);
            joinSourcePO.setCreateTime(new Date());
            joinSourceMapper.insert(joinSourcePO);
        } catch (Exception e) {
            LOGGER.error("saveJoinSource failed.||flowInstanceId={}||joinNodeInstanceId={}||sourceNodeInstanceId={}",
                    flowInstanceId, joinNodeInstanceId, sourceNodeInstanceId, e);
        }
    }


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
        String executeId = (String) joinNodeInstancePo.get("executeId");
        String newExecuteId = ExecutorUtil.append(executeId, ExecutorUtil.getCurrentExecuteId((String) currentNodeInstance.get("executeId")));
        joinNodeInstancePo.put("executeId", newExecuteId);
        joinNodeInstancePo.setStatus(status);
        joinNodeInstancePo.setModifyTime(new Date());
        // Record per-branch source in the plugin-owned join source table instead of
        // accumulating comma-separated IDs into the core source_node_instance_id varchar field.
        saveJoinSource(joinNodeInstancePo.getFlowInstanceId(), joinNodeInstancePo.getNodeInstanceId(),
                currentNodeInstance.getSourceNodeInstanceId(), currentNodeInstance.getSourceNodeKey());
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