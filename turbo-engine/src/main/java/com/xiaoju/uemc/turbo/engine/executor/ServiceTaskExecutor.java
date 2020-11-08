package com.xiaoju.uemc.turbo.engine.executor;

import com.xiaoju.uemc.turbo.engine.common.RuntimeContext;
import org.springframework.stereotype.Service;

/**
 * Created by Stefanie on 2019/12/1.
 */

@Service
public class ServiceTaskExecutor extends ElementExecutor {

    @Override
    protected void doRollback(RuntimeContext runtimeContext) {
        //not implement
        LOGGER.warn("doRollback: not implement as a ServiceTask.||flowInstanceId={}||flowDeployId={}||modelKey={}",
                runtimeContext.getFlowInstanceId(), runtimeContext.getFlowDeployId(), runtimeContext.getCurrentNodeModel().getKey());
    }
}
