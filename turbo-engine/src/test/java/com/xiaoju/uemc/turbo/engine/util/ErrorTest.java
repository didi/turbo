package com.xiaoju.uemc.turbo.engine.util;

import com.google.common.collect.Lists;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.common.RuntimeContext;
import com.xiaoju.uemc.turbo.engine.dto.CommitTaskDTO;
import com.xiaoju.uemc.turbo.engine.dto.StartProcessDTO;
import com.xiaoju.uemc.turbo.engine.executor.ElementExecutor;
import com.xiaoju.uemc.turbo.engine.executor.ExecutorFactory;
import com.xiaoju.uemc.turbo.engine.executor.UserTaskExecutor;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.CommitTaskParam;
import com.xiaoju.uemc.turbo.engine.param.StartProcessParam;
import com.xiaoju.uemc.turbo.engine.processor.RuntimeProcessor;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 错误码覆盖单元测试类
 */
public class ErrorTest  extends BaseTest {

    @Resource
    private RuntimeProcessor runtimeProcessor;

    @Resource
    private ExecutorFactory executorFactory;

    @Resource
    private UserTaskExecutor userTaskExecutor;

    // ErrorEnum.PARAM_INVALID
    @Test
    public void error_2001() {
        StartProcessParam startProcessParam = new StartProcessParam();
        try {
            StartProcessDTO startProcessDTO = runtimeProcessor.startProcess(startProcessParam);
            LOGGER.info("result.||startProcessDTO={}", startProcessDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ErrorEnum.GET_FLOW_DEPLOYMENT_FAILED
    @Test
    public void error_4007() {
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId("notExistFlowDeployId");
        try {
            StartProcessDTO startProcessDTO = runtimeProcessor.startProcess(startProcessParam);
            LOGGER.info("result.||startProcessDTO={}", startProcessDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
    @Test
    public void error_4014() {
        FlowElement unSupportedElement = new FlowElement();
        unSupportedElement.setType(100);
        try {
            ElementExecutor elementExecutor = executorFactory.getElementExecutor(unSupportedElement);
            LOGGER.info("result.||elementExecutor={}", elementExecutor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
    @Test
    public void error_4008() {
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId("notExistFlowInstanceId");
        commitTaskParam.setTaskInstanceId("test");
        commitTaskParam.setVariables(new ArrayList<>());
        try {
            CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
            LOGGER.info("testCommit.||commitTaskDTO={}", commitTaskDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ErrorEnum.GET_NODE_INSTANCE_FAILED
    @Test
    public void error_4010() {
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId("test");
        commitTaskParam.setTaskInstanceId("notExistNodeInstanceId");
        commitTaskParam.setVariables(new ArrayList<>());
        try {
            CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
            LOGGER.info("testCommit.||commitTaskDTO={}", commitTaskDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ErrorEnum.REENTRANT_WARNING

    // ErrorEnum.GET_INSTANCE_DATA_FAILED
}
