package com.xiaoju.uemc.turbo.engine.validator;

import com.xiaoju.uemc.turbo.engine.common.Constants;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.common.FlowElementType;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import com.xiaoju.uemc.turbo.engine.executor.*;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.util.FlowModelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;

/**
 * Created by Stefanie on 2019/12/1.
 */
@Component
public class ElementValidatorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElementValidatorFactory.class);

    @Resource
    private StartEventValidator startEventValidator;

    @Resource
    private EndEventValidator endEventValidator;

    @Resource
    private SequenceFlowValidator sequenceFlowValidator;

    @Resource
    private UserTaskValidator userTaskValidator;

    @Resource
    private ExclusiveGatewayValidator exclusiveGatewayValidator;

    public ElementValidator getElementValidator(FlowElement flowElement) throws ProcessException {
        int elementType = flowElement.getType();
        ElementValidator elementValidator = getElementValidator(elementType);

        if (elementValidator == null) {
            LOGGER.warn("getElementValidator failed: unsupported elementType.|elementType={}", elementType);
            throw new ProcessException(ErrorEnum.UNSUPPORTED_ELEMENT_TYPE,
                    MessageFormat.format(Constants.NODE_INFO_FORMAT, flowElement.getKey(),
                            FlowModelUtil.getElementName(flowElement), elementType));
        }
        return elementValidator;
    }

    private ElementValidator getElementValidator(int elementType) {
        switch (elementType) {
            case FlowElementType.START_EVENT:
                return startEventValidator;
            case FlowElementType.END_EVENT:
                return endEventValidator;
            case FlowElementType.SEQUENCE_FLOW:
                return sequenceFlowValidator;
            case FlowElementType.USER_TASK:
                return userTaskValidator;
            case FlowElementType.EXCLUSIVE_GATEWAY:
                return exclusiveGatewayValidator;
            default:
                return null;
        }
    }
}
