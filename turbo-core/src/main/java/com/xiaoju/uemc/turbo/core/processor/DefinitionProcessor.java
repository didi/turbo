package com.xiaoju.uemc.turbo.core.processor;

import com.alibaba.fastjson.JSON;
import com.didiglobal.reportlogger.LoggerFactory;
import com.didiglobal.reportlogger.ReportLogger;
import com.xiaoju.uemc.turbo.core.common.ErrorEnum;
import com.xiaoju.uemc.turbo.core.common.FlowDefinitionStatus;
import com.xiaoju.uemc.turbo.core.common.FlowDeploymentStatus;
import com.xiaoju.uemc.turbo.core.common.FlowModuleEnum;
import com.xiaoju.uemc.turbo.core.dao.FlowDefinitionDAO;
import com.xiaoju.uemc.turbo.core.dao.FlowDeploymentDAO;
import com.xiaoju.uemc.turbo.core.dto.CreateFlowDTO;
import com.xiaoju.uemc.turbo.core.dto.DeployFlowDTO;
import com.xiaoju.uemc.turbo.core.dto.FlowModuleDTO;
import com.xiaoju.uemc.turbo.core.entity.FlowDefinitionPO;
import com.xiaoju.uemc.turbo.core.entity.FlowDeploymentPO;
import com.xiaoju.uemc.turbo.core.exception.ParamException;
import com.xiaoju.uemc.turbo.core.param.CreateFlowParam;
import com.xiaoju.uemc.turbo.core.param.DeployFlowParam;
import com.xiaoju.uemc.turbo.core.param.UpdateFlowParam;
import com.xiaoju.uemc.turbo.core.util.StrongUuidGenerator;
import com.xiaoju.uemc.turbo.core.validator.ModelValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Stefanie on 2019/12/1.
 */
@Component
public class DefinitionProcessor {

    private static final ReportLogger LOGGER = LoggerFactory.getLogger(DefinitionProcessor.class);

    private static final StrongUuidGenerator idGenerator = StrongUuidGenerator.getInstance();

    @Autowired
    FlowDefinitionDAO flowDefinitionDAO;

    @Autowired
    FlowDeploymentDAO flowDeploymentDAO;

    public CreateFlowDTO create(CreateFlowParam createFlowParam) throws Exception {
        FlowDefinitionPO flowDefinitionPO = new FlowDefinitionPO();
        BeanUtils.copyProperties(createFlowParam, flowDefinitionPO);
        String flowModuleId = idGenerator.getNextId();
        flowDefinitionPO.setFlowModuleId(flowModuleId);
        flowDefinitionPO.setStatus(FlowDefinitionStatus.INIT);
        flowDefinitionPO.setCreateTime(new Date());
        flowDefinitionPO.setModifyTime(new Date());
        int rows = flowDefinitionDAO.insert(flowDefinitionPO);
        if (rows <= 0) {
            throw new ParamException(ErrorEnum.DEFINITION_INSERT_INVALID);
        }
        CreateFlowDTO createFlowDTO = new CreateFlowDTO();
        BeanUtils.copyProperties(flowDefinitionPO, createFlowDTO);
        return createFlowDTO;
    }

    // TODO: 2019/12/2  Attention: update tenantId in FlowDeployment while update tenantId // why？？？
    public boolean update(UpdateFlowParam updateFlowParam) throws ParamException {
        FlowDefinitionPO flowDefinitionPO = new FlowDefinitionPO();
        BeanUtils.copyProperties(updateFlowParam, flowDefinitionPO);
        flowDefinitionPO.setStatus(FlowDefinitionStatus.EDITING);
        flowDefinitionPO.setModifyTime(new Date());
        int rows = flowDefinitionDAO.updateByModuleId(flowDefinitionPO);
        if (rows <= 0) {
            throw new ParamException(ErrorEnum.DEFINITION_UPDATE_INVALID);
        }
        return true;
    }

    public DeployFlowDTO deploy(DeployFlowParam deployFlowParam) throws Exception {
        FlowDefinitionPO flowDefinitionPO = flowDefinitionDAO.selectByModuleId(deployFlowParam.getFlowModuleId());
        if (null == flowDefinitionPO) {
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "flowModule is not exist");
        }

        Integer status = flowDefinitionPO.getStatus();
        if (status != FlowDefinitionStatus.EDITING) {
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "flowModule is not editing status");
        }

        String flowModel = flowDefinitionPO.getFlowModel();
        ModelValidator.validate(flowModel);
        FlowDeploymentPO flowDeploymentPO = new FlowDeploymentPO();
        BeanUtils.copyProperties(flowDefinitionPO, flowDeploymentPO);
        String flowDeployId = idGenerator.getNextId();
        flowDeploymentPO.setFlowDeployId(flowDeployId);
        flowDeploymentPO.setStatus(FlowDeploymentStatus.DEPLOYED);
        int rows = flowDeploymentDAO.insert(flowDeploymentPO);
        if (rows <= 0) {
            throw new ParamException(ErrorEnum.DEFINITION_INSERT_INVALID);
        }
        DeployFlowDTO deployFlowDTO = new DeployFlowDTO();
        BeanUtils.copyProperties(flowDeploymentPO, deployFlowDTO);
        return deployFlowDTO;
    }

    public FlowModuleDTO getFlowModule(String flowModuleId, String flowDeployId) {
        if (StringUtils.isNotBlank(flowDeployId)) {
            return getFlowModuleByFlowDeployId(flowDeployId);
        } else {
            return getFlowModuleByFlowModuleId(flowModuleId);
        }
    }

    private FlowModuleDTO getFlowModuleByFlowModuleId(String flowModuleId) {
        FlowDefinitionPO flowDefinitionPO = flowDefinitionDAO.selectByModuleId(flowModuleId);
        if (flowDefinitionPO == null) {
            LOGGER.warn("getFlowModuleByFlowModuleId failed: flowDefinitionPO is null.||flowModuleId={}", flowModuleId);
            return null;
        }
        FlowModuleDTO flowModuleDTO = new FlowModuleDTO();
        BeanUtils.copyProperties(flowDefinitionPO, flowModuleDTO);
        Integer status = FlowModuleEnum.getStatusByDefinitionStatus(flowDefinitionPO.getStatus());
        flowModuleDTO.setStatus(status);
        LOGGER.info("getFlowModuleByFlowModuleId||flowModuleId={}||FlowModuleDTO={}", flowModuleId, JSON.toJSONString(flowModuleDTO));
        return flowModuleDTO;
    }

    private FlowModuleDTO getFlowModuleByFlowDeployId(String flowDeployId) {
        FlowDeploymentPO flowDeploymentPO = flowDeploymentDAO.selectByDeployId(flowDeployId);
        if (flowDeploymentPO == null) {
            LOGGER.warn("getFlowModuleByFlowDeployId failed: flowDeploymentPO is null.||flowDeployId={}", flowDeployId);
            return null;
        }
        FlowModuleDTO flowModuleDTO = new FlowModuleDTO();
        BeanUtils.copyProperties(flowDeploymentPO, flowModuleDTO);
        Integer status = FlowModuleEnum.getStatusByDeploymentStatus(flowDeploymentPO.getStatus());
        flowModuleDTO.setStatus(status);
        LOGGER.info("getFlowModuleByFlowDeployId||flowDeployId={}||response={}", flowDeployId, JSON.toJSONString(flowModuleDTO));
        return flowModuleDTO;
    }
}
