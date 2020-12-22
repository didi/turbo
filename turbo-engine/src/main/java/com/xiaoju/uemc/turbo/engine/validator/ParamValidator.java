package com.xiaoju.uemc.turbo.engine.validator;

import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.exception.ParamException;
import com.xiaoju.uemc.turbo.engine.param.*;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Stefanie on 2019/12/2.
 */
public class ParamValidator {

    private ParamValidator() {}

    public static void validate(StartProcessParam startProcessParam) throws ParamException {
        if (startProcessParam == null) {
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "param is null");
        }
        if (StringUtils.isBlank(startProcessParam.getFlowDeployId())
                && StringUtils.isBlank(startProcessParam.getFlowModuleId())) {
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "flowDeployId and flowModuleId is empty.");
        }
    }

    public static void validate(RuntimeTaskParam runtimeTaskParam) throws ParamException {
        if (runtimeTaskParam == null) {
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "param is null");
        }
        if (StringUtils.isBlank(runtimeTaskParam.getFlowInstanceId())
                || StringUtils.isBlank(runtimeTaskParam.getTaskInstanceId())) {
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "flowInstanceId and taskInstanceId is empty.");
        }
    }

    public static void validate(CreateFlowParam createFlowParam) throws ParamException {
        baseValidate(createFlowParam);
    }

    public static void validate(UpdateFlowParam updateFlowParam) throws ParamException {
        baseValidate(updateFlowParam);
        if (StringUtils.isBlank(updateFlowParam.getFlowModuleId())) {
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "flowModuleId is null");
        }
        if (StringUtils.isBlank(updateFlowParam.getFlowModel())) {
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "flowModel is null");
        }
    }

    public static void validate(GetFlowModuleParam getFlowModuleParam) throws ParamException {
        if (getFlowModuleParam == null) {
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "param is null");
        }
        if (StringUtils.isBlank(getFlowModuleParam.getFlowModuleId()) && StringUtils.isBlank(getFlowModuleParam.getFlowDeployId())) {
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "flowDeployId and flowModuleId is blank");
        }
    }

    public static void validate(DeployFlowParam deployFlowParam) throws ParamException {
        baseValidate(deployFlowParam);
        if (StringUtils.isBlank(deployFlowParam.getFlowModuleId())) {
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "flowModuleId is null");
        }
    }

    private static void baseValidate(CommonParam commonParam) throws ParamException {
        if (commonParam == null) {
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "param is null");
        }
        if (StringUtils.isBlank(commonParam.getTenant())) {
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "tenant is null");
        }
        if (StringUtils.isBlank(commonParam.getCaller())) {
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "caller is null");
        }
    }
}
