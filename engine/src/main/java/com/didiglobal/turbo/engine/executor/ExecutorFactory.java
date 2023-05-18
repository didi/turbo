package com.didiglobal.turbo.engine.executor;

import com.didiglobal.turbo.engine.common.Constants;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.common.FlowElementType;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.executor.callactivity.SyncSingleCallActivityExecutor;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.util.FlowModelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.text.MessageFormat;
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
        switch (elementType) {
            case FlowElementType.START_EVENT:
                return startEventExecutor;
            case FlowElementType.END_EVENT:
                return endEventExecutor;
            case FlowElementType.SEQUENCE_FLOW:
                return sequenceFlowExecutor;
            case FlowElementType.USER_TASK:
                return userTaskExecutor;
            case FlowElementType.EXCLUSIVE_GATEWAY:
                return exclusiveGatewayExecutor;
            case FlowElementType.CALL_ACTIVITY:
                return getCallActivityExecutor(flowElement);
            default:
                return null;
        }
    }

    private ElementExecutor getCallActivityExecutor(FlowElement flowElement) {
        int elementType = flowElement.getType();
        if (FlowElementType.CALL_ACTIVITY != elementType) {
            return null;
        }
        Map<String, Object> properties = flowElement.getProperties();
        String callActivityExecuteType = Constants.CALL_ACTIVITY_EXECUTE_TYPE.SYNC;
        if (properties.containsKey(Constants.ELEMENT_PROPERTIES.CALL_ACTIVITY_EXECUTE_TYPE)) {
            callActivityExecuteType = properties.get(Constants.ELEMENT_PROPERTIES.CALL_ACTIVITY_EXECUTE_TYPE).toString();
        }
        String callActivityInstanceType = Constants.CALL_ACTIVITY_INSTANCE_TYPE.SINGLE;
        if (properties.containsKey(Constants.ELEMENT_PROPERTIES.CALL_ACTIVITY_INSTANCE_TYPE)) {
            callActivityInstanceType = properties.get(Constants.ELEMENT_PROPERTIES.CALL_ACTIVITY_INSTANCE_TYPE).toString();
        }

        if (callActivityExecuteType.equals(Constants.CALL_ACTIVITY_EXECUTE_TYPE.SYNC)
            && callActivityInstanceType.equals(Constants.CALL_ACTIVITY_INSTANCE_TYPE.SINGLE)) {
            return syncSingleCallActivityExecutor;
        } else {
            return null;
        }
    }
}
