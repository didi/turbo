package com.didiglobal.turbo.plugin.resolver;

import com.didiglobal.turbo.engine.bo.ElementInstance;
import com.didiglobal.turbo.engine.common.NodeInstanceStatus;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.plugin.SourceNodeResolver;
import com.didiglobal.turbo.engine.util.FlowModelUtil;
import com.didiglobal.turbo.plugin.InclusiveGatewayElementPlugin;
import com.didiglobal.turbo.plugin.ParallelGatewayElementPlugin;
import com.didiglobal.turbo.plugin.dao.NodeInstanceSourceDAO;
import com.didiglobal.turbo.plugin.entity.NodeInstanceSourcePO;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 并行网关/包含网关的 SourceNodeResolver 实现。
 * <p>
 * 从 ei_node_instance_source 扩展表中读取 join 节点的多个来源节点，
 * 并为每个来源节点解析出对应的 sequence flow，用于历史展示。
 */
@Component
public class ParallelSourceNodeResolver implements SourceNodeResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelSourceNodeResolver.class);

    @Resource
    private NodeInstanceSourceDAO nodeInstanceSourceDAO;

    @Override
    public boolean supports(NodeInstancePO nodeInstancePO, Map<String, FlowElement> flowElementMap) {
        String nodeKey = nodeInstancePO.getNodeKey();
        FlowElement flowElement = flowElementMap.get(nodeKey);
        if (flowElement == null) {
            return false;
        }
        int type = flowElement.getType();
        // 仅处理并行网关和包含网关类型的节点
        return type == ParallelGatewayElementPlugin.elementType
            || type == InclusiveGatewayElementPlugin.elementType;
    }

    @Override
    public List<ElementInstance> resolveSourceSequenceFlows(NodeInstancePO nodeInstancePO, Map<String, FlowElement> flowElementMap) {
        String flowInstanceId = nodeInstancePO.getFlowInstanceId();
        String nodeInstanceId = nodeInstancePO.getNodeInstanceId();
        String nodeKey = nodeInstancePO.getNodeKey();
        int nodeStatus = nodeInstancePO.getStatus();

        // 查询 ei_node_instance_source 扩展表
        List<NodeInstanceSourcePO> sourcePOList = nodeInstanceSourceDAO.selectByNodeInstanceId(flowInstanceId, nodeInstanceId);

        if (CollectionUtils.isEmpty(sourcePOList)) {
            // 没有扩展表数据，回退到 engine 默认行为
            return null;
        }

        List<ElementInstance> result = new ArrayList<>();
        for (NodeInstanceSourcePO sourcePO : sourcePOList) {
            String sourceNodeKey = sourcePO.getSourceNodeKey();
            FlowElement sourceFlowElement = FlowModelUtil.getSequenceFlow(flowElementMap, sourceNodeKey, nodeKey);
            if (sourceFlowElement == null) {
                LOGGER.warn("resolveSourceSequenceFlows: sequence flow not found.||sourceNodeKey={}||nodeKey={}",
                    sourceNodeKey, nodeKey);
                continue;
            }

            int sourceSequenceFlowStatus = nodeStatus;
            if (nodeStatus == NodeInstanceStatus.ACTIVE) {
                sourceSequenceFlowStatus = NodeInstanceStatus.COMPLETED;
            }
            ElementInstance sequenceFlowInstance = new ElementInstance(sourceFlowElement.getKey(), sourceSequenceFlowStatus, null, null);
            result.add(sequenceFlowInstance);
        }

        return result;
    }
}
