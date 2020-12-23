package com.didiglobal.turbo.engine.executor;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.didiglobal.turbo.engine.common.Constants;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.common.FlowElementType;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.util.FlowModelUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;

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


    public ElementExecutor getElementExecutor(FlowElement flowElement) throws ProcessException {
        int elementType = flowElement.getType();
        ElementExecutor elementExecutor = getElementExecutor(elementType);

        if (elementExecutor == null) {
            LOGGER.warn("getElementExecutor failed: unsupported elementType.|elementType={}", elementType);
            throw new ProcessException(ErrorEnum.UNSUPPORTED_ELEMENT_TYPE,
                    MessageFormat.format(Constants.NODE_INFO_FORMAT, flowElement.getKey(),
                            FlowModelUtil.getElementName(flowElement), elementType));
        }

        return elementExecutor;
    }

    private ElementExecutor getElementExecutor(int elementType) {
        switch (elementType) {
            case FlowElementType.START_EVENT: return startEventExecutor;
            case FlowElementType.END_EVENT: return endEventExecutor;
            case FlowElementType.SEQUENCE_FLOW: return sequenceFlowExecutor;
            case FlowElementType.USER_TASK: return userTaskExecutor;
            case FlowElementType.EXCLUSIVE_GATEWAY: return exclusiveGatewayExecutor;
            default: return null;
        }
    }
}
