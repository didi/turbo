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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Map;

@Service
public class InstanceDataService {

    @Resource
    private InstanceDataDAO instanceDataDAO;

    @Resource
    private ProcessInstanceDAO processInstanceDAO;

    @Resource
    private FlowDeploymentDAO flowDeploymentDAO;

    @Resource
    private NodeInstanceDAO nodeInstanceDAO;

    @Resource
    private FlowInstanceMappingDAO flowInstanceMappingDAO;

    @Resource
    private FlowInstanceService flowInstanceService;

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
