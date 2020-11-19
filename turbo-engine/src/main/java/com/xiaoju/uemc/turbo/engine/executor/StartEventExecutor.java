package com.xiaoju.uemc.turbo.engine.executor;

import com.xiaoju.uemc.turbo.engine.bo.NodeInstanceBO;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.common.NodeInstanceStatus;
import com.xiaoju.uemc.turbo.engine.common.RuntimeContext;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * start node executor
 *
 * Created by Stefanie on 2019/12/1.
 */

@Service
public class StartEventExecutor extends ElementExecutor {

    @Override
    protected void postExecute(RuntimeContext runtimeContext) throws Exception {
        NodeInstanceBO currentNodeInstance = runtimeContext.getCurrentNodeInstance();
        // second time set instance data id
        currentNodeInstance.setInstanceDataId(runtimeContext.getInstanceDataId());
        // change status to complete
        currentNodeInstance.setStatus(NodeInstanceStatus.COMPLETED);
        // add this node to processed nodeInstance list tail
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
