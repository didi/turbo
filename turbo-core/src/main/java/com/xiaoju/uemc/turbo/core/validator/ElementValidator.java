package com.xiaoju.uemc.turbo.core.validator;

import com.didiglobal.reportlogger.LoggerFactory;
import com.didiglobal.reportlogger.ReportLogger;
import com.xiaoju.uemc.turbo.core.common.Constants;
import com.xiaoju.uemc.turbo.core.common.ErrorEnum;
import com.xiaoju.uemc.turbo.core.common.FlowElementType;
import com.xiaoju.uemc.turbo.core.exception.ModelException;
import com.xiaoju.uemc.turbo.core.model.FlowElement;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：optimus-prime
 * 类 名 称：ElementValidator
 * 类 描 述：
 * 创建时间：2019/12/10 9:53 AM
 * 创 建 人：didiwangxing
 */
public class ElementValidator {

    protected static final ReportLogger LOGGER = LoggerFactory.getLogger(FlowModelValidator.class);

    public static void validate(List<FlowElement> flowElementList) throws ModelException {

        HashMap<String, FlowElement> flowElementMap = new HashMap<>();

        for (FlowElement flowElement : flowElementList) {
            flowElementMap.put(flowElement.getKey(), flowElement);
        }

        for (FlowElement flowElement : flowElementList) {
            int elementType = flowElement.getType();
            String elementName = (String) flowElement.getProperties().getOrDefault("name", StringUtils.EMPTY);
            String elementKey = flowElement.getKey();
            switch (elementType) { // TODO fill
                case FlowElementType.START_EVENT:
                    // 不可有incoming
                    if (flowElement.getIncoming() != null && flowElement.getIncoming().size() > 0) {
//                    throw new ModelException(ErrorEnum.MODEL_INVALID.getErrNo(), "startNode should not have incoming");
                        LOGGER.warn("startNode should not have incoming");
                    }
                    checkOutgoing(flowElement);
                    break;
                case FlowElementType.END_EVENT:
                    // 不可有outgoing
                    if (flowElement.getOutgoing() != null && flowElement.getOutgoing().size() > 0) {
//                    throw new ModelException(ErrorEnum.MODEL_INVALID.getErrNo(), "endNode should not have outgoing");
                        LOGGER.warn("endNode should not have outgoing");
                    }
                    break;
                case FlowElementType.SEQUENCE_FLOW:
                    checkOutgoing(flowElement);
                    break;
                case FlowElementType.USER_TASK:
//                    checkOutgoing(flowElement);
                    break;
                case FlowElementType.SERVICE_TASK:
                    checkOutgoing(flowElement);
                    break;
                case FlowElementType.EXCLUSIVE_GATEWAY:
                    // 排他网关节点的outgoing不可以为空
                    // 排他网关节点的outgoing最多可以一条"default"边，并且不可以有空边
                    List<String> outgoing = flowElement.getOutgoing();
                    if (CollectionUtils.isEmpty(outgoing)) {
                        String exceptionMsg = MessageFormat.format(Constants.MODEL_DEFINITION_ERROR_MSG_FORMAT,
                                ErrorEnum.GATEWAY_NO_OUTGOING.getErrMsg(), elementName, elementKey);
                        LOGGER.error(exceptionMsg);
                        throw new ModelException(ErrorEnum.GATEWAY_NO_OUTGOING.getErrNo(), exceptionMsg);
                    }
                    int elseNum = 0;
                    for (String k : outgoing) {
                        FlowElement e = flowElementMap.get(k);
                        Map<String, Object> properties = e.getProperties();
                        String conditions = (String) properties.get("conditionsequenceflow");
                        String isDefaultStr = (String) properties.getOrDefault("defaultConditions", "false");
                        boolean isDefault = Boolean.valueOf(isDefaultStr.trim());
                        if (StringUtils.isBlank(conditions) && !isDefault) {
                            String exceptionMsg = MessageFormat.format(Constants.MODEL_DEFINITION_ERROR_MSG_FORMAT,
                                    ErrorEnum.GATEWAY_EMPTY_EDGE_OUTGOING.getErrMsg(), elementName, elementKey);
                            LOGGER.error(exceptionMsg);
                            throw new ModelException(ErrorEnum.GATEWAY_EMPTY_EDGE_OUTGOING.getErrNo(), exceptionMsg);
                        }
                        if (isDefault) {
                            elseNum++;
                        }
                    }
                    if (elseNum > 1) {
                        String exceptionMsg = MessageFormat.format(Constants.MODEL_DEFINITION_ERROR_MSG_FORMAT,
                                ErrorEnum.GATEWAY_TOO_MANY_DEFAULT_EDGE.getErrMsg(), elementName, elementKey);
                        LOGGER.error(exceptionMsg);
                        throw new ModelException(ErrorEnum.GATEWAY_TOO_MANY_DEFAULT_EDGE.getErrNo(), exceptionMsg);
                    }
                    break;
                default: {
                    String exceptionMsg = MessageFormat.format(Constants.MODEL_DEFINITION_ERROR_MSG_FORMAT,
                            ErrorEnum.MODEL_UNKNOWN_ELEMENT_KEY.getErrMsg(), elementName, elementKey);
                    LOGGER.error(exceptionMsg);
                    throw new ModelException(ErrorEnum.MODEL_UNKNOWN_ELEMENT_KEY.getErrNo(), exceptionMsg);
                }
            }
        }

    }

    private static void checkOutgoing(FlowElement flowElement) throws ModelException {
        String elementName = (String) flowElement.getProperties().getOrDefault("name", StringUtils.EMPTY);
        String elementKey = flowElement.getKey();
        // 非网关节点，只可以有一条outgoing
        List<String> outgoing = flowElement.getOutgoing();
        if (null == outgoing || outgoing.size() != 1) {
            String exceptionMsg = MessageFormat.format(Constants.MODEL_DEFINITION_ERROR_MSG_FORMAT,
                    ErrorEnum.NORMAL_NODE_MUST_ONE_OUTGOING.getErrMsg(), elementName, elementKey);
            LOGGER.error(exceptionMsg);
            throw new ModelException(ErrorEnum.NORMAL_NODE_MUST_ONE_OUTGOING.getErrNo(), exceptionMsg);
        }
    }
}
