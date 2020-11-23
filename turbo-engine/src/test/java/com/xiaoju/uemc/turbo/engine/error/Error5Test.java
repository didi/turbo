package com.xiaoju.uemc.turbo.engine.error;

import com.xiaoju.uemc.turbo.engine.executor.ExecutorFactory;
import com.xiaoju.uemc.turbo.engine.executor.UserTaskExecutor;
import com.xiaoju.uemc.turbo.engine.processor.RuntimeProcessor;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;

import javax.annotation.Resource;

/**
 * 错误码覆盖单元测试类之5000~5999 系统错误
 */
public class Error5Test extends BaseTest {

    @Resource
    private RuntimeProcessor runtimeProcessor;

    @Resource
    private ExecutorFactory executorFactory;

    @Resource
    private UserTaskExecutor userTaskExecutor;


}
