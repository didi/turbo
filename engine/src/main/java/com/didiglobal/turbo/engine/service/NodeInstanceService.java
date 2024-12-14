package com.didiglobal.turbo.engine.service;

import com.didiglobal.turbo.engine.common.FlowElementType;
import com.didiglobal.turbo.engine.dao.FlowDeploymentDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import com.didiglobal.turbo.engine.entity.FlowInstancePO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.util.FlowModelUtil;

import java.util.List;
import java.util.Map;

public class NodeInstanceService {

    private final NodeInstanceDAO nodeInstanceDAO;

    private final ProcessInstanceDAO processInstanceDAO;

    private final FlowDeploymentDAO flowDeploymentDAO;

    private final FlowInstanceService flowInstanceService;

    public NodeInstanceService(NodeInstanceDAO nodeInstanceDAO, ProcessInstanceDAO processInstanceDAO, FlowDeploymentDAO flowDeploymentDAO, FlowInstanceService flowInstanceService) {
        this.nodeInstanceDAO = nodeInstanceDAO;
        this.processInstanceDAO = processInstanceDAO;
        this.flowDeploymentDAO = flowDeploymentDAO;
        this.flowInstanceService = flowInstanceService;
    }

    public NodeInstancePO selectByNodeInstanceId(String flowInstanceId, String nodeInstanceId, boolean effectiveForSubFlowInstance) {
        NodeInstancePO nodeInstancePO = nodeInstanceDAO.selectByNodeInstanceId(flowInstanceId, nodeInstanceId);
        if (!effectiveForSubFlowInstance) {
            return nodeInstancePO;
        }
        if (nodeInstancePO != null) {
            return nodeInstancePO;
        }
        String subFlowInstanceId = flowInstanceService.getFlowInstanceIdByRootFlowInstanceIdAndNodeInstanceId(flowInstanceId, nodeInstanceId);
        return nodeInstanceDAO.selectByNodeInstanceId(subFlowInstanceId, nodeInstanceId);
    }

    public NodeInstancePO selectRecentEndNode(String flowInstanceId) {
        FlowInstancePO rootFlowInstancePO = processInstanceDAO.selectByFlowInstanceId(flowInstanceId);
        FlowDeploymentPO rootFlowDeploymentPO = flowDeploymentDAO.selectByDeployId(rootFlowInstancePO.getFlowDeployId());
        Map<String, FlowElement> rootFlowElementMap = FlowModelUtil.getFlowElementMap(rootFlowDeploymentPO.getFlowModel());

        List<NodeInstancePO> nodeInstancePOList = nodeInstanceDAO.selectDescByFlowInstanceId(flowInstanceId);
        for (NodeInstancePO nodeInstancePO : nodeInstancePOList) {
            int elementType = FlowModelUtil.getElementType(nodeInstancePO.getNodeKey(), rootFlowElementMap);
            if (elementType == FlowElementType.END_EVENT) {
                return nodeInstancePO;
            }
        }
        return null;
    }
}
