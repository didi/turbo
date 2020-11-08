package com.xiaoju.uemc.turbo.engine.validator;

import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.exception.ModelException;
import com.xiaoju.uemc.turbo.engine.model.FlowModel;
import com.xiaoju.uemc.turbo.engine.util.FlowModelUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Stefanie on 2019/12/2.
 */
public class ModelValidator {

    public static void validate(String flowModelStr) throws ModelException {
        if (StringUtils.isBlank(flowModelStr)) {
            throw new ModelException(ErrorEnum.MODEL_EMPTY);
        }

        FlowModel flowModel = FlowModelUtil.parseModelFromString(flowModelStr);

        FlowModelValidator.validate(flowModel);
        ElementValidator.validate(flowModel.getFlowElementList());
    }
}
