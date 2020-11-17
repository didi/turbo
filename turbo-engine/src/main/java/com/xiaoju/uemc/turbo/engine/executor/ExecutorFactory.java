package com.xiaoju.uemc.turbo.engine.executor;

import com.didiglobal.reportlogger.LoggerFactory;
import com.didiglobal.reportlogger.ReportLogger;
import com.xiaoju.uemc.turbo.engine.common.Constants;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.common.FlowElementType;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.util.FlowModelUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;

/**
 * Created by Stefanie on 2019/12/1.
 */
@Service
public class ExecutorFactory {

    private static final ReportLogger LOGGER = LoggerFactory.getLogger(ExecutorFactory.class);

    @Resource
    private StartEventExecutor startEventExecutor;

    @Resource
    private EndEventExecutor endEventExecutor;

    @Resource
    private SequenceFlowExecutor sequenceFlowExecutor;

    @Resource
    private UserTaskExecutor userTaskExecutor;

    @Resource
    private ServiceTaskExecutor serviceTaskExecutor;

    @Resource
    private ExclusiveGatewayExecutor exclusiveGatewayExecutor;


    public ElementExecutor getElementExecutor(FlowElement flowElement) throws Exception {
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
            case FlowElementType.START_EVENT: {
                return startEventExecutor;
            }
            case FlowElementType.END_EVENT: {
                return endEventExecutor;
            }
            case FlowElementType.SEQUENCE_FLOW: {
                return sequenceFlowExecutor;
            }
            case FlowElementType.USER_TASK: {
                return userTaskExecutor;
            }
            case FlowElementType.EXCLUSIVE_GATEWAY: {
                return exclusiveGatewayExecutor;
            }
            default: {
                return null;
            }
        }
    }
}
