package com.xiaoju.uemc.turbo.engine.error;

import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.dto.CommitTaskDTO;
import com.xiaoju.uemc.turbo.engine.dto.RecallTaskDTO;
import com.xiaoju.uemc.turbo.engine.dto.StartProcessDTO;
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

    private StartProcessDTO startProcess() throws Exception {
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId("zk_deploy_id_1");
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("orderId", "string", "123"));
        startProcessParam.setVariables(variables);
        return runtimeProcessor.startProcess(startProcessParam);
    }

    // ErrorEnum.SUCCESS
    @Test
    public void error_1000() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);
        // user task -> end node
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.SUCCESS.getErrNo());
    }

    // ErrorEnum.REENTRANT_WARNING
    @Test
    public void error_1001() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);
        // user task -> end node
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        // commit end node
        commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.REENTRANT_WARNING.getErrNo());
    }

    // ErrorEnum.COMMIT_SUSPEND
    @Test
    public void error_1002() throws Exception {
        // start node -> user task
        StartProcessDTO startProcessDTO = startProcess();
        Assert.assertTrue(startProcessDTO.getErrCode() == ErrorEnum.COMMIT_SUSPEND.getErrNo());
    }

    // ErrorEnum.ROLLBACK_SUSPEND
    @Test
    public void error_1003() throws Exception {
        // start process
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);
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
    }
}
