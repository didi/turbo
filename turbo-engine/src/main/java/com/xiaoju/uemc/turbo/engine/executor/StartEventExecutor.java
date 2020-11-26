package com.xiaoju.uemc.turbo.engine.executor;

import com.xiaoju.uemc.turbo.engine.bo.NodeInstanceBO;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.common.NodeInstanceStatus;
import com.xiaoju.uemc.turbo.engine.common.RuntimeContext;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * StartEvent node executor is used to StartEvent node execution. It does not actually do any operation.
 * After execution, it is set to completed status and added to the end of the execution node list
 *
 * Rollback to the start node is not allowed, otherwise an exception will be thrown
 *
 * Created by Stefanie on 2019/12/1.
 */
@Service
public class StartEventExecutor extends ElementExecutor {

    /**
     * Reset the instance data ID to prevent data changes in the do phase,
     * then set the status to complete and add it to the end of the execution node list
     *
     * @param runtimeContext include flow info and runtime info
     * @throws Exception
     */
    @Override
    protected void postExecute(RuntimeContext runtimeContext) throws Exception {
        NodeInstanceBO currentNodeInstance = runtimeContext.getCurrentNodeInstance();
        currentNodeInstance.setInstanceDataId(runtimeContext.getInstanceDataId());
        currentNodeInstance.setStatus(NodeInstanceStatus.COMPLETED);
        runtimeContext.getNodeInstanceList().add(currentNodeInstance);
    }

    @Override
    protected void preRollback(RuntimeContext runtimeContext) throws Exception {
        runtimeContext.setCurrentNodeInstance(runtimeContext.getSuspendNodeInstance());
        runtimeContext.setNodeInstanceList(Collections.emptyList());

        LOGGER.warn("postRollback: reset runtimeContext.||flowInstanceId={}||nodeKey={}||nodeType={}",
                runtimeContext.getFlowInstanceId(), runtimeContext.getCurrentNodeModel().getKey(), runtimeContext.getCurrentNodeModel().getType());
        throw new ProcessException(ErrorEnum.NO_USER_TASK_TO_ROLLBACK, "It's a startEvent.");
    }

    @Override
    protected void postRollback(RuntimeContext runtimeContext) throws Exception {
        //do nothing
    }
}
