package com.xiaoju.uemc.turbo.engine.error;

import com.google.common.collect.Lists;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.common.RuntimeContext;
import com.xiaoju.uemc.turbo.engine.dto.CommitTaskDTO;
import com.xiaoju.uemc.turbo.engine.dto.RecallTaskDTO;
import com.xiaoju.uemc.turbo.engine.dto.StartProcessDTO;
import com.xiaoju.uemc.turbo.engine.exception.SuspendException;
import com.xiaoju.uemc.turbo.engine.executor.UserTaskExecutor;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.CommitTaskParam;
import com.xiaoju.uemc.turbo.engine.param.RecallTaskParam;
import com.xiaoju.uemc.turbo.engine.param.StartProcessParam;
import com.xiaoju.uemc.turbo.engine.processor.RuntimeProcessor;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 错误码覆盖单元测试类之1000~1999 非阻断性错误码
 */
public class Error1Test extends BaseTest {

    @Resource
    private RuntimeProcessor runtimeProcessor;

    @Resource
    private UserTaskExecutor userTaskExecutor;

    private StartProcessDTO startProcess() {
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId("zk_deploy_id_1");
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("orderId", "string", "123"));
        startProcessParam.setVariables(variables);
        try {
            StartProcessDTO startProcessDTO = runtimeProcessor.startProcess(startProcessParam);
            return startProcessDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ErrorEnum.SUCCESS
    @Test
    public void error_1000() {
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);
        try {
            // user task -> end node
            CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
            Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.SUCCESS.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ErrorEnum.REENTRANT_WARNING
    @Test
    public void error_1001() {
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);
        try {
            // user task -> end node
            CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
            commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
            Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.REENTRANT_WARNING.getErrNo());
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
        } catch (SuspendException e) {
            Assert.assertTrue(e.getErrNo() == ErrorEnum.COMMIT_SUSPEND.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ErrorEnum.ROLLBACK_SUSPEND
    @Test
    public void error_1003() {
        // start process
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);
        try {
            // user task -> exclusive gateway node -> user task
            // commit
            CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);

            // rollback
            // user task <- exclusive gateway node <- user task
            RecallTaskParam recallTaskParam = new RecallTaskParam();
            recallTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
            recallTaskParam.setTaskInstanceId(commitTaskDTO.getActiveTaskInstance().getNodeInstanceId());
            RecallTaskDTO recallTaskDTO = runtimeProcessor.recall(recallTaskParam);

            LOGGER.info("testRollback.||recallTaskDTO={}", recallTaskDTO);
            Assert.assertTrue(recallTaskDTO.getErrCode() == ErrorEnum.ROLLBACK_SUSPEND.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
