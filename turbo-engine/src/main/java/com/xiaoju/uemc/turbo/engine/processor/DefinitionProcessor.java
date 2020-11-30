package com.xiaoju.uemc.turbo.engine.processor;

import com.alibaba.fastjson.JSON;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
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
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by Stefanie on 2019/12/1.
 */
@Component
public class DefinitionProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefinitionProcessor.class);

    private static final IdGenerator idGenerator = StrongUuidGenerator.getInstance();

    @Resource
    private ModelValidator modelValidator;

    @Resource
    private FlowDefinitionDAO flowDefinitionDAO;

    @Resource
    private FlowDeploymentDAO flowDeploymentDAO;

    public CreateFlowResult create(CreateFlowParam createFlowParam) {
        CreateFlowResult createFlowResult = new CreateFlowResult();
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

            BeanUtils.copyProperties(flowDefinitionPO, createFlowResult);
            fillCommonDTO(createFlowResult, ErrorEnum.SUCCESS);
        } catch (ProcessException pe) {
            fillCommonDTO(createFlowResult, pe.getErrNo(), pe.getErrMsg());
        }
        return createFlowResult;
    }

    public UpdateFlowResult update(UpdateFlowParam updateFlowParam) {
        UpdateFlowResult updateFlowResult = new UpdateFlowResult();
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
            fillCommonDTO(updateFlowResult, ErrorEnum.SUCCESS);
        } catch (ProcessException pe) {
            fillCommonDTO(updateFlowResult, pe.getErrNo(), pe.getErrMsg());
        }
        return updateFlowResult;
    }

    public DeployFlowResult deploy(DeployFlowParam deployFlowParam) {
        DeployFlowResult deployFlowResult = new DeployFlowResult();
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

            BeanUtils.copyProperties(flowDeploymentPO, deployFlowResult);
            fillCommonDTO(deployFlowResult, ErrorEnum.SUCCESS);
        } catch (BaseException be) {
            fillCommonDTO(deployFlowResult, be.getErrNo(), be.getErrMsg());
        }
        return deployFlowResult;
    }

    public FlowModuleResult getFlowModule(String flowModuleId, String flowDeployId) {
        FlowModuleResult flowModuleResult = new FlowModuleResult();
        try {
            ParamValidator.validate(flowModuleId, flowDeployId);
            if (StringUtils.isNotBlank(flowDeployId)) {
                flowModuleResult = getFlowModuleByFlowDeployId(flowDeployId);
            } else {
                flowModuleResult = getFlowModuleByFlowModuleId(flowModuleId);
            }
            fillCommonDTO(flowModuleResult, ErrorEnum.SUCCESS);
        } catch (ProcessException pe) {
            fillCommonDTO(flowModuleResult, pe.getErrNo(), pe.getErrMsg());
        }
        return flowModuleResult;
    }

    private FlowModuleResult getFlowModuleByFlowModuleId(String flowModuleId) throws ParamException {
        FlowDefinitionPO flowDefinitionPO = flowDefinitionDAO.selectByModuleId(flowModuleId);
        if (flowDefinitionPO == null) {
            LOGGER.warn("getFlowModuleByFlowModuleId failed: can not find flowDefinitionPO.||flowModuleId={}", flowModuleId);
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "flowDefinitionPO is not exist");
        }
        FlowModuleResult flowModuleResult = new FlowModuleResult();
        BeanUtils.copyProperties(flowDefinitionPO, flowModuleResult);
        Integer status = FlowModuleEnum.getStatusByDefinitionStatus(flowDefinitionPO.getStatus());
        flowModuleResult.setStatus(status);
        LOGGER.info("getFlowModuleByFlowModuleId||flowModuleId={}||FlowModuleResult={}", flowModuleId, JSON.toJSONString(flowModuleResult));
        return flowModuleResult;
    }

    private FlowModuleResult getFlowModuleByFlowDeployId(String flowDeployId) throws ParamException {
        FlowDeploymentPO flowDeploymentPO = flowDeploymentDAO.selectByDeployId(flowDeployId);
        if (flowDeploymentPO == null) {
            LOGGER.warn("getFlowModuleByFlowDeployId failed: can not find flowDefinitionPO.||flowDeployId={}", flowDeployId);
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "flowDefinitionPO is not exist");
        }
        FlowModuleResult flowModuleResult = new FlowModuleResult();
        BeanUtils.copyProperties(flowDeploymentPO, flowModuleResult);
        Integer status = FlowModuleEnum.getStatusByDeploymentStatus(flowDeploymentPO.getStatus());
        flowModuleResult.setStatus(status);
        LOGGER.info("getFlowModuleByFlowDeployId||flowDeployId={}||response={}", flowDeployId, JSON.toJSONString(flowModuleResult));
        return flowModuleResult;
    }

    private void fillCommonDTO(CommonResult commonResult, ErrorEnum errorEnum) {
        fillCommonDTO(commonResult, errorEnum.getErrNo(), errorEnum.getErrMsg());
    }

    private void fillCommonDTO(CommonResult commonResult, int errNo, String errMsg) {
        commonResult.setErrCode(errNo);
        commonResult.setErrMsg(errMsg);
    }
}
