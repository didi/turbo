package com.didiglobal.turbo.engine.executor;

import com.didiglobal.turbo.engine.common.Constants;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.common.FlowElementType;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.executor.callactivity.SyncSingleCallActivityExecutor;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.plugin.ElementPlugin;
import com.didiglobal.turbo.engine.plugin.manager.PluginManager;
import com.didiglobal.turbo.engine.util.FlowModelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExecutorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorFactory.class);

    @Resource
    private StartEventExecutor startEventExecutor;

    @Resource
    private EndEventExecutor endEventExecutor;

    @Resource
    private SequenceFlowExecutor sequenceFlowExecutor;

    @Resource
    private UserTaskExecutor userTaskExecutor;

    @Resource
    private ExclusiveGatewayExecutor exclusiveGatewayExecutor;

    @Resource
    private SyncSingleCallActivityExecutor syncSingleCallActivityExecutor;

    @Resource
    private PluginManager pluginManager;

    private final Map<Integer, ElementExecutor> executorMap = new HashMap<>(16);

    /**
     * 将原生执行器与插件扩展执行器汇总
     * 插件扩展执行器可以通过设置与原生执行器相同的elementType值进行覆盖
     */
    @PostConstruct
    public void init() {
        executorMap.put(FlowElementType.SEQUENCE_FLOW, sequenceFlowExecutor);
        executorMap.put(FlowElementType.START_EVENT, startEventExecutor);
        executorMap.put(FlowElementType.END_EVENT, endEventExecutor);
        executorMap.put(FlowElementType.USER_TASK, userTaskExecutor);
        executorMap.put(FlowElementType.EXCLUSIVE_GATEWAY, exclusiveGatewayExecutor);
        List<ElementPlugin> elementPlugins = pluginManager.getPluginsFor(ElementPlugin.class);
        elementPlugins.forEach(elementPlugin -> executorMap.put(elementPlugin.getFlowElementType(), elementPlugin.getElementExecutor()));
    }

    public ElementExecutor getElementExecutor(FlowElement flowElement) throws ProcessException {
        ElementExecutor elementExecutor = getElementExecutorInternal(flowElement);

        if (elementExecutor == null) {
            LOGGER.warn("getElementExecutor failed: unsupported elementType.|elementType={}", flowElement.getType());
            throw new ProcessException(ErrorEnum.UNSUPPORTED_ELEMENT_TYPE,
                MessageFormat.format(Constants.NODE_INFO_FORMAT, flowElement.getKey(),
                    FlowModelUtil.getElementName(flowElement), flowElement.getType()));
        }

        return elementExecutor;
    }

    private ElementExecutor getElementExecutorInternal(FlowElement flowElement) {
        int elementType = flowElement.getType();
        if (elementType == FlowElementType.CALL_ACTIVITY) {
            return getCallActivityExecutor(flowElement);
        }
        return executorMap.get(elementType);
    }

    private ElementExecutor getCallActivityExecutor(FlowElement flowElement) {
        int elementType = flowElement.getType();
        if (FlowElementType.CALL_ACTIVITY != elementType) {
            return null;
        }
        Map<String, Object> properties = flowElement.getProperties();
        String callActivityExecuteType = properties.getOrDefault(
                Constants.ELEMENT_PROPERTIES.CALL_ACTIVITY_EXECUTE_TYPE,
                Constants.CALL_ACTIVITY_EXECUTE_TYPE.SYNC).toString();

        String callActivityInstanceType = properties.getOrDefault(
                Constants.ELEMENT_PROPERTIES.CALL_ACTIVITY_INSTANCE_TYPE,
                Constants.CALL_ACTIVITY_INSTANCE_TYPE.SINGLE).toString();

        if (callActivityExecuteType.equals(Constants.CALL_ACTIVITY_EXECUTE_TYPE.SYNC)
            && callActivityInstanceType.equals(Constants.CALL_ACTIVITY_INSTANCE_TYPE.SINGLE)) {
            return syncSingleCallActivityExecutor;
        } else {
            return null;
        }
    }
}
