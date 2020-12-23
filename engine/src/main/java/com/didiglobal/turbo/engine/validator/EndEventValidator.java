package com.didiglobal.turbo.engine.validator;

import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.model.FlowElement;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class EndEventValidator extends ElementValidator {

    protected static final Logger LOGGER = LoggerFactory.getLogger(EndEventValidator.class);

    /**
     * CheckOutgoing: check endEvent's outgoing, warn while outgoing is not empty.
     *
     * @param flowElementMap, flowElement
     */
    @Override
    protected void checkOutgoing(Map<String, FlowElement> flowElementMap, FlowElement flowElement) {
        List<String> outgoing = flowElement.getOutgoing();

        if (CollectionUtils.isNotEmpty(outgoing)) {
            recordElementValidatorException(flowElement, ErrorEnum.ELEMENT_TOO_MUCH_OUTGOING);
        }
    }
}
