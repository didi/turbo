package com.xiaoju.uemc.turbo.engine.executor;

import com.xiaoju.uemc.turbo.engine.bo.NodeInstanceBO;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.common.NodeInstanceStatus;
import com.xiaoju.uemc.turbo.engine.common.RuntimeContext;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Created by Stefanie on 2019/12/1.
 */

@Service
public class StartEventExecutor extends ElementExecutor {

    @Override
    protected void postExecute(RuntimeContext runtimeContext) throws ProcessException {
        NodeInstanceBO currentNodeInstance = runtimeContext.getCurrentNodeInstance();
        currentNodeInstance.setInstanceDataId(runtimeContext.getInstanceDataId());
        currentNodeInstance.setStatus(NodeInstanceStatus.COMPLETED);
        runtimeContext.getNodeInstanceList().add(currentNodeInstance);
    }

    @Override
    protected void preRollback(RuntimeContext runtimeContext) throws ProcessException {
        runtimeContext.setCurrentNodeInstance(runtimeContext.getSuspendNodeInstance());
        runtimeContext.setNodeInstanceList(Collections.emptyList());

        LOGGER.warn("postRollback: reset runtimeContext.||flowInstanceId={}||nodeKey={}||nodeType={}",
                runtimeContext.getFlowInstanceId(), runtimeContext.getCurrentNodeModel().getKey(), runtimeContext.getCurrentNodeModel().getType());
        throw new ProcessException(ErrorEnum.NO_USER_TASK_TO_ROLLBACK, "It's a startEvent.");
    }

    @Override
    protected void postRollback(RuntimeContext runtimeContext) throws ProcessException {
        //do nothing
    }
}
