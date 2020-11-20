package com.xiaoju.uemc.turbo.engine.validator;

import com.alibaba.fastjson.JSON;
import com.xiaoju.uemc.turbo.engine.common.Constants;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.exception.ModelException;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.util.FlowModelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：optimus-prime
 * 类 名 称：Element1Validator
 * 类 描 述：
 * 创建时间：2019/12/10 9:53 AM
 * 创 建 人：didiwangxing
 */
@Component
public class ExclusiveGatewayValidator extends ElementValidator {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ExclusiveGatewayValidator.class);

    @Override
    protected void checkOutgoing(Map<String, FlowElement> flowElementMap, FlowElement flowElement) throws ModelException {
        List<String> outgoing = flowElement.getOutgoing();

        if (CollectionUtils.isEmpty(outgoing)) {
            LOGGER.warn("element lack outgoing.||flowElement={}", JSON.toJSONString(flowElement));
            throw new ModelException(ErrorEnum.ELEMENT_LACK_OUTGOING);
        }

        String elementName = FlowModelUtil.getElementName(flowElement);
        String elementkey = flowElement.getKey();
        List<String> outgoingList = flowElement.getOutgoing();
        int defaultConditionCount = 0;

        for (String outgoingKey : outgoingList) {
            FlowElement outgoingSequenceFlow = FlowModelUtil.getFlowElement(flowElementMap, outgoingKey);

            String condition = FlowModelUtil.getConditionFromSequenceFlow(outgoingSequenceFlow);
            boolean isDefaultCondition = FlowModelUtil.isDefaultCondition(outgoingSequenceFlow);

            if (StringUtils.isBlank(condition) && !isDefaultCondition) {
                String exceptionMsg = MessageFormat.format(Constants.MODEL_DEFINITION_ERROR_MSG_FORMAT,
                        ErrorEnum.EMPTY_SEQUENCE_OUTGOING.getErrMsg(), elementName, elementkey);
                LOGGER.error(exceptionMsg);
                throw new ModelException(ErrorEnum.EMPTY_SEQUENCE_OUTGOING.getErrNo(), exceptionMsg);
            }
            if (isDefaultCondition) {
                defaultConditionCount++;
            }
        }

        if (defaultConditionCount > 1) {
            String exceptionMsg = MessageFormat.format(Constants.MODEL_DEFINITION_ERROR_MSG_FORMAT,
                    ErrorEnum.TOO_MANY_DEFAULT_SEQUENCE.getErrMsg(), elementName, elementkey);
            LOGGER.error(exceptionMsg);
            throw new ModelException(ErrorEnum.TOO_MANY_DEFAULT_SEQUENCE.getErrNo(), exceptionMsg);
        }
    }
}
