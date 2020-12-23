package com.didiglobal.turbo.engine.validator;

import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.exception.DefinitionException;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.util.FlowModelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ExclusiveGatewayValidator extends ElementValidator {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ExclusiveGatewayValidator.class);

    @Override
    protected void checkOutgoing(Map<String, FlowElement> flowElementMap, FlowElement flowElement) throws DefinitionException {
        List<String> outgoing = flowElement.getOutgoing();

        if (CollectionUtils.isEmpty(outgoing)) {
            throwElementValidatorException(flowElement, ErrorEnum.ELEMENT_LACK_OUTGOING);
        }

        List<String> outgoingList = flowElement.getOutgoing();
        int defaultConditionCount = 0;

        for (String outgoingKey : outgoingList) {
            FlowElement outgoingSequenceFlow = FlowModelUtil.getFlowElement(flowElementMap, outgoingKey);

            String condition = FlowModelUtil.getConditionFromSequenceFlow(outgoingSequenceFlow);
            boolean isDefaultCondition = FlowModelUtil.isDefaultCondition(outgoingSequenceFlow);

            if (StringUtils.isBlank(condition) && !isDefaultCondition) {
                throwElementValidatorException(flowElement, ErrorEnum.EMPTY_SEQUENCE_OUTGOING);
            }
            if (isDefaultCondition) {
                defaultConditionCount++;
            }
        }

        if (defaultConditionCount > 1) {
            throwElementValidatorException(flowElement, ErrorEnum.TOO_MANY_DEFAULT_SEQUENCE);
        }
    }
}
