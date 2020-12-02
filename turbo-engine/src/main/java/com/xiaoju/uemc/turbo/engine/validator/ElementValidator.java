package com.xiaoju.uemc.turbo.engine.validator;

import com.xiaoju.uemc.turbo.engine.common.Constants;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.exception.DefinitionException;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.util.FlowModelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class ElementValidator {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ElementValidator.class);

    protected void checkIncoming(Map<String, FlowElement> flowElementMap, FlowElement flowElement) throws DefinitionException {
        List<String> incomingList = flowElement.getIncoming();

        if (CollectionUtils.isEmpty(incomingList)) {
            throwElementValidatorException(flowElement, ErrorEnum.ELEMENT_LACK_INCOMING);
        }
    }

    protected void checkOutgoing(Map<String, FlowElement> flowElementMap, FlowElement flowElement) throws DefinitionException {
        List<String> outgoingList = flowElement.getOutgoing();

        if (CollectionUtils.isEmpty(outgoingList)) {
            throwElementValidatorException(flowElement, ErrorEnum.ELEMENT_LACK_OUTGOING);
        }
    }

    protected void validator(Map<String, FlowElement> flowElementMap, FlowElement flowElement) throws DefinitionException {
        checkIncoming(flowElementMap, flowElement);
        checkOutgoing(flowElementMap, flowElement);
    }

    protected void throwElementValidatorException(FlowElement flowElement, ErrorEnum errorEnum) throws DefinitionException {
        String exceptionMsg = getElementValidatorExceptionMsg(flowElement, errorEnum);
        LOGGER.warn(exceptionMsg);
        throw new DefinitionException(errorEnum.getErrNo(), exceptionMsg);
    }

    protected String getElementValidatorExceptionMsg(FlowElement flowElement, ErrorEnum errorEnum) {
        String elementName = FlowModelUtil.getElementName(flowElement);
        String elementKey = flowElement.getKey();
        return MessageFormat.format(Constants.MODEL_DEFINITION_ERROR_MSG_FORMAT, errorEnum, elementName, elementKey);
    }
}
