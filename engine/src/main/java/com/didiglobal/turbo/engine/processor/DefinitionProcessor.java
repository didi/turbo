package com.didiglobal.turbo.engine.processor;

import com.alibaba.fastjson.JSON;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.common.FlowDefinitionStatus;
import com.didiglobal.turbo.engine.common.FlowDeploymentStatus;
import com.didiglobal.turbo.engine.common.FlowModuleEnum;
import com.didiglobal.turbo.engine.dao.FlowDefinitionDAO;
import com.didiglobal.turbo.engine.dao.FlowDeploymentDAO;
import com.didiglobal.turbo.engine.entity.FlowDefinitionPO;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import com.didiglobal.turbo.engine.exception.DefinitionException;
import com.didiglobal.turbo.engine.exception.ParamException;
import com.didiglobal.turbo.engine.exception.TurboException;
import com.didiglobal.turbo.engine.param.CreateFlowParam;
import com.didiglobal.turbo.engine.param.DeployFlowParam;
import com.didiglobal.turbo.engine.param.GetFlowModuleParam;
import com.didiglobal.turbo.engine.param.UpdateFlowParam;
import com.didiglobal.turbo.engine.result.*;
import com.didiglobal.turbo.engine.util.IdGenerator;
import com.didiglobal.turbo.engine.util.StrongUuidGenerator;
import com.didiglobal.turbo.engine.validator.ModelValidator;
import com.didiglobal.turbo.engine.validator.ParamValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class DefinitionProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefinitionProcessor.class);

    private static final IdGenerator idGenerator = new StrongUuidGenerator();

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
            Date date = new Date();
            flowDefinitionPO.setCreateTime(date);
            flowDefinitionPO.setModifyTime(date);

            int rows = flowDefinitionDAO.insert(flowDefinitionPO);
            if (rows != 1) {
                LOGGER.warn("create flow failed: insert to db failed.||createFlowParam={}", createFlowParam);
                throw new DefinitionException(ErrorEnum.DEFINITION_INSERT_INVALID);
            }

            BeanUtils.copyProperties(flowDefinitionPO, createFlowResult);
            fillCommonResult(createFlowResult, ErrorEnum.SUCCESS);
        } catch (TurboException te) {
            fillCommonResult(createFlowResult, te);
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
            if (rows != 1) {
                LOGGER.warn("update flow failed: update to db failed.||updateFlowParam={}", updateFlowParam);
                throw new DefinitionException(ErrorEnum.DEFINITION_UPDATE_INVALID);
            }
            fillCommonResult(updateFlowResult, ErrorEnum.SUCCESS);
        } catch (TurboException te) {
            fillCommonResult(updateFlowResult, te);
        }
        return updateFlowResult;
    }

    public DeployFlowResult deploy(DeployFlowParam deployFlowParam) {
        DeployFlowResult deployFlowResult = new DeployFlowResult();
        try {
            ParamValidator.validate(deployFlowParam);

            FlowDefinitionPO flowDefinitionPO = flowDefinitionDAO.selectByModuleId(deployFlowParam.getFlowModuleId());
            if (null == flowDefinitionPO) {
                LOGGER.warn("deploy flow failed: flow is not exist.||deployFlowParam={}", deployFlowParam);
                throw new DefinitionException(ErrorEnum.FLOW_NOT_EXIST);
            }

            Integer status = flowDefinitionPO.getStatus();
            if (status != FlowDefinitionStatus.EDITING) {
                LOGGER.warn("deploy flow failed: flow is not editing status.||deployFlowParam={}", deployFlowParam);
                throw new DefinitionException(ErrorEnum.FLOW_NOT_EDITING);
            }

            String flowModel = flowDefinitionPO.getFlowModel();
            modelValidator.validate(flowModel);

            FlowDeploymentPO flowDeploymentPO = new FlowDeploymentPO();
            BeanUtils.copyProperties(flowDefinitionPO, flowDeploymentPO);
            String flowDeployId = idGenerator.getNextId();
            flowDeploymentPO.setFlowDeployId(flowDeployId);
            flowDeploymentPO.setStatus(FlowDeploymentStatus.DEPLOYED);

            int rows = flowDeploymentDAO.insert(flowDeploymentPO);
            if (rows != 1) {
                LOGGER.warn("deploy flow failed: insert to db failed.||deployFlowParam={}", deployFlowParam);
                throw new DefinitionException(ErrorEnum.DEFINITION_INSERT_INVALID);
            }

            BeanUtils.copyProperties(flowDeploymentPO, deployFlowResult);
            fillCommonResult(deployFlowResult, ErrorEnum.SUCCESS);
        } catch (TurboException te) {
            fillCommonResult(deployFlowResult, te);
        }
        return deployFlowResult;
    }

    public FlowModuleResult getFlowModule(GetFlowModuleParam getFlowModuleParam) {
        FlowModuleResult flowModuleResult = new FlowModuleResult();
        try {
            ParamValidator.validate(getFlowModuleParam);
            String flowModuleId = getFlowModuleParam.getFlowModuleId();
            String flowDeployId = getFlowModuleParam.getFlowDeployId();
            if (StringUtils.isNotBlank(flowDeployId)) {
                flowModuleResult = getFlowModuleByFlowDeployId(flowDeployId);
            } else {
                flowModuleResult = getFlowModuleByFlowModuleId(flowModuleId);
            }
            fillCommonResult(flowModuleResult, ErrorEnum.SUCCESS);
        } catch (TurboException te) {
            fillCommonResult(flowModuleResult, te);
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

    private void fillCommonResult(CommonResult commonResult, ErrorEnum errorEnum) {
        fillCommonResult(commonResult, errorEnum.getErrNo(), errorEnum.getErrMsg());
    }

    private void fillCommonResult(CommonResult commonResult, TurboException turboException) {
        fillCommonResult(commonResult, turboException.getErrNo(), turboException.getErrMsg());
    }

    private void fillCommonResult(CommonResult commonResult, int errNo, String errMsg) {
        commonResult.setErrCode(errNo);
        commonResult.setErrMsg(errMsg);
    }
}
