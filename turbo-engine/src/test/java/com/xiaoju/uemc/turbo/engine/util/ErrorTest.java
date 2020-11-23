package com.xiaoju.uemc.turbo.engine.util;

import com.google.common.collect.Lists;
import com.xiaoju.uemc.turbo.engine.common.RuntimeContext;
import com.xiaoju.uemc.turbo.engine.dto.CommitTaskDTO;
import com.xiaoju.uemc.turbo.engine.dto.StartProcessDTO;
import com.xiaoju.uemc.turbo.engine.executor.ElementExecutor;
import com.xiaoju.uemc.turbo.engine.executor.ExecutorFactory;
import com.xiaoju.uemc.turbo.engine.executor.UserTaskExecutor;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.param.CommitTaskParam;
import com.xiaoju.uemc.turbo.engine.param.StartProcessParam;
import com.xiaoju.uemc.turbo.engine.processor.RuntimeProcessor;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * 错误码覆盖单元测试类
 */
@Deprecated
public class ErrorTest  extends BaseTest {

    @Resource
    private RuntimeProcessor runtimeProcessor;

    @Resource
    private ExecutorFactory executorFactory;

    @Resource
    private UserTaskExecutor userTaskExecutor;



    // ErrorEnum.GET_FLOW_DEPLOYMENT_FAILED


    // ErrorEnum.SAVE_FLOW_INSTANCE_FAILED
    @Test
    private void error_4016() {
        // oh shit, it is very hard.
    }

    // ErrorEnum.SAVE_INSTANCE_DATA_FAILED
    @Test
    private void error_4017() {
        // oh shit, it is very hard.
    }

    // ErrorEnum.GET_NODE_FAILED
    @Test
    private void error_4009() {
        // get node failed depend on db data.
        // example
        // 1.get startNode from flowElementMap

        // to be continued
    }

    // ErrorEnum.UNSUPPORTED_ELEMENT_TYPE


    // ErrorEnum.COMMIT_SUSPEND
    @Test
    public void error_1002() {
        RuntimeContext runtimeContext = new RuntimeContext();
        runtimeContext.setCurrentNodeModel(new FlowElement());
        runtimeContext.setNodeInstanceList(Lists.newArrayList());
        try {
            userTaskExecutor.execute(runtimeContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ErrorEnum.GET_FLOW_INSTANCE_FAILE


    // ErrorEnum.GET_NODE_INSTANCE_FAILED


    // ErrorEnum.REENTRANT_WARNING

    // ErrorEnum.GET_INSTANCE_DATA_FAILED
}
