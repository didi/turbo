package com.xiaoju.uemc.turbo.engine.validator;

import com.google.common.collect.Maps;
import com.xiaoju.uemc.turbo.engine.common.Constants;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.common.FlowElementType;
import com.xiaoju.uemc.turbo.engine.exception.DefinitionException;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.model.FlowModel;
import com.xiaoju.uemc.turbo.engine.util.FlowModelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：optimus-prime
 * 类 名 称：FlowModelValidator
 * 类 描 述：
 * 创建时间：2019/12/9 11:00 AM
 * 创 建 人：didiwangxing
 */
@Component
public class FlowModelValidator {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FlowModelValidator.class);

    @Resource
    private ElementValidatorFactory elementValidatorFactory;

    public void validate(FlowModel flowModel) throws ProcessException, DefinitionException {
        if (flowModel == null || CollectionUtils.isEmpty(flowModel.getFlowElementList())) {
            LOGGER.warn("message={}", ErrorEnum.MODEL_EMPTY.getErrMsg());
            throw new DefinitionException(ErrorEnum.MODEL_EMPTY);
        }

        List<FlowElement> flowElementList = flowModel.getFlowElementList();
        Map<String, FlowElement> flowElementMap = Maps.newHashMap();

        for(FlowElement flowElement : flowElementList) {
            if (flowElementMap.containsKey(flowElement.getKey())) {
                String elementName = FlowModelUtil.getElementName(flowElement);
                String elementKey = flowElement.getKey();
                String exceptionMsg = MessageFormat.format(Constants.MODEL_DEFINITION_ERROR_MSG_FORMAT, ErrorEnum.ELEMENT_KEY_NOT_UNIQUE, elementName, elementKey);
                LOGGER.warn(exceptionMsg);
                throw new DefinitionException(ErrorEnum.ELEMENT_KEY_NOT_UNIQUE.getErrNo(), exceptionMsg);
            }
            flowElementMap.put(flowElement.getKey(), flowElement);
        }

        int startEventCount = 0;
        int endEventCount = 0;

        for (FlowElement flowElement : flowElementList) {

            ElementValidator elementValidator = elementValidatorFactory.getElementValidator(flowElement);
            elementValidator.validator(flowElementMap, flowElement);

            if (FlowElementType.START_EVENT == flowElement.getType()) {
                startEventCount++;
            }

            if (FlowElementType.END_EVENT == flowElement.getType()) {
                endEventCount++;
            }
        }

        if (startEventCount != 1) {
            LOGGER.warn("message={}||startEventCount={}", ErrorEnum.START_NODE_INVALID.getErrMsg(), startEventCount);
            throw new DefinitionException(ErrorEnum.START_NODE_INVALID);
        }

        if (endEventCount < 1) {
            LOGGER.warn("message={}", ErrorEnum.END_NODE_INVALID.getErrMsg());
            throw new DefinitionException(ErrorEnum.END_NODE_INVALID);
        }
    }
}
