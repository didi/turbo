package com.didiglobal.turbo.engine.validator;

import com.didiglobal.turbo.engine.common.Constants;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.common.FlowElementType;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.plugin.ElementPlugin;
import com.didiglobal.turbo.engine.plugin.manager.PluginManager;
import com.didiglobal.turbo.engine.util.FlowModelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElementValidatorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElementValidatorFactory.class);

    private final StartEventValidator startEventValidator;

    private final EndEventValidator endEventValidator;

    private final SequenceFlowValidator sequenceFlowValidator;

    private final UserTaskValidator userTaskValidator;

    private final ExclusiveGatewayValidator exclusiveGatewayValidator;

    private final CallActivityValidator callActivityValidator;

    private final PluginManager pluginManager;

    private final Map<Integer, ElementValidator> validatorMap = new HashMap<>(16);

    public ElementValidatorFactory(StartEventValidator startEventValidator, EndEventValidator endEventValidator, SequenceFlowValidator sequenceFlowValidator, UserTaskValidator userTaskValidator, ExclusiveGatewayValidator exclusiveGatewayValidator, CallActivityValidator callActivityValidator, PluginManager pluginManager) {
        this.startEventValidator = startEventValidator;
        this.endEventValidator = endEventValidator;
        this.sequenceFlowValidator = sequenceFlowValidator;
        this.userTaskValidator = userTaskValidator;
        this.exclusiveGatewayValidator = exclusiveGatewayValidator;
        this.callActivityValidator = callActivityValidator;
        this.pluginManager = pluginManager;
        init();
    }

    /**
     * 将原生校验器与插件扩展校验器汇总
     * 插件扩展校验器可以通过设置与原生校验器相同的elementType值进行覆盖
     */
    public void init() {
        validatorMap.put(FlowElementType.SEQUENCE_FLOW, sequenceFlowValidator);
        validatorMap.put(FlowElementType.START_EVENT, startEventValidator);
        validatorMap.put(FlowElementType.END_EVENT, endEventValidator);
        validatorMap.put(FlowElementType.USER_TASK, userTaskValidator);
        validatorMap.put(FlowElementType.EXCLUSIVE_GATEWAY, exclusiveGatewayValidator);
        validatorMap.put(FlowElementType.CALL_ACTIVITY, callActivityValidator);

        List<ElementPlugin> elementPlugins = pluginManager.getPluginsFor(ElementPlugin.class);
        elementPlugins.forEach(elementPlugin -> validatorMap.put(elementPlugin.getFlowElementType(), elementPlugin.getElementValidator()));
    }

    public ElementValidator getElementValidator(FlowElement flowElement) throws ProcessException {
        int elementType = flowElement.getType();
        ElementValidator elementValidator = getElementValidator(elementType);

        if (elementValidator == null) {
            LOGGER.warn("getElementValidator failed: unsupported elementType.||elementType={}", elementType);
            throw new ProcessException(ErrorEnum.UNSUPPORTED_ELEMENT_TYPE,
                    MessageFormat.format(Constants.NODE_INFO_FORMAT, flowElement.getKey(),
                            FlowModelUtil.getElementName(flowElement), elementType));
        }
        return elementValidator;
    }

    private ElementValidator getElementValidator(int elementType) {
        return validatorMap.get(elementType);
    }
}
