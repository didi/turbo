package com.xiaoju.uemc.turbo.engine.processor;

import com.xiaoju.uemc.turbo.engine.bo.ElementInstance;
import com.xiaoju.uemc.turbo.engine.bo.NodeInstance;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.dao.mapper.FlowDeploymentMapper;
import com.xiaoju.uemc.turbo.engine.result.*;
import com.xiaoju.uemc.turbo.engine.entity.FlowDeploymentPO;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.CommitTaskParam;
import com.xiaoju.uemc.turbo.engine.param.RollbackTaskParam;
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

    private StartProcessResult startProcess() throws Exception {
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
        StartProcessResult startProcessResult = startProcess();
        Assert.assertTrue(startProcessResult.getErrCode() == ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(startProcessResult.getActiveTaskInstance().getModelKey(), "BranchUserTask_0scrl8d"));
    }

    // UserTask -> EndEvent
    @Test
    public void testNormalCommitToEnd() throws Exception {
        StartProcessResult startProcessResult = startProcess();

        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);

        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        LOGGER.info("testCommit.||commitTaskResult={}", commitTaskResult);
        Assert.assertTrue(commitTaskResult.getErrCode() == ErrorEnum.SUCCESS.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult.getActiveTaskInstance().getModelKey(), "EndEvent_0s4vsxw"));
    }

    // UserTask -> ExclusiveGateway -> UserTask
    @Test
    public void testNormalCommitToUserTask() throws Exception {
        StartProcessResult startProcessResult = startProcess();

        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);

        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        LOGGER.info("testCommit.||commitTaskResult={}", commitTaskResult);
        Assert.assertTrue(commitTaskResult.getErrCode() == ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult.getActiveTaskInstance().getModelKey(), "UserTask_0uld0u9"));
    }

    // UserTask -> ExclusiveGateway -> UserTask
    // UserTask ->
    @Test
    public void testRepeatedCommitToUserTask() throws Exception {
        StartProcessResult startProcessResult = startProcess();

        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        LOGGER.info("testCommit.||commitTaskResult={}", commitTaskResult);

        Assert.assertTrue(commitTaskResult.getErrCode() == ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult.getActiveTaskInstance().getModelKey(), "UserTask_0uld0u9"));
    }

    // UserTask -> EndEvent -> Commit again
    @Test
    public void testCommitCompletedFlowInstance() throws Exception {
        StartProcessResult startProcessResult = startProcess();

        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        LOGGER.info("testCommit.||commitTaskResult={}", commitTaskResult);

        Assert.assertTrue(commitTaskResult.getErrCode() == ErrorEnum.REENTRANT_WARNING.getErrNo());
    }

    @Test
    public void testCommitTerminatedFlowInstance() throws Exception {
        StartProcessResult startProcessResult = startProcess();

        runtimeProcessor.terminateProcess(startProcessResult.getFlowInstanceId());

        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        Assert.assertTrue(commitTaskResult.getErrCode() == ErrorEnum.COMMIT_REJECTRD.getErrNo());
    }

    // UserTask <- ExclusiveGateway <- UserTask : Commit old UserTask
    @Test
    public void testRollbackToUserTaskAndCommitOldUserTask() throws Exception {
        // start process
        StartProcessResult startProcessResult = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        // UserTask <- ExclusiveGateway <- UserTask
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        RollbackTaskResult recallTaskResult = runtimeProcessor.rollback(rollbackTaskParam);

        // commit old UserTask
        commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        LOGGER.info("testRollbackToUserTaskAndCommitOldUserTask.||commitTaskResult={}", commitTaskResult);
        Assert.assertTrue(commitTaskResult.getErrCode() == ErrorEnum.COMMIT_FAILED.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult.getActiveTaskInstance().getModelKey(), "BranchUserTask_0scrl8d"));
    }

    @Test
    public void testRollbackFromMiddleUserTask() throws Exception {
        // start process
        StartProcessResult startProcessResult = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        String branchUserTaskNodeInstanceId = startProcessResult.getActiveTaskInstance().getNodeInstanceId();
        commitTaskParam.setTaskInstanceId(branchUserTaskNodeInstanceId);
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        // StartEvent <- UserTask
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        // Previous UserTask node
        rollbackTaskParam.setTaskInstanceId(branchUserTaskNodeInstanceId);
        RollbackTaskResult recallTaskResult = runtimeProcessor.rollback(rollbackTaskParam);

        // Ignore current userTask
        LOGGER.info("testRollbackFromMiddleUserTask.||recallTaskResult={}", recallTaskResult);
        Assert.assertTrue(recallTaskResult.getErrCode() == ErrorEnum.ROLLBACK_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(recallTaskResult.getActiveTaskInstance().getModelKey(), "BranchUserTask_0scrl8d"));
    }



    // UserTask <- ExclusiveGateway <- UserTask
    @Test
    public void testRollbackToUserTask() throws Exception {
        // start process
        StartProcessResult startProcessResult = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        // UserTask <- ExclusiveGateway <- UserTask
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        RollbackTaskResult recallTaskResult = runtimeProcessor.rollback(rollbackTaskParam);

        LOGGER.info("testRollback.||recallTaskResult={}", recallTaskResult);
        Assert.assertTrue(recallTaskResult.getErrCode() == ErrorEnum.ROLLBACK_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(recallTaskResult.getActiveTaskInstance().getModelKey(), "BranchUserTask_0scrl8d"));
    }

    // StartEvent <- UserTask
    @Test
    public void testRollbackToStartEvent() throws Exception {
        // start process
        StartProcessResult startProcessResult = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        // UserTask <- ExclusiveGateway <- UserTask
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        RollbackTaskResult recallTaskResult = runtimeProcessor.rollback(rollbackTaskParam);

        // StartEvent <- UserTask
        rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(recallTaskResult.getActiveTaskInstance().getNodeInstanceId());
        recallTaskResult = runtimeProcessor.rollback(rollbackTaskParam);
        LOGGER.info("testRollback.||recallTaskResult={}", recallTaskResult);
        Assert.assertTrue(recallTaskResult.getErrCode() == ErrorEnum.NO_USER_TASK_TO_ROLLBACK.getErrNo());
    }

    // rollback completed process
    @Test
    public void testRollbackFromEndEvent() throws Exception {
        // start process
        StartProcessResult startProcessResult = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);

        // UserTask -> EndEvent
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        // rollback EndEvent
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        RollbackTaskResult recallTaskResult = runtimeProcessor.rollback(rollbackTaskParam);

        LOGGER.info("testRollback.||recallTaskResult={}", recallTaskResult);
        Assert.assertTrue(recallTaskResult.getErrCode() == ErrorEnum.ROLLBACK_REJECTRD.getErrNo());
    }

    @Test
    public void testTerminateProcess() throws Exception {
        StartProcessResult startProcessResult = startProcess();
        TerminateResult terminateResult = runtimeProcessor.terminateProcess(startProcessResult.getFlowInstanceId());
        LOGGER.info("testTerminateProcess.||terminateResult={}", terminateResult);
        Assert.assertTrue(terminateResult.getErrCode() == ErrorEnum.SUCCESS.getErrNo());
    }

    @Test
    public void testGetHistoryUserTaskList() throws Exception {
        StartProcessResult startProcessResult = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        NodeInstanceListResult nodeInstanceListResult = runtimeProcessor.getHistoryUserTaskList(commitTaskResult.getFlowInstanceId());
        LOGGER.info("testGetHistoryUserTaskList.||nodeInstanceListResult={}", nodeInstanceListResult);
        StringBuilder sb = new StringBuilder();
        for (NodeInstance elementInstanceResult : nodeInstanceListResult.getNodeInstanceList()) {
            sb.append("[");
            sb.append(elementInstanceResult.getModelKey());
            sb.append(" ");
            sb.append(elementInstanceResult.getStatus());
            sb.append("]->");
        }
        LOGGER.info("testGetHistoryUserTaskList.||snapshot={}", sb.toString());

        Assert.assertTrue(nodeInstanceListResult.getNodeInstanceList().size() == 2);
        Assert.assertTrue(StringUtils.equals(nodeInstanceListResult.getNodeInstanceList().get(0).getModelKey(), "UserTask_0uld0u9"));
    }


    @Test
    public void testGetFailedHistoryElementList() throws Exception {
        StartProcessResult startProcessResult = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        variables.add(new InstanceData("orderId", "string", "notExistOrderId"));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway : Failed
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        LOGGER.info("testGetFailedHistoryElementList.||commitTaskResult={}", commitTaskResult);
        Assert.assertTrue(commitTaskResult.getErrCode() == ErrorEnum.GET_OUTGOING_FAILED.getErrNo());

        ElementInstanceListResult elementInstanceListResult = runtimeProcessor.getHistoryElementList(commitTaskResult.getFlowInstanceId());
        LOGGER.info("testGetHistoryElementList.||elementInstanceListResult={}", elementInstanceListResult);
        StringBuilder sb = new StringBuilder();
        for (ElementInstance elementInstanceResult : elementInstanceListResult.getElementInstanceList()) {
            sb.append("[");
            sb.append(elementInstanceResult.getModelKey());
            sb.append(" ");
            sb.append(elementInstanceResult.getStatus());
            sb.append("]->");
        }
        LOGGER.info("testGetHistoryElementList.||snapshot={}", sb.toString());

        Assert.assertTrue(elementInstanceListResult.getElementInstanceList().size() == 5);
        Assert.assertTrue(StringUtils.equals(elementInstanceListResult.getElementInstanceList().get(4).getModelKey(), "ExclusiveGateway_0yq2l0s"));
    }

    @Test
    public void testGetCompletedHistoryElementList() throws Exception {
        StartProcessResult startProcessResult = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);

        // UserTask -> EndEvent
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        ElementInstanceListResult elementInstanceListResult = runtimeProcessor.getHistoryElementList(commitTaskResult.getFlowInstanceId());
        LOGGER.info("testGetHistoryElementList.||elementInstanceListResult={}", elementInstanceListResult);
        StringBuilder sb = new StringBuilder();
        for (ElementInstance elementInstanceResult : elementInstanceListResult.getElementInstanceList()) {
            sb.append("[");
            sb.append(elementInstanceResult.getModelKey());
            sb.append(" ");
            sb.append(elementInstanceResult.getStatus());
            sb.append("]->");
        }
        LOGGER.info("testGetHistoryElementList.||snapshot={}", sb.toString());

        Assert.assertTrue(elementInstanceListResult.getElementInstanceList().size() == 5);
        Assert.assertTrue(StringUtils.equals(elementInstanceListResult.getElementInstanceList().get(4).getModelKey(), "EndEvent_0s4vsxw"));
    }

    // commit a,  b  回滚之后是不是之前的数据 - error
    @Test
    public void testGetInstanceData() throws Exception {
        StartProcessResult startProcessResult = startProcess();
        String flowInstanceId = startProcessResult.getFlowInstanceId();
        InstanceDataListResult instanceDataList = runtimeProcessor.getInstanceData(flowInstanceId);
        LOGGER.info("testGetInstanceData 1.||instanceDataList={}", instanceDataList);

        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        variables.add(new InstanceData("commitTime", "int", 1));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        // UserTask -> UserTask
        CommitTaskParam commitTaskParam1 = new CommitTaskParam();
        commitTaskParam1.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam1.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables1 = new ArrayList<>();
        variables1.add(new InstanceData("orderStatus", "string", "2"));
        variables1.add(new InstanceData("commitTime", "int", 2));
        commitTaskParam1.setVariables(variables1);
        CommitTaskResult commitTaskResult1 = runtimeProcessor.commit(commitTaskParam1);

        instanceDataList = runtimeProcessor.getInstanceData(flowInstanceId);
        LOGGER.info("testGetInstanceData 2.||instanceDataList={}", instanceDataList);

        // UserTask <- UserTask
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(commitTaskResult1.getActiveTaskInstance().getNodeInstanceId());
        RollbackTaskResult recallTaskResult = runtimeProcessor.rollback(rollbackTaskParam);
        LOGGER.info("recallTaskResult 3.||recallTaskResult.variables={}", recallTaskResult.getVariables());

        // UserTask <- ExclusiveGateway <- UserTask
        RollbackTaskParam rollbackTaskParam1 = new RollbackTaskParam();
        rollbackTaskParam1.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        rollbackTaskParam1.setTaskInstanceId(recallTaskResult.getActiveTaskInstance().getNodeInstanceId());
        RollbackTaskResult recallTaskResult1 = runtimeProcessor.rollback(rollbackTaskParam1);
        LOGGER.info("recallTaskResult 4.||recallTaskResult.variables={}", recallTaskResult1.getVariables());

        instanceDataList = runtimeProcessor.getInstanceData(flowInstanceId);
        LOGGER.info("testGetInstanceData 5.||instanceDataList={}", instanceDataList);
    }

    @Test
    public void testGetNodeInstance() throws Exception {
        StartProcessResult startProcessResult = startProcess();
        String flowInstanceId = startProcessResult.getFlowInstanceId();
        NodeInstanceResult nodeInstanceResult = runtimeProcessor.getNodeInstance(flowInstanceId, startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        LOGGER.info("testGetNodeInstance.||nodeInstanceResult={}", nodeInstanceResult);

        Assert.assertTrue(StringUtils.equals(nodeInstanceResult.getNodeInstance().getNodeInstanceId(), startProcessResult.getActiveTaskInstance().getNodeInstanceId()));
    }
}
