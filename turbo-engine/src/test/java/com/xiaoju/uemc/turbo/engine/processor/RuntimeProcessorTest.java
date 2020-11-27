package com.xiaoju.uemc.turbo.engine.processor;

import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.dto.*;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.CommitTaskParam;
import com.xiaoju.uemc.turbo.engine.param.RecallTaskParam;
import com.xiaoju.uemc.turbo.engine.param.StartProcessParam;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefanie on 2019/12/13.
 */
public class RuntimeProcessorTest extends BaseTest {

    @Resource
    RuntimeProcessor runtimeProcessor;

    private StartProcessDTO startProcess() throws Exception {
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId("zk_deploy_id_1");
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("orderId", "string", "123"));
        startProcessParam.setVariables(variables);
        return runtimeProcessor.startProcess(startProcessParam);
    }

    @Test
    public void testStartProcess() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();
        Assert.assertTrue(startProcessDTO.getErrCode() == ErrorEnum.COMMIT_SUSPEND.getErrNo());
    }

    // UserTask -> EndEvent
    @Test
    public void testCommitToEnd() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        LOGGER.info("testCommit.||commitTaskDTO={}", commitTaskDTO);
        Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.SUCCESS.getErrNo());
    }

    // UserTask -> ExclusiveGateway -> UserTask
    @Test
    public void testCommitToUserTask() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        LOGGER.info("testCommit.||commitTaskDTO={}", commitTaskDTO);
        Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.COMMIT_SUSPEND.getErrNo());
    }

    // UserTask <- ExclusiveGateway <- UserTask
    @Test
    public void testRollbackToUserTask() throws Exception {
        // start process
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);
        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        // rollback
        RecallTaskParam recallTaskParam = new RecallTaskParam();
        recallTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        recallTaskParam.setTaskInstanceId(commitTaskDTO.getActiveTaskInstance().getNodeInstanceId());
        RecallTaskDTO recallTaskDTO = runtimeProcessor.recall(recallTaskParam);
        LOGGER.info("testRollback.||recallTaskDTO={}", recallTaskDTO);
        Assert.assertTrue(recallTaskDTO.getErrCode() == ErrorEnum.ROLLBACK_SUSPEND.getErrNo());
    }

    // StartEvent <- UserTask
    @Test
    public void testRollbackToStartEvent() throws Exception {
        // start process
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        // UserTask <- ExclusiveGateway <- UserTask
        RecallTaskParam recallTaskParam = new RecallTaskParam();
        recallTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        recallTaskParam.setTaskInstanceId(commitTaskDTO.getActiveTaskInstance().getNodeInstanceId());
        RecallTaskDTO recallTaskDTO = runtimeProcessor.recall(recallTaskParam);
        // StartEvent <- UserTask
        recallTaskParam = new RecallTaskParam();
        recallTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        recallTaskParam.setTaskInstanceId(recallTaskDTO.getActiveTaskInstance().getNodeInstanceId());
        recallTaskDTO = runtimeProcessor.recall(recallTaskParam);
        LOGGER.info("testRollback.||recallTaskDTO={}", recallTaskDTO);
        Assert.assertTrue(recallTaskDTO.getErrCode() == ErrorEnum.NO_USER_TASK_TO_ROLLBACK.getErrNo());
    }

    // rollback completed process
    @Test
    public void testRollbackFromEndEvent() throws Exception {
        // start process
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);
        // UserTask -> EndEvent
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        // rollback EndEvent
        RecallTaskParam recallTaskParam = new RecallTaskParam();
        recallTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        recallTaskParam.setTaskInstanceId(commitTaskDTO.getActiveTaskInstance().getNodeInstanceId());
        RecallTaskDTO recallTaskDTO = runtimeProcessor.recall(recallTaskParam);
        LOGGER.info("testRollback.||recallTaskDTO={}", recallTaskDTO);
        Assert.assertTrue(recallTaskDTO.getErrCode() == ErrorEnum.FLOW_INSTANCE_CANNOT_ROLLBACK.getErrNo());
    }

    @Test
    public void testTerminateProcess() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();
        TerminateDTO terminateDTO = runtimeProcessor.terminateProcess(startProcessDTO.getFlowInstanceId());
        LOGGER.info("testTerminateProcess.||terminateDTO={}", terminateDTO);
        Assert.assertTrue(terminateDTO.getErrCode() == ErrorEnum.SUCCESS.getErrNo());
    }

    @Test
    public void testGetHistoryUserTaskList() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        NodeInstanceListDTO nodeInstanceListDTO = runtimeProcessor.getHistoryUserTaskList(commitTaskDTO.getFlowInstanceId());
        LOGGER.info("testGetHistoryUserTaskList.||nodeInstanceListDTO={}", nodeInstanceListDTO);
        StringBuilder sb = new StringBuilder();
        for (ElementInstanceDTO elementInstanceDTO : nodeInstanceListDTO.getNodeInstanceDTOList()) {
            sb.append("[");
            sb.append(elementInstanceDTO.getModelKey());
            sb.append(" ");
            sb.append(elementInstanceDTO.getStatus());
            sb.append("]->");
        }
        LOGGER.info("testGetHistoryUserTaskList.||snapshot={}", sb.toString());
    }

    @Test
    public void testGetHistoryElementList() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);

        // UserTask -> EndEvent
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        ElementInstanceListDTO elementInstanceListDTO = runtimeProcessor.getHistoryElementList(commitTaskDTO.getFlowInstanceId());
        LOGGER.info("testGetHistoryElementList.||elementInstanceListDTO={}", elementInstanceListDTO);
        StringBuilder sb = new StringBuilder();
        for (ElementInstanceDTO elementInstanceDTO : elementInstanceListDTO.getElementInstanceDTOList()) {
            sb.append("[");
            sb.append(elementInstanceDTO.getModelKey());
            sb.append(" ");
            sb.append(elementInstanceDTO.getStatus());
            sb.append("]->");
        }
        LOGGER.info("testGetHistoryElementList.||snapshot={}", sb.toString());
    }

    @Test
    public void testGetInstanceData() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();
        String flowInstanceId = startProcessDTO.getFlowInstanceId();
        List<InstanceData> instanceDataList = runtimeProcessor.getInstanceData(flowInstanceId);
        LOGGER.info("testGetInstanceData.||instanceDataList={}", instanceDataList);
    }

    @Test
    public void testGetNodeInstance() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();
        String flowInstanceId = startProcessDTO.getFlowInstanceId();
        NodeInstanceDTO nodeInstanceDTO = runtimeProcessor.getNodeInstance(flowInstanceId, startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        LOGGER.info("testGetNodeInstance.||nodeInstanceDTO={}", nodeInstanceDTO);
        Assert.assertTrue(nodeInstanceDTO.getNodeInstanceId().equals(startProcessDTO.getActiveTaskInstance().getNodeInstanceId()));
    }
}
