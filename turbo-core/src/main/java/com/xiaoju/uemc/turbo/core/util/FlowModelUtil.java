package com.xiaoju.uemc.turbo.core.util;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.reportlogger.LoggerFactory;
import com.didiglobal.reportlogger.ReportLogger;
import com.google.common.collect.Maps;
import com.xiaoju.uemc.turbo.core.common.Constants;
import com.xiaoju.uemc.turbo.core.common.ErrorEnum;
import com.xiaoju.uemc.turbo.core.common.FlowElementType;
import com.xiaoju.uemc.turbo.core.exception.ProcessException;
import com.xiaoju.uemc.turbo.core.model.FlowElement;
import com.xiaoju.uemc.turbo.core.model.FlowModel;
import com.xiaoju.uemc.turbo.core.model.InstanceData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by Stefanie on 2019/12/5.
 */
public class FlowModelUtil {
    protected static final ReportLogger LOGGER = LoggerFactory.getLogger(FlowModelUtil.class);

    /**
     * Parse flowModelStr to flowModel, put flowElement into map, with key=key and value=flowElement
     *
     * @param flowModelStr
     * @return flowElementMap or emptyMap
     */
    public static Map<String, FlowElement> getFlowElementMap(String flowModelStr) {
        if (StringUtils.isBlank(flowModelStr)) {
            return MapUtils.EMPTY_MAP;
        }

        FlowModel flowModel = parseModelFromString(flowModelStr);
        List<FlowElement> flowElementList = flowModel.getFlowElementList();
        if (CollectionUtils.isEmpty(flowElementList)) {
            return MapUtils.EMPTY_MAP;
        }
        Map<String, FlowElement> flowElementMap = Maps.newHashMap();
        flowElementList.forEach(flowElement -> {
            flowElementMap.put(flowElement.getKey(), flowElement);
        });
        return flowElementMap;
    }

    /**
     * Get startEvent node from flowModel
     *
     * @param flowModel
     * @return startEvent node or null
     */
    public static FlowElement getStartEvent(Map<String, FlowElement> flowModel) {
        for (FlowElement flowElement : flowModel.values()) {
            if (FlowElementType.START_EVENT == flowElement.getType()) {
                return flowElement;
            }
        }
        return null;
    }

    public static FlowElement getFlowElement(Map<String, FlowElement> flowModel, String elementKey) {
        return flowModel.get(elementKey);
    }

    /**
     * Get sequenceFlow between sourceNode and targetNode
     *
     * @param flowModel
     * @param sourceNodeKey
     * @param targetNodeKey
     * @return sequenceFlow or null
     */
    public static FlowElement getSequenceFlow(Map<String, FlowElement> flowModel, String sourceNodeKey, String targetNodeKey) {
        FlowElement sourceNode = getFlowElement(flowModel, sourceNodeKey);
        FlowElement targetNode = getFlowElement(flowModel, targetNodeKey);
        List<String> outgoingList = sourceNode.getOutgoing();
        List<String> incomingList = targetNode.getIncoming();
        for (String outgoing : outgoingList) {
            for (String incoming : incomingList) {
                if (outgoing.equals(incoming)) {
                    return getFlowElement(flowModel, outgoing);
                }
            }
        }
        return null;
    }

    /**
     * Parse flowModelStr to FlowModel without view info eg: bounds
     *
     * @param flowModelStr
     * @return flowModel
     */
    public static FlowModel parseModelFromString(String flowModelStr) {
        return JSONObject.parseObject(flowModelStr, FlowModel.class);
    }

    public static String getConditionFromSequenceFlow(FlowElement flowElement) {
        Map<String, Object> properties = flowElement.getProperties();
        return (String) properties.get(Constants.ELEMENT_PROPERTIES.CONDITION);
    }

    public static boolean isDefaultCondition(FlowElement flowElement) {
        Map<String, Object> properties = flowElement.getProperties();
        String isDefaultStr = (String) properties.get(Constants.ELEMENT_PROPERTIES.DEFAULT_CONDITION);
        return "true".equalsIgnoreCase(isDefaultStr);
    }

    public static String getHookInfos(FlowElement flowElement) {
        Map<String, Object> properties = flowElement.getProperties();
        return (String) properties.get(Constants.ELEMENT_PROPERTIES.HOOK_INFO_IDS);
    }

    public static int getElementType(String elementKey, Map<String, FlowElement> flowElementMap) {
        FlowElement flowElement = flowElementMap.get(elementKey);
        if (flowElement != null) {
            return flowElement.getType();
        }
        return -1;
    }

    public static boolean isElementType(String elementKey, Map<String, FlowElement> flowElementMap, int targetElementType) {
        int sourceElementType = getElementType(elementKey, flowElementMap);
        return sourceElementType == targetElementType;
    }

    public static String getElementName(FlowElement flowElement) {
        if (flowElement == null) {
            return StringUtils.EMPTY;
        }
        Map<String, Object> properties = flowElement.getProperties();
        if (MapUtils.isEmpty(properties)) {
            return StringUtils.EMPTY;
        }
        return StringUtils.defaultString((String) properties.get(Constants.ELEMENT_PROPERTIES.NAME), StringUtils.EMPTY);
    }

    public static FlowElement getUniqueNextNode(FlowElement currentFlowElement, Map<String, FlowElement> flowElementMap) {
        List<String> outgoingKeyList = currentFlowElement.getOutgoing();
        String nextElementKey = outgoingKeyList.get(0);
        FlowElement nextFlowElement = FlowModelUtil.getFlowElement(flowElementMap, nextElementKey);
        while (nextFlowElement.getType() == FlowElementType.SEQUENCE_FLOW) {
            nextFlowElement = getUniqueNextNode(nextFlowElement, flowElementMap);
        }
        return nextFlowElement;
    }

    public static FlowElement calculateNextNode(FlowElement currentFlowElement, Map<String, FlowElement> flowElementMap,
                                                Map<String, InstanceData> instanceDataMap) throws Exception {
        FlowElement nextFlowElement = calculateOutgoing(currentFlowElement, flowElementMap, instanceDataMap);

        while (nextFlowElement.getType() == FlowElementType.SEQUENCE_FLOW) {
            nextFlowElement = getUniqueNextNode(nextFlowElement, flowElementMap);
        }
        return nextFlowElement;
    }

    private static FlowElement calculateOutgoing(FlowElement flowElement, Map<String, FlowElement> flowElementMap,
                                                 Map<String, InstanceData> instanceDataMap) throws Exception {
        FlowElement defaultElement = null;

        List<String> outgoingList = flowElement.getOutgoing();
        for (String outgoingKey : outgoingList) {
            FlowElement outgoingSequenceFlow = FlowModelUtil.getFlowElement(flowElementMap, outgoingKey);

            //case1 condition is true, hit the outgoing
            String condition = FlowModelUtil.getConditionFromSequenceFlow(outgoingSequenceFlow);
            if (StringUtils.isNotBlank(condition) && processCondition(condition, instanceDataMap)) {
                return outgoingSequenceFlow;
            }

            if (FlowModelUtil.isDefaultCondition(outgoingSequenceFlow)) {
                defaultElement = outgoingSequenceFlow;
            }
        }
        //case2 return default while it has is configured
        if (defaultElement != null) {
            LOGGER.info("calculateOutgoing: return defaultElement.||nodeKey={}", flowElement.getKey());
            return defaultElement;
        }

        LOGGER.warn("calculateOutgoing failed.||nodeKey={}", flowElement.getKey());
        throw new ProcessException(ErrorEnum.GET_OUTGOING_FAILED);
    }

    private static boolean processCondition(String expression, Map<String, InstanceData> instanceDataMap) throws Exception {
        // TODO: 2019/12/30 format: "${expression}"——>"expression"
        if (expression.startsWith("${") && expression.endsWith("}")) {
            expression = expression.substring(2, expression.length() - 1);
        }

        boolean result = false;
        Map<String, Object> dataMap = InstanceDataUtil.parseInstanceDataMap(instanceDataMap);
        try {
            result = (Boolean) GroovyUtil.calculate(expression, dataMap);
        } catch (Throwable t) {
            LOGGER.error("processCondition exception.||message={}||expression={}||instanceDataMap={}, ",
                    t.getMessage(), expression, instanceDataMap, t);
            String groovyExFormat = "{0}: expression={1}";
            throw new ProcessException(ErrorEnum.GROOVY_CALCULATE_FAILED, MessageFormat.format(groovyExFormat, t.getMessage(), expression));
        } finally {
            LOGGER.info("processCondition.||expression={}||instanceDataMap={}||result={}",
                    expression, JSONObject.toJSONString(instanceDataMap), result);
        }
        return result;
    }
}
