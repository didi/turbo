package com.xiaoju.uemc.turbo.core.validator;

import com.xiaoju.uemc.turbo.core.common.ErrorEnum;
import com.xiaoju.uemc.turbo.core.exception.ModelException;
import com.xiaoju.uemc.turbo.core.model.FlowModel;
import com.xiaoju.uemc.turbo.core.util.FlowModelUtil;
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
