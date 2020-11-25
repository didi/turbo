package com.xiaoju.uemc.turbo.engine.processor;

import com.alibaba.fastjson.JSON;
import com.didiglobal.reportlogger.LoggerFactory;
import com.didiglobal.reportlogger.ReportLogger;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.common.FlowDefinitionStatus;
import com.xiaoju.uemc.turbo.engine.common.FlowDeploymentStatus;
import com.xiaoju.uemc.turbo.engine.common.FlowModuleEnum;
import com.xiaoju.uemc.turbo.engine.dao.FlowDefinitionDAO;
import com.xiaoju.uemc.turbo.engine.dao.FlowDeploymentDAO;
import com.xiaoju.uemc.turbo.engine.dto.*;
import com.xiaoju.uemc.turbo.engine.entity.FlowDefinitionPO;
import com.xiaoju.uemc.turbo.engine.entity.FlowDeploymentPO;
import com.xiaoju.uemc.turbo.engine.exception.BaseException;
import com.xiaoju.uemc.turbo.engine.exception.ParamException;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import com.xiaoju.uemc.turbo.engine.param.CreateFlowParam;
import com.xiaoju.uemc.turbo.engine.param.DeployFlowParam;
import com.xiaoju.uemc.turbo.engine.param.UpdateFlowParam;
import com.xiaoju.uemc.turbo.engine.util.IdGenerator;
import com.xiaoju.uemc.turbo.engine.util.StrongUuidGenerator;
import com.xiaoju.uemc.turbo.engine.validator.ModelValidator;
import com.xiaoju.uemc.turbo.engine.validator.ParamValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by Stefanie on 2019/12/1.
 */
@Component
public class DefinitionProcessor {

    private static final ReportLogger LOGGER = LoggerFactory.getLogger(DefinitionProcessor.class);

    private static final IdGenerator idGenerator = StrongUuidGenerator.getInstance();

    @Resource
    private ModelValidator modelValidator;

    @Resource
    private FlowDefinitionDAO flowDefinitionDAO;

    @Resource
    private FlowDeploymentDAO flowDeploymentDAO;

    public CreateFlowDTO create(CreateFlowParam createFlowParam) {
        CreateFlowDTO createFlowDTO = new CreateFlowDTO();
        try {
            ParamValidator.validate(createFlowParam);

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

            BeanUtils.copyProperties(flowDefinitionPO, createFlowDTO);
            fillCommonDTO(createFlowDTO, ErrorEnum.SUCCESS);
        } catch (ProcessException pe) {
            fillCommonDTO(createFlowDTO, pe.getErrNo(), pe.getErrMsg());
        }
        return createFlowDTO;
    }

    public UpdateFlowDTO update(UpdateFlowParam updateFlowParam) {
        UpdateFlowDTO updateFlowDTO = new UpdateFlowDTO();
        try {
            ParamValidator.validate(updateFlowParam);

            FlowDefinitionPO flowDefinitionPO = new FlowDefinitionPO();
            BeanUtils.copyProperties(updateFlowParam, flowDefinitionPO);
            flowDefinitionPO.setStatus(FlowDefinitionStatus.EDITING);
            flowDefinitionPO.setModifyTime(new Date());

            int rows = flowDefinitionDAO.updateByModuleId(flowDefinitionPO);
            if (rows <= 0) {
                throw new ProcessException(ErrorEnum.DEFINITION_UPDATE_INVALID);
            }
            fillCommonDTO(updateFlowDTO, ErrorEnum.SUCCESS);
        } catch (ProcessException pe) {
            fillCommonDTO(updateFlowDTO, pe.getErrNo(), pe.getErrMsg());
        }
        return updateFlowDTO;
    }

    public DeployFlowDTO deploy(DeployFlowParam deployFlowParam) {
        DeployFlowDTO deployFlowDTO = new DeployFlowDTO();
        try {
            ParamValidator.validate(deployFlowParam);

            FlowDefinitionPO flowDefinitionPO = flowDefinitionDAO.selectByModuleId(deployFlowParam.getFlowModuleId());
            if (null == flowDefinitionPO) {
                throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "flowModule is not exist");
            }

            Integer status = flowDefinitionPO.getStatus();
            if (status != FlowDefinitionStatus.EDITING) {
                throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "flowModule is not editing status");
            }

            String flowModel = flowDefinitionPO.getFlowModel();
            modelValidator.validate(flowModel);

            FlowDeploymentPO flowDeploymentPO = new FlowDeploymentPO();
            BeanUtils.copyProperties(flowDefinitionPO, flowDeploymentPO);
            String flowDeployId = idGenerator.getNextId();
            flowDeploymentPO.setFlowDeployId(flowDeployId);
            flowDeploymentPO.setStatus(FlowDeploymentStatus.DEPLOYED);

            int rows = flowDeploymentDAO.insert(flowDeploymentPO);
            if (rows <= 0) {
                throw new ProcessException(ErrorEnum.DEFINITION_INSERT_INVALID);
            }

            BeanUtils.copyProperties(flowDeploymentPO, deployFlowDTO);
            fillCommonDTO(deployFlowDTO, ErrorEnum.SUCCESS);
        } catch (BaseException be) {
            fillCommonDTO(deployFlowDTO, be.getErrNo(), be.getErrMsg());
        }
        return deployFlowDTO;
    }

    public FlowModuleDTO getFlowModule(String flowModuleId, String flowDeployId) {
        FlowModuleDTO flowModuleDTO = new FlowModuleDTO();
        try {
            ParamValidator.validate(flowModuleId, flowDeployId);
            if (StringUtils.isNotBlank(flowDeployId)) {
                flowModuleDTO = getFlowModuleByFlowDeployId(flowDeployId);
            } else {
                flowModuleDTO = getFlowModuleByFlowModuleId(flowModuleId);
            }
            fillCommonDTO(flowModuleDTO, ErrorEnum.SUCCESS);
        } catch (ProcessException pe) {
            fillCommonDTO(flowModuleDTO, pe.getErrNo(), pe.getErrMsg());
        }
        return flowModuleDTO;
    }

    private FlowModuleDTO getFlowModuleByFlowModuleId(String flowModuleId) throws ParamException {
        FlowDefinitionPO flowDefinitionPO = flowDefinitionDAO.selectByModuleId(flowModuleId);
        if (flowDefinitionPO == null) {
            LOGGER.warn("getFlowModuleByFlowModuleId failed: can not find flowDefinitionPO.||flowModuleId={}", flowModuleId);
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "flowDefinitionPO is not exist");
        }
        FlowModuleDTO flowModuleDTO = new FlowModuleDTO();
        BeanUtils.copyProperties(flowDefinitionPO, flowModuleDTO);
        Integer status = FlowModuleEnum.getStatusByDefinitionStatus(flowDefinitionPO.getStatus());
        flowModuleDTO.setStatus(status);
        LOGGER.info("getFlowModuleByFlowModuleId||flowModuleId={}||FlowModuleDTO={}", flowModuleId, JSON.toJSONString(flowModuleDTO));
        return flowModuleDTO;
    }

    private FlowModuleDTO getFlowModuleByFlowDeployId(String flowDeployId) throws ParamException {
        FlowDeploymentPO flowDeploymentPO = flowDeploymentDAO.selectByDeployId(flowDeployId);
        if (flowDeploymentPO == null) {
            LOGGER.warn("getFlowModuleByFlowDeployId failed: can not find flowDefinitionPO.||flowDeployId={}", flowDeployId);
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "flowDefinitionPO is not exist");
        }
        FlowModuleDTO flowModuleDTO = new FlowModuleDTO();
        BeanUtils.copyProperties(flowDeploymentPO, flowModuleDTO);
        Integer status = FlowModuleEnum.getStatusByDeploymentStatus(flowDeploymentPO.getStatus());
        flowModuleDTO.setStatus(status);
        LOGGER.info("getFlowModuleByFlowDeployId||flowDeployId={}||response={}", flowDeployId, JSON.toJSONString(flowModuleDTO));
        return flowModuleDTO;
    }

    private void fillCommonDTO(CommonDTO commonDTO, ErrorEnum errorEnum) {
        fillCommonDTO(commonDTO, errorEnum.getErrNo(), errorEnum.getErrMsg());
    }

    private void fillCommonDTO(CommonDTO commonDTO, int errNo, String errMsg) {
        commonDTO.setErrCode(errNo);
        commonDTO.setErrMsg(errMsg);
    }
}
