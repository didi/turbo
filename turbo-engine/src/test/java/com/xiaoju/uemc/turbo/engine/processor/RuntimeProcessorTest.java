package com.xiaoju.uemc.turbo.engine.processor;

import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.dao.mapper.FlowDeploymentMapper;
import com.xiaoju.uemc.turbo.engine.dto.*;
import com.xiaoju.uemc.turbo.engine.entity.FlowDeploymentPO;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.CommitTaskParam;
import com.xiaoju.uemc.turbo.engine.param.RecallTaskParam;
import com.xiaoju.uemc.turbo.engine.param.StartProcessParam;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.apache.commons.lang3.StringUtils;
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
    private RuntimeProcessor runtimeProcessor;

    @Resource
    private FlowDeploymentMapper flowDeploymentMapper;

    private StartProcessDTO startProcess() throws Exception {
        // prepare
        FlowDeploymentPO flowDeploymentPO = EntityBuilder.buildSpecialFlowDeploymentPO();
        FlowDeploymentPO _flowDeploymentPO = flowDeploymentMapper.selectByDeployId(flowDeploymentPO.getFlowDeployId());
        if (_flowDeploymentPO != null) {
            if (!StringUtils.equals(_flowDeploymentPO.getFlowModel(), flowDeploymentPO.getFlowModel())) {
                flowDeploymentMapper.deleteById(_flowDeploymentPO.getId());
                flowDeploymentMapper.insert(flowDeploymentPO);
            }
        } else {
            flowDeploymentMapper.insert(flowDeploymentPO);
        }

        // start process
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId(flowDeploymentPO.getFlowDeployId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("orderId", "string", "123"));
        variables.add(new InstanceData("orderStatus", "string", "1"));
        startProcessParam.setVariables(variables);
        // build
        return runtimeProcessor.startProcess(startProcessParam);
    }

    @Test
    public void testStartProcess() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();
        Assert.assertTrue(startProcessDTO.getErrCode() == ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(startProcessDTO.getActiveTaskInstance().getModelKey(), "BranchUserTask_0scrl8d"));
    }

    // UserTask -> EndEvent
    @Test
    public void testNormalCommitToEnd() throws Exception {
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
        Assert.assertTrue(StringUtils.equals(commitTaskDTO.getActiveTaskInstance().getModelKey(), "EndEvent_0s4vsxw"));
    }

    // UserTask -> ExclusiveGateway -> UserTask
    @Test
    public void testNormalCommitToUserTask() throws Exception {
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
        Assert.assertTrue(StringUtils.equals(commitTaskDTO.getActiveTaskInstance().getModelKey(), "UserTask_0uld0u9"));
    }

    // UserTask -> ExclusiveGateway -> UserTask
    // UserTask ->
    @Test
    public void testRepeatedCommitToUserTask() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();

        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);

        commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        LOGGER.info("testCommit.||commitTaskDTO={}", commitTaskDTO);

        Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskDTO.getActiveTaskInstance().getModelKey(), "UserTask_0uld0u9"));
    }

    // UserTask -> EndEvent -> Commit again
    @Test
    public void testCommitCompletedFlowInstance() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();

        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);

        commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        LOGGER.info("testCommit.||commitTaskDTO={}", commitTaskDTO);

        Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.REENTRANT_WARNING.getErrNo());
    }

    @Test
    public void testCommitTerminatedFlowInstance() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();

        runtimeProcessor.terminateProcess(startProcessDTO.getFlowInstanceId());

        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);

        Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.TERMINATE_CANNOT_COMMIT.getErrNo());
    }

    // UserTask <- ExclusiveGateway <- UserTask : Commit old UserTask
    @Test
    public void testRollbackToUserTaskAndCommitOldUserTask() throws Exception {
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

        // commit old UserTask
        commitTaskDTO = runtimeProcessor.commit(commitTaskParam);

        LOGGER.info("testRollbackToUserTaskAndCommitOldUserTask.||commitTaskDTO={}", commitTaskDTO);
        Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.COMMIT_FAILED.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskDTO.getActiveTaskInstance().getModelKey(), "BranchUserTask_0scrl8d"));
    }

    @Test
    public void testRollbackFromMiddleUserTask() throws Exception {
        // start process
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        String branchUserTaskNodeInstanceId = startProcessDTO.getActiveTaskInstance().getNodeInstanceId();
        commitTaskParam.setTaskInstanceId(branchUserTaskNodeInstanceId);
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);

        // StartEvent <- UserTask
        RecallTaskParam recallTaskParam = new RecallTaskParam();
        recallTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        // Previous UserTask node
        recallTaskParam.setTaskInstanceId(branchUserTaskNodeInstanceId);
        RecallTaskDTO recallTaskDTO = runtimeProcessor.recall(recallTaskParam);

        // Ignore current userTask
        LOGGER.info("testRollbackFromMiddleUserTask.||recallTaskDTO={}", recallTaskDTO);
        Assert.assertTrue(recallTaskDTO.getErrCode() == ErrorEnum.ROLLBACK_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(recallTaskDTO.getActiveTaskInstance().getModelKey(), "BranchUserTask_0scrl8d"));
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

        // UserTask <- ExclusiveGateway <- UserTask
        RecallTaskParam recallTaskParam = new RecallTaskParam();
        recallTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        recallTaskParam.setTaskInstanceId(commitTaskDTO.getActiveTaskInstance().getNodeInstanceId());
        RecallTaskDTO recallTaskDTO = runtimeProcessor.recall(recallTaskParam);

        LOGGER.info("testRollback.||recallTaskDTO={}", recallTaskDTO);
        Assert.assertTrue(recallTaskDTO.getErrCode() == ErrorEnum.ROLLBACK_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(recallTaskDTO.getActiveTaskInstance().getModelKey(), "BranchUserTask_0scrl8d"));
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

        Assert.assertTrue(nodeInstanceListDTO.getNodeInstanceDTOList().size() == 2);
        Assert.assertTrue(StringUtils.equals(nodeInstanceListDTO.getNodeInstanceDTOList().get(0).getModelKey(), "UserTask_0uld0u9"));
    }


    @Test
    public void testGetFailedHistoryElementList() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        variables.add(new InstanceData("orderId", "string", "notExistOrderId"));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway : Failed
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        LOGGER.info("testGetFailedHistoryElementList.||commitTaskDTO={}", commitTaskDTO);
        Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.GET_OUTGOING_FAILED.getErrNo());

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

        Assert.assertTrue(elementInstanceListDTO.getElementInstanceDTOList().size() == 5);
        Assert.assertTrue(StringUtils.equals(elementInstanceListDTO.getElementInstanceDTOList().get(4).getModelKey(), "ExclusiveGateway_0yq2l0s"));
    }

    @Test
    public void testGetCompletedHistoryElementList() throws Exception {
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

        Assert.assertTrue(elementInstanceListDTO.getElementInstanceDTOList().size() == 5);
        Assert.assertTrue(StringUtils.equals(elementInstanceListDTO.getElementInstanceDTOList().get(4).getModelKey(), "EndEvent_0s4vsxw"));
    }

    // commit a,  b  回滚之后是不是之前的数据 - error
    @Test
    public void testGetInstanceData() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();
        String flowInstanceId = startProcessDTO.getFlowInstanceId();
        List<InstanceData> instanceDataList = runtimeProcessor.getInstanceData(flowInstanceId);
        LOGGER.info("testGetInstanceData 1.||instanceDataList={}", instanceDataList);

        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        variables.add(new InstanceData("commitTime", "int", 1));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);

        // UserTask -> UserTask
        CommitTaskParam commitTaskParam1 = new CommitTaskParam();
        commitTaskParam1.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam1.setTaskInstanceId(commitTaskDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables1 = new ArrayList<>();
        variables1.add(new InstanceData("orderStatus", "string", "2"));
        variables1.add(new InstanceData("commitTime", "int", 2));
        commitTaskParam1.setVariables(variables1);
        CommitTaskDTO commitTaskDTO1 = runtimeProcessor.commit(commitTaskParam1);

        instanceDataList = runtimeProcessor.getInstanceData(flowInstanceId);
        LOGGER.info("testGetInstanceData 2.||instanceDataList={}", instanceDataList);

        // UserTask <- UserTask
        RecallTaskParam recallTaskParam = new RecallTaskParam();
        recallTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        recallTaskParam.setTaskInstanceId(commitTaskDTO1.getActiveTaskInstance().getNodeInstanceId());
        RecallTaskDTO recallTaskDTO = runtimeProcessor.recall(recallTaskParam);
        LOGGER.info("recallTaskDTO 3.||recallTaskDTO={}", recallTaskDTO);

        // UserTask <- ExclusiveGateway <- UserTask
        RecallTaskParam recallTaskParam1 = new RecallTaskParam();
        recallTaskParam1.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        recallTaskParam1.setTaskInstanceId(recallTaskDTO.getActiveTaskInstance().getNodeInstanceId());
        RecallTaskDTO recallTaskDTO1 = runtimeProcessor.recall(recallTaskParam1);
        LOGGER.info("recallTaskDTO 4.||recallTaskDTO={}", recallTaskDTO1);

        instanceDataList = runtimeProcessor.getInstanceData(flowInstanceId);
        LOGGER.info("testGetInstanceData 5.||instanceDataList={}", instanceDataList);
    }

    @Test
    public void testGetNodeInstance() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();
        String flowInstanceId = startProcessDTO.getFlowInstanceId();
        NodeInstanceDTO nodeInstanceDTO = runtimeProcessor.getNodeInstance(flowInstanceId, startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        LOGGER.info("testGetNodeInstance.||nodeInstanceDTO={}", nodeInstanceDTO);

        Assert.assertTrue(StringUtils.equals(nodeInstanceDTO.getNodeInstanceId(), startProcessDTO.getActiveTaskInstance().getNodeInstanceId()));
    }
}
