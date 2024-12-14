package com.didiglobal.turbo.engine.service;

import com.didiglobal.turbo.engine.common.FlowElementType;
import com.didiglobal.turbo.engine.dao.FlowDeploymentDAO;
import com.didiglobal.turbo.engine.dao.FlowInstanceMappingDAO;
import com.didiglobal.turbo.engine.dao.InstanceDataDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import com.didiglobal.turbo.engine.entity.FlowInstanceMappingPO;
import com.didiglobal.turbo.engine.entity.FlowInstancePO;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.util.FlowModelUtil;

import java.util.Map;

public class InstanceDataService {

    private final InstanceDataDAO instanceDataDAO;

    private final ProcessInstanceDAO processInstanceDAO;

    private final FlowDeploymentDAO flowDeploymentDAO;

    private final NodeInstanceDAO nodeInstanceDAO;

    private final FlowInstanceMappingDAO flowInstanceMappingDAO;

    private final FlowInstanceService flowInstanceService;

    public InstanceDataService(InstanceDataDAO instanceDataDAO, ProcessInstanceDAO processInstanceDAO, FlowDeploymentDAO flowDeploymentDAO, NodeInstanceDAO nodeInstanceDAO, FlowInstanceMappingDAO flowInstanceMappingDAO, FlowInstanceService flowInstanceService) {
        this.instanceDataDAO = instanceDataDAO;
        this.processInstanceDAO = processInstanceDAO;
        this.flowDeploymentDAO = flowDeploymentDAO;
        this.nodeInstanceDAO = nodeInstanceDAO;
        this.flowInstanceMappingDAO = flowInstanceMappingDAO;
        this.flowInstanceService = flowInstanceService;
    }

    public InstanceDataPO select(String flowInstanceId, String instanceDataId, boolean effectiveForSubFlowInstance) {
        InstanceDataPO instanceDataPO = instanceDataDAO.select(flowInstanceId, instanceDataId);
        if (!effectiveForSubFlowInstance) {
            return instanceDataPO;
        }
        if (instanceDataPO != null) {
            return instanceDataPO;
        }
        String subFlowInstanceId = flowInstanceService.getFlowInstanceIdByRootFlowInstanceIdAndInstanceDataId(flowInstanceId, instanceDataId);
        return instanceDataDAO.select(subFlowInstanceId, instanceDataId);
    }

    public InstanceDataPO select(String flowInstanceId, boolean effectiveForSubFlowInstance) {
        InstanceDataPO instanceDataPO = instanceDataDAO.selectRecentOne(flowInstanceId);
        if (!effectiveForSubFlowInstance) {
            return instanceDataPO;
        }
        FlowInstancePO flowInstancePO = processInstanceDAO.selectByFlowInstanceId(flowInstanceId);
        FlowDeploymentPO flowDeploymentPO = flowDeploymentDAO.selectByDeployId(flowInstancePO.getFlowDeployId());
        Map<String, FlowElement> flowElementMap = FlowModelUtil.getFlowElementMap(flowDeploymentPO.getFlowModel());

        NodeInstancePO nodeInstancePO = nodeInstanceDAO.selectRecentOne(flowInstanceId);
        int elementType = FlowModelUtil.getElementType(nodeInstancePO.getNodeKey(), flowElementMap);
        if (elementType != FlowElementType.CALL_ACTIVITY) {
            return instanceDataDAO.selectRecentOne(flowInstanceId);
        } else {
            FlowInstanceMappingPO flowInstanceMappingPO = flowInstanceMappingDAO.selectFlowInstanceMappingPO(flowInstanceId, nodeInstancePO.getNodeInstanceId());
            return select(flowInstanceMappingPO.getSubFlowInstanceId(), true);
        }
    }
}
