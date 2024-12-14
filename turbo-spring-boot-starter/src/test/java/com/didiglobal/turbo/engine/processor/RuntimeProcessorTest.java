package com.didiglobal.turbo.engine.processor;

import com.alibaba.fastjson.JSON;
import com.didiglobal.turbo.engine.bo.ElementInstance;
import com.didiglobal.turbo.engine.bo.NodeInstance;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.param.CommitTaskParam;
import com.didiglobal.turbo.engine.param.RollbackTaskParam;
import com.didiglobal.turbo.engine.param.StartProcessParam;
import com.didiglobal.turbo.engine.result.CommitTaskResult;
import com.didiglobal.turbo.engine.result.ElementInstanceListResult;
import com.didiglobal.turbo.engine.result.InstanceDataListResult;
import com.didiglobal.turbo.engine.result.NodeInstanceListResult;
import com.didiglobal.turbo.engine.result.NodeInstanceResult;
import com.didiglobal.turbo.engine.result.RollbackTaskResult;
import com.didiglobal.turbo.engine.result.StartProcessResult;
import com.didiglobal.turbo.engine.result.TerminateResult;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import com.didiglobal.turbo.mybatis.dao.mapper.FlowDeploymentMapper;
import com.didiglobal.turbo.mybatis.entity.FlowDeploymentEntity;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
            flowDeploymentMapper.deleteById(_flowDeploymentPO.getId());
        }
        flowDeploymentMapper.insert(FlowDeploymentEntity.of(flowDeploymentPO));

        // start process
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId(flowDeploymentPO.getFlowDeployId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("orderId", "123"));
        variables.add(new InstanceData("orderStatus", "1"));
        startProcessParam.setVariables(variables);
        // build
        return runtimeProcessor.startProcess(startProcessParam);
    }

    @Test
    public void testStartProcess() throws Exception {
        StartProcessResult startProcessResult = startProcess();
        Assert.assertEquals(startProcessResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
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
        variables.add(new InstanceData("danxuankuang_ytgyk", 1));
        commitTaskParam.setVariables(variables);

        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        LOGGER.info("testCommit.||commitTaskResult={}", commitTaskResult);
        Assert.assertEquals(commitTaskResult.getErrCode(), ErrorEnum.SUCCESS.getErrNo());
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
        variables.add(new InstanceData("danxuankuang_ytgyk", 0));
        commitTaskParam.setVariables(variables);

        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        LOGGER.info("testCommit.||commitTaskResult={}", commitTaskResult);
        Assert.assertEquals(commitTaskResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
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
        variables.add(new InstanceData("danxuankuang_ytgyk", 0));
        commitTaskParam.setVariables(variables);
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        LOGGER.info("testCommit.||commitTaskResult={}", commitTaskResult);

        Assert.assertEquals(commitTaskResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
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
        variables.add(new InstanceData("danxuankuang_ytgyk", 1));
        commitTaskParam.setVariables(variables);
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        LOGGER.info("testCommit.||commitTaskResult={}", commitTaskResult);

        Assert.assertEquals(commitTaskResult.getErrCode(), ErrorEnum.REENTRANT_WARNING.getErrNo());
    }

    @Test
    public void testCommitTerminatedFlowInstance() throws Exception {
        StartProcessResult startProcessResult = startProcess();

        runtimeProcessor.terminateProcess(startProcessResult.getFlowInstanceId(), false);

        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", 1));
        commitTaskParam.setVariables(variables);
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        Assert.assertEquals(commitTaskResult.getErrCode(), ErrorEnum.COMMIT_REJECTRD.getErrNo());
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
        variables.add(new InstanceData("danxuankuang_ytgyk", 0));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        // UserTask <- ExclusiveGateway <- UserTask
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        RollbackTaskResult rollbackTaskResult = runtimeProcessor.rollback(rollbackTaskParam);

        // commit old UserTask
        commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        LOGGER.info("testRollbackToUserTaskAndCommitOldUserTask.||commitTaskResult={}", commitTaskResult);
        Assert.assertEquals(commitTaskResult.getErrCode(), ErrorEnum.COMMIT_FAILED.getErrNo());
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
        variables.add(new InstanceData("danxuankuang_ytgyk", 0));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        // StartEvent <- UserTask
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        // Previous UserTask node
        rollbackTaskParam.setTaskInstanceId(branchUserTaskNodeInstanceId);
        RollbackTaskResult rollbackTaskResult = runtimeProcessor.rollback(rollbackTaskParam);

        // Ignore current userTask
        LOGGER.info("testRollbackFromMiddleUserTask.||rollbackTaskResult={}", rollbackTaskResult);
        Assert.assertEquals(rollbackTaskResult.getErrCode(), ErrorEnum.ROLLBACK_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(rollbackTaskResult.getActiveTaskInstance().getModelKey(), "BranchUserTask_0scrl8d"));
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
        variables.add(new InstanceData("danxuankuang_ytgyk", 0));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        // UserTask <- ExclusiveGateway <- UserTask
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        RollbackTaskResult rollbackTaskResult = runtimeProcessor.rollback(rollbackTaskParam);

        LOGGER.info("testRollback.||rollbackTaskResult={}", rollbackTaskResult);
        Assert.assertEquals(rollbackTaskResult.getErrCode(), ErrorEnum.ROLLBACK_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(rollbackTaskResult.getActiveTaskInstance().getModelKey(), "BranchUserTask_0scrl8d"));
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
        variables.add(new InstanceData("danxuankuang_ytgyk", 0));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        // UserTask <- ExclusiveGateway <- UserTask
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        RollbackTaskResult rollbackTaskResult = runtimeProcessor.rollback(rollbackTaskParam);

        // StartEvent <- UserTask
        rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(rollbackTaskResult.getActiveTaskInstance().getNodeInstanceId());
        rollbackTaskResult = runtimeProcessor.rollback(rollbackTaskParam);
        LOGGER.info("testRollback.||rollbackTaskResult={}", rollbackTaskResult);
        Assert.assertEquals(rollbackTaskResult.getErrCode(), ErrorEnum.NO_USER_TASK_TO_ROLLBACK.getErrNo());
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
        variables.add(new InstanceData("danxuankuang_ytgyk", 1));
        commitTaskParam.setVariables(variables);

        // UserTask -> EndEvent
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        // rollback EndEvent
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        RollbackTaskResult rollbackTaskResult = runtimeProcessor.rollback(rollbackTaskParam);

        LOGGER.info("testRollback.||rollbackTaskResult={}", rollbackTaskResult);
        Assert.assertEquals(rollbackTaskResult.getErrCode(), ErrorEnum.ROLLBACK_REJECTRD.getErrNo());
    }

    @Test
    public void testTerminateProcess() throws Exception {
        StartProcessResult startProcessResult = startProcess();
        TerminateResult terminateResult = runtimeProcessor.terminateProcess(startProcessResult.getFlowInstanceId(), false);
        LOGGER.info("testTerminateProcess.||terminateResult={}", terminateResult);
        Assert.assertEquals(terminateResult.getErrCode(), ErrorEnum.SUCCESS.getErrNo());
    }

    @Test
    public void testGetHistoryUserTaskList() throws Exception {
        StartProcessResult startProcessResult = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", 0));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        NodeInstanceListResult nodeInstanceListResult = runtimeProcessor.getHistoryUserTaskList(commitTaskResult.getFlowInstanceId(), false);
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

        Assert.assertEquals(2, nodeInstanceListResult.getNodeInstanceList().size());
        Assert.assertTrue(StringUtils.equals(nodeInstanceListResult.getNodeInstanceList().get(0).getModelKey(), "UserTask_0uld0u9"));
    }


    @Test
    public void testGetFailedHistoryElementList() throws Exception {
        StartProcessResult startProcessResult = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", 0));
        variables.add(new InstanceData("orderId", "notExistOrderId"));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway : Failed
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        LOGGER.info("testGetFailedHistoryElementList.||commitTaskResult={}", commitTaskResult);
        Assert.assertEquals(commitTaskResult.getErrCode(), ErrorEnum.GET_OUTGOING_FAILED.getErrNo());

        ElementInstanceListResult elementInstanceListResult = runtimeProcessor.getHistoryElementList(commitTaskResult.getFlowInstanceId(), false);
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

        Assert.assertEquals(5, elementInstanceListResult.getElementInstanceList().size());
        Assert.assertTrue(StringUtils.equals(elementInstanceListResult.getElementInstanceList().get(4).getModelKey(), "ExclusiveGateway_0yq2l0s"));
    }

    @Test
    public void testGetCompletedHistoryElementList() throws Exception {
        StartProcessResult startProcessResult = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", 1));
        commitTaskParam.setVariables(variables);

        // UserTask -> EndEvent
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        ElementInstanceListResult elementInstanceListResult = runtimeProcessor.getHistoryElementList(commitTaskResult.getFlowInstanceId(), false);
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

        Assert.assertEquals(5, elementInstanceListResult.getElementInstanceList().size());
        Assert.assertTrue(StringUtils.equals(elementInstanceListResult.getElementInstanceList().get(4).getModelKey(), "EndEvent_0s4vsxw"));
    }


    @Test
    public void testGetInstanceData() throws Exception {
        StartProcessResult startProcessResult = startProcess();
        String flowInstanceId = startProcessResult.getFlowInstanceId();
        InstanceDataListResult instanceDataList = runtimeProcessor.getInstanceData(flowInstanceId, false);
        LOGGER.info("testGetInstanceData 1.||instanceDataList={}", instanceDataList);

        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", 0));
        variables.add(new InstanceData("commitTime", 1));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);

        // UserTask -> UserTask
        CommitTaskParam commitTaskParam1 = new CommitTaskParam();
        commitTaskParam1.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam1.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables1 = new ArrayList<>();
        variables1.add(new InstanceData("orderStatus", "2"));
        variables1.add(new InstanceData("commitTime", 2));
        commitTaskParam1.setVariables(variables1);
        CommitTaskResult commitTaskResult1 = runtimeProcessor.commit(commitTaskParam1);

        instanceDataList = runtimeProcessor.getInstanceData(flowInstanceId, false);
        LOGGER.info("testGetInstanceData 2.||instanceDataList={}", instanceDataList);

        // UserTask <- UserTask
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(commitTaskResult1.getActiveTaskInstance().getNodeInstanceId());
        RollbackTaskResult rollbackTaskResult = runtimeProcessor.rollback(rollbackTaskParam);
        LOGGER.info("rollbackTaskResult 3.||rollbackTaskResult.variables={}", rollbackTaskResult.getVariables());

        // UserTask <- ExclusiveGateway <- UserTask
        RollbackTaskParam rollbackTaskParam1 = new RollbackTaskParam();
        rollbackTaskParam1.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        rollbackTaskParam1.setTaskInstanceId(rollbackTaskResult.getActiveTaskInstance().getNodeInstanceId());
        RollbackTaskResult rollbackTaskResult1 = runtimeProcessor.rollback(rollbackTaskParam1);
        LOGGER.info("rollbackTaskResult 4.||rollbackTaskResult.variables={}", rollbackTaskResult1.getVariables());

        instanceDataList = runtimeProcessor.getInstanceData(flowInstanceId, false);
        LOGGER.info("testGetInstanceData 5.||instanceDataList={}", instanceDataList);
        String initData = JSON.toJSONString(startProcessResult.getVariables());
        String rollbackData = JSON.toJSONString(rollbackTaskResult1.getVariables());
        Assert.assertTrue(StringUtils.equals(initData, rollbackData));
    }

    @Test
    public void testGetNodeInstance() throws Exception {
        StartProcessResult startProcessResult = startProcess();
        String flowInstanceId = startProcessResult.getFlowInstanceId();
        NodeInstanceResult nodeInstanceResult = runtimeProcessor.getNodeInstance(flowInstanceId, startProcessResult.getActiveTaskInstance().getNodeInstanceId(), false);
        LOGGER.info("testGetNodeInstance.||nodeInstanceResult={}", nodeInstanceResult);

        Assert.assertTrue(StringUtils.equals(nodeInstanceResult.getNodeInstance().getNodeInstanceId(), startProcessResult.getActiveTaskInstance().getNodeInstanceId()));
    }
}
