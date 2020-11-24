package com.xiaoju.uemc.turbo.engine.validator;

import com.didiglobal.reportlogger.LoggerFactory;
import com.didiglobal.reportlogger.ReportLogger;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.exception.ModelException;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import com.xiaoju.uemc.turbo.engine.model.FlowModel;
import com.xiaoju.uemc.turbo.engine.util.FlowModelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by Stefanie on 2019/12/2.
 */
@Component
public class ModelValidator {

    protected static final ReportLogger LOGGER = LoggerFactory.getLogger(FlowModelValidator.class);

    @Resource
    private FlowModelValidator flowModelValidator;

    public void validate(String flowModelStr) throws ModelException, ProcessException {
        if (StringUtils.isBlank(flowModelStr)) {
            LOGGER.error(ErrorEnum.MODEL_EMPTY.getErrMsg());
            throw new ModelException(ErrorEnum.MODEL_EMPTY);
        }

        FlowModel flowModel = FlowModelUtil.parseModelFromString(flowModelStr);
        if (flowModel == null || CollectionUtils.isEmpty(flowModel.getFlowElementList())) {
            LOGGER.error(ErrorEnum.MODEL_EMPTY.getErrMsg());
            throw new ModelException(ErrorEnum.MODEL_EMPTY);
        }
        flowModelValidator.validate(flowModel);
    }
}
