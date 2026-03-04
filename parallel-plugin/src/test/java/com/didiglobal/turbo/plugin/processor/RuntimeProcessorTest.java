package com.didiglobal.turbo.plugin.processor;

import com.didiglobal.turbo.engine.bo.ElementInstance;
import com.didiglobal.turbo.engine.bo.NodeInstance;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.dao.mapper.FlowDeploymentMapper;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.param.CommitTaskParam;
import com.didiglobal.turbo.engine.param.RollbackTaskParam;
import com.didiglobal.turbo.engine.param.StartProcessParam;
import com.didiglobal.turbo.engine.processor.RuntimeProcessor;
import com.didiglobal.turbo.engine.result.CommitTaskResult;
import com.didiglobal.turbo.engine.result.ElementInstanceListResult;
import com.didiglobal.turbo.engine.result.NodeInstanceListResult;
import com.didiglobal.turbo.engine.result.RollbackTaskResult;
import com.didiglobal.turbo.engine.result.StartProcessResult;
import com.didiglobal.turbo.engine.result.TerminateResult;
import com.didiglobal.turbo.plugin.common.ParallelErrorEnum;
import com.didiglobal.turbo.plugin.common.ParallelRuntimeContext;
import com.didiglobal.turbo.plugin.runner.BaseTest;
import com.didiglobal.turbo.plugin.util.EntityBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RuntimeProcessorTest extends BaseTest {

    @Resource
    private RuntimeProcessor runtimeProcessor;

    @Resource
    private FlowDeploymentMapper flowDeploymentMapper;

    private StartProcessResult startParallelProcess(String flag) throws Exception {
        // prepare
        FlowDeploymentPO flowDeploymentPO = EntityBuilder.buildParallelFlowDeploymentPO();
        flowDeploymentPO.setFlowModuleId(flowDeploymentPO.getFlowModuleId() + "_" + flag);
        flowDeploymentPO.setFlowDeployId(flowDeploymentPO.getFlowDeployId() + "_" + flag);
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
        variables.add(new InstanceData("orderId", "123"));
        variables.add(new InstanceData("orderStatus", "1"));
        variables.add(new InstanceData("a", 11));
        startProcessParam.setVariables(variables);
        // build
        return runtimeProcessor.startProcess(startProcessParam);
    }

    private StartProcessResult startInclusiveProcess(String flag) throws Exception {
        // prepare
        FlowDeploymentPO flowDeploymentPO = EntityBuilder.buildInclusiveFlowDeploymentPO();
        flowDeploymentPO.setFlowModuleId(flowDeploymentPO.getFlowModuleId() + "_" + flag);
        flowDeploymentPO.setFlowDeployId(flowDeploymentPO.getFlowDeployId() + "_" + flag);
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
        variables.add(new InstanceData("orderId", "123"));
        variables.add(new InstanceData("orderStatus", "1"));
        variables.add(new InstanceData("a", 11));
        startProcessParam.setVariables(variables);
        // build
        return runtimeProcessor.startProcess(startProcessParam);
    }

    private StartProcessResult startParallelProcessWithMergeOne(String flag) throws Exception {
        // prepare
        FlowDeploymentPO flowDeploymentPO = EntityBuilder.buildParallelFlowDeploymentPOWithMergeOne();
        flowDeploymentPO.setFlowModuleId(flowDeploymentPO.getFlowModuleId() + "_" + flag);
        flowDeploymentPO.setFlowDeployId(flowDeploymentPO.getFlowDeployId() + "_" + flag);
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
        variables.add(new InstanceData("orderId", "123"));
        variables.add(new InstanceData("orderStatus", "1"));
        variables.add(new InstanceData("a", 11));
        startProcessParam.setVariables(variables);
        // build
        return runtimeProcessor.startProcess(startProcessParam);
    }

    private StartProcessResult startNestedParallelProcess(String flag) {
        // prepare
        FlowDeploymentPO flowDeploymentPO = EntityBuilder.buildNestedParallelFlowDeploymentPO();
        flowDeploymentPO.setFlowModuleId(flowDeploymentPO.getFlowModuleId() + "_" + flag);
        flowDeploymentPO.setFlowDeployId(flowDeploymentPO.getFlowDeployId() + "_" + flag);
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
        variables.add(new InstanceData("aaa", 5));
        variables.add(new InstanceData("bbb", 3));
        startProcessParam.setVariables(variables);
        // build
        return runtimeProcessor.startProcess(startProcessParam);
    }

    @Test
    public void testStartProcess() throws Exception {
        StartProcessResult startProcessResult = startParallelProcess(null);
        Assert.assertEquals(startProcessResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getModelKey(), "UserTask_0iv55sh"));
        Assert.assertTrue(StringUtils.equals(startProcessResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getModelKey(), "UserTask_0m7qih6"));
    }

    /**
     *                                                                                                    --> UserTask_1ram9jm --> UserTask_32ed01b
     *                                                                                                   |                                         |
     *  StartEvent_2s70149 --> ParallelGateway_38ad233 --> UserTask_0iv55sh --> ParallelGateway_1djgrgp --> UserTask_2npcbgp --> UserTask_01tuns9 --> ParallelGateway_3a1nn9f --> ParallelGateway_10lo44j --> EndEvent_2c8j53d
     *                                                 |                                                                                                                      |
     *                                                 |                                                                                                                      |
     *                                                  -------------------------------------------------> UserTask_0m7qih6 --------------------------------------------------
     */
    @Test
    public void testParallelGateway() throws Exception {
        // Start -> UserTask_0iv55sh & UserTask_0m7qih6
        StartProcessResult startProcessResult = startParallelProcess("normal");
        Assert.assertEquals(startProcessResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getModelKey(), "UserTask_0iv55sh"));
        Assert.assertTrue(StringUtils.equals(startProcessResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getModelKey(), "UserTask_0m7qih6"));

        // UserTask_0iv55sh -> UserTask_2npcbgp & UserTask_1ram9jm
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 0));
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", 0));
        commitTaskParam.setVariables(variables);
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        Assert.assertEquals(commitTaskResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getModelKey(), "UserTask_2npcbgp"));
        Assert.assertTrue(StringUtils.equals(commitTaskResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getModelKey(), "UserTask_1ram9jm"));

        // UserTask_2npcbgp -> UserTask_01tuns9
        CommitTaskParam commitTaskParam1 = new CommitTaskParam();
        commitTaskParam1.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        commitTaskParam1.setTaskInstanceId(commitTaskResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam1.setExtendProperties(copyExtendProperties(commitTaskResult.getExtendProperties(), 0));
        List<InstanceData> variables1 = new ArrayList<>();
        variables1.add(new InstanceData("danxuankuang_ytgyk", 1));
        commitTaskParam1.setVariables(variables1);
        CommitTaskResult commitTaskResult1 = runtimeProcessor.commit(commitTaskParam1);
        Assert.assertEquals(commitTaskResult1.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult1.getActiveTaskInstance().getModelKey(), "UserTask_01tuns9"));

        // UserTask_01tuns9 -> ParallelGateway_3a1nn9f
        CommitTaskParam commitTaskParam2 = new CommitTaskParam();
        commitTaskParam2.setFlowInstanceId(commitTaskResult1.getFlowInstanceId());
        commitTaskParam2.setTaskInstanceId(commitTaskResult1.getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam2.setExtendProperties(copyExtendProperties(commitTaskResult1.getExtendProperties(), 0));
        List<InstanceData> variables2 = new ArrayList<>();
        variables2.add(new InstanceData("danxuankuang_ytgyk", 2));
        commitTaskParam2.setVariables(variables2);
        CommitTaskResult commitTaskResult2 = runtimeProcessor.commit(commitTaskParam2);
        Assert.assertEquals(commitTaskResult2.getErrCode(), ParallelErrorEnum.WAITING_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult2.getActiveTaskInstance().getModelKey(), "ParallelGateway_3a1nn9f"));

        // UserTask_1ram9jm -> UserTask_32ed01b
        CommitTaskParam commitTaskParam3 = new CommitTaskParam();
        commitTaskParam3.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        commitTaskParam3.setTaskInstanceId(commitTaskResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam3.setExtendProperties(copyExtendProperties(commitTaskResult.getExtendProperties(), 1));
        List<InstanceData> variables3 = new ArrayList<>();
        variables3.add(new InstanceData("danxuankuang_ytgyk", 3));
        commitTaskParam3.setVariables(variables3);
        CommitTaskResult commitTaskResult3 = runtimeProcessor.commit(commitTaskParam3);
        Assert.assertEquals(commitTaskResult3.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult3.getActiveTaskInstance().getModelKey(), "UserTask_32ed01b"));

        // UserTask_32ed01b -> ParallelGateway_10lo44j
        CommitTaskParam commitTaskParam4 = new CommitTaskParam();
        commitTaskParam4.setFlowInstanceId(commitTaskResult3.getFlowInstanceId());
        commitTaskParam4.setTaskInstanceId(commitTaskResult3.getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam4.setExtendProperties(copyExtendProperties(commitTaskResult3.getExtendProperties(), 0));
        List<InstanceData> variables4 = new ArrayList<>();
        variables4.add(new InstanceData("danxuankuang_ytgyk", 4));
        commitTaskParam4.setVariables(variables4);
        CommitTaskResult commitTaskResult4 = runtimeProcessor.commit(commitTaskParam4);
        Assert.assertEquals(commitTaskResult4.getErrCode(), ParallelErrorEnum.WAITING_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult4.getActiveTaskInstance().getModelKey(), "ParallelGateway_10lo44j"));

        // UserTask_0m7qih6 -> End
        CommitTaskParam commitTaskParam5 = new CommitTaskParam();
        commitTaskParam5.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam5.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam5.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 1));
        List<InstanceData> variables5 = new ArrayList<>();
        variables5.add(new InstanceData("danxuankuang_ytgyk", 5));
        commitTaskParam5.setVariables(variables5);
        CommitTaskResult commitTaskResult5 = runtimeProcessor.commit(commitTaskParam5);
        Assert.assertEquals(commitTaskResult5.getErrCode(), ErrorEnum.SUCCESS.getErrNo());
    }

    /**
     *                                                                                                    --> UserTask_1ram9jm --> UserTask_32ed01b
     *                                                                                                   |                                         |
     *  StartEvent_2s70149 --> ParallelGateway_38ad233 --> UserTask_0iv55sh --> ParallelGateway_1djgrgp --> UserTask_2npcbgp --> UserTask_01tuns9 --> ParallelGateway_3a1nn9f --> ParallelGateway_10lo44j --> EndEvent_2c8j53d
     *                                                 |                                                                                                                      |
     *                                                 |                                                                                                                      |
     *                                                  -------------------------------------------------> UserTask_0m7qih6 --------------------------------------------------
     */
    @Test
    public void testParallelGatewayRollback() throws Exception {
        // Start -> UserTask_0iv55sh & UserTask_0m7qih6
        StartProcessResult startProcessResult = startParallelProcess("rollback");
        Assert.assertEquals(startProcessResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getModelKey(), "UserTask_0iv55sh"));
        Assert.assertTrue(StringUtils.equals(startProcessResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getModelKey(), "UserTask_0m7qih6"));

        // UserTask_0iv55sh -> UserTask_2npcbgp & UserTask_1ram9jm
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 0));
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", 0));
        commitTaskParam.setVariables(variables);
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        Assert.assertEquals(commitTaskResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getModelKey(), "UserTask_2npcbgp"));
        Assert.assertTrue(StringUtils.equals(commitTaskResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getModelKey(), "UserTask_1ram9jm"));

        // UserTask_2npcbgp -> UserTask_01tuns9
        CommitTaskParam commitTaskParam1 = new CommitTaskParam();
        commitTaskParam1.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        commitTaskParam1.setTaskInstanceId(commitTaskResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam1.setExtendProperties(copyExtendProperties(commitTaskResult.getExtendProperties(), 0));
        List<InstanceData> variables1 = new ArrayList<>();
        variables1.add(new InstanceData("danxuankuang_ytgyk", 1));
        commitTaskParam1.setVariables(variables1);
        CommitTaskResult commitTaskResult1 = runtimeProcessor.commit(commitTaskParam1);
        Assert.assertEquals(commitTaskResult1.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult1.getActiveTaskInstance().getModelKey(), "UserTask_01tuns9"));

        // UserTask_01tuns9 -> UserTask_2npcbgp
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(commitTaskResult1.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(commitTaskResult1.getActiveTaskInstance().getNodeInstanceId());
        rollbackTaskParam.setExtendProperties(copyExtendProperties(commitTaskResult1.getExtendProperties(), 0));
        RollbackTaskResult rollbackTaskResult = runtimeProcessor.rollback(rollbackTaskParam);
        Assert.assertEquals(rollbackTaskResult.getErrCode(), ErrorEnum.ROLLBACK_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(rollbackTaskResult.getActiveTaskInstance().getModelKey(), "UserTask_2npcbgp"));

        // UserTask_1ram9jm -> ParallelGateway_1djgrgp (Exception)
        RollbackTaskParam rollbackTaskParam1 = new RollbackTaskParam();
        rollbackTaskParam1.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        rollbackTaskParam1.setTaskInstanceId(commitTaskResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getNodeInstanceId());
        rollbackTaskParam1.setExtendProperties(copyExtendProperties(commitTaskResult.getExtendProperties(), 1));
        RollbackTaskResult rollbackTaskResult1 = runtimeProcessor.rollback(rollbackTaskParam1);
        Assert.assertEquals(rollbackTaskResult1.getErrCode(), ParallelErrorEnum.NOT_SUPPORT_ROLLBACK.getErrNo());
    }

    /**
     *                                                                                                    --> UserTask_1ram9jm --> UserTask_32ed01b
     *                                                                                                   |                                         |
     *  StartEvent_2s70149 --> ParallelGateway_38ad233 --> UserTask_0iv55sh --> ParallelGateway_1djgrgp --> UserTask_2npcbgp --> UserTask_01tuns9 --> ParallelGateway_3a1nn9f --> ParallelGateway_10lo44j --> EndEvent_2c8j53d
     *                                                 |                                                                                                                      |
     *                                                 |                                                                                                                      |
     *                                                  -------------------------------------------------> UserTask_0m7qih6 --------------------------------------------------
     */
    @Test
    public void testParallelGatewayTerminate() throws Exception {
        // Start -> UserTask_0iv55sh & UserTask_0m7qih6
        StartProcessResult startProcessResult = startParallelProcess("terminate");
        Assert.assertEquals(startProcessResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getModelKey(), "UserTask_0iv55sh"));
        Assert.assertTrue(StringUtils.equals(startProcessResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getModelKey(), "UserTask_0m7qih6"));

        // UserTask_0iv55sh -> UserTask_2npcbgp & UserTask_1ram9jm
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 0));
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", 0));
        commitTaskParam.setVariables(variables);
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        Assert.assertEquals(commitTaskResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getModelKey(), "UserTask_2npcbgp"));
        Assert.assertTrue(StringUtils.equals(commitTaskResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getModelKey(), "UserTask_1ram9jm"));

        // UserTask_2npcbgp -> UserTask_01tuns9
        CommitTaskParam commitTaskParam1 = new CommitTaskParam();
        commitTaskParam1.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        commitTaskParam1.setTaskInstanceId(commitTaskResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam1.setExtendProperties(copyExtendProperties(commitTaskResult.getExtendProperties(), 0));
        List<InstanceData> variables1 = new ArrayList<>();
        variables1.add(new InstanceData("danxuankuang_ytgyk", 1));
        commitTaskParam1.setVariables(variables1);
        CommitTaskResult commitTaskResult1 = runtimeProcessor.commit(commitTaskParam1);
        Assert.assertEquals(commitTaskResult1.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult1.getActiveTaskInstance().getModelKey(), "UserTask_01tuns9"));

        // UserTask_1ram9jm -> UserTask_32ed01b
        CommitTaskParam commitTaskParam2 = new CommitTaskParam();
        commitTaskParam2.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        commitTaskParam2.setTaskInstanceId(commitTaskResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam2.setExtendProperties(copyExtendProperties(commitTaskResult.getExtendProperties(), 1));
        List<InstanceData> variables2 = new ArrayList<>();
        variables2.add(new InstanceData("danxuankuang_ytgyk", 2));
        commitTaskParam2.setVariables(variables2);
        CommitTaskResult commitTaskResult2 = runtimeProcessor.commit(commitTaskParam2);
        Assert.assertEquals(commitTaskResult2.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult2.getActiveTaskInstance().getModelKey(), "UserTask_32ed01b"));

        // UserTask_32ed01b Terminate
        TerminateResult terminateResult = runtimeProcessor.terminateProcess(commitTaskResult2.getFlowInstanceId(), false);
        Assert.assertEquals(terminateResult.getErrCode(), ErrorEnum.SUCCESS.getErrNo());

        // UserTask_01tuns9 Commit (Exception)
        CommitTaskParam commitTaskParam3 = new CommitTaskParam();
        commitTaskParam3.setFlowInstanceId(commitTaskResult1.getFlowInstanceId());
        commitTaskParam3.setTaskInstanceId(commitTaskResult1.getActiveTaskInstance().getNodeInstanceId());
        CommitTaskResult commitTaskResult3 = runtimeProcessor.commit(commitTaskParam3);
        Assert.assertTrue(commitTaskResult3.getErrCode() == ErrorEnum.COMMIT_REJECTRD.getErrNo());
    }

    /**
     *                                                                                                    --> UserTask_1ram9jm --> UserTask_32ed01b
     *                                                                                                   |                                         |
     *  StartEvent_2s70149 --> ParallelGateway_38ad233 --> UserTask_0iv55sh --> ParallelGateway_1djgrgp --> UserTask_2npcbgp --> UserTask_01tuns9 --> ParallelGateway_3a1nn9f --> ParallelGateway_10lo44j --> EndEvent_2c8j53d
     *                                                 |                                                                                                                      |
     *                                                 |                                                                                                                      |
     *                                                  -------------------------------------------------> UserTask_0m7qih6 --------------------------------------------------
     */
    @Test
    public void testParallelGatewayMergeOne() throws Exception {
        // Start -> UserTask_0iv55sh & UserTask_0m7qih6
        StartProcessResult startProcessResult = startParallelProcessWithMergeOne("normal");
        Assert.assertEquals(startProcessResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getModelKey(), "UserTask_0iv55sh"));
        Assert.assertTrue(StringUtils.equals(startProcessResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getModelKey(), "UserTask_0m7qih6"));

        // UserTask_0iv55sh -> UserTask_2npcbgp & UserTask_1ram9jm
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 0));
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", 0));
        commitTaskParam.setVariables(variables);
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        Assert.assertEquals(commitTaskResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getModelKey(), "UserTask_2npcbgp"));
        Assert.assertTrue(StringUtils.equals(commitTaskResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getModelKey(), "UserTask_1ram9jm"));

        // UserTask_2npcbgp -> UserTask_01tuns9
        CommitTaskParam commitTaskParam1 = new CommitTaskParam();
        commitTaskParam1.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        commitTaskParam1.setTaskInstanceId(commitTaskResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam1.setExtendProperties(copyExtendProperties(commitTaskResult.getExtendProperties(), 0));
        List<InstanceData> variables1 = new ArrayList<>();
        variables1.add(new InstanceData("danxuankuang_ytgyk", 1));
        commitTaskParam1.setVariables(variables1);
        CommitTaskResult commitTaskResult1 = runtimeProcessor.commit(commitTaskParam1);
        Assert.assertEquals(commitTaskResult1.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult1.getActiveTaskInstance().getModelKey(), "UserTask_01tuns9"));

        // UserTask_01tuns9 -> ParallelGateway_10lo44j
        CommitTaskParam commitTaskParam2 = new CommitTaskParam();
        commitTaskParam2.setFlowInstanceId(commitTaskResult1.getFlowInstanceId());
        commitTaskParam2.setTaskInstanceId(commitTaskResult1.getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam2.setExtendProperties(copyExtendProperties(commitTaskResult1.getExtendProperties(), 0));
        List<InstanceData> variables2 = new ArrayList<>();
        variables2.add(new InstanceData("danxuankuang_ytgyk", 2));
        commitTaskParam2.setVariables(variables2);
        CommitTaskResult commitTaskResult2 = runtimeProcessor.commit(commitTaskParam2);
        Assert.assertEquals(commitTaskResult2.getErrCode(), ParallelErrorEnum.WAITING_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult2.getActiveTaskInstance().getModelKey(), "ParallelGateway_10lo44j"));

        // UserTask_1ram9jm Commit (Exception)
        CommitTaskParam commitTaskParam3 = new CommitTaskParam();
        commitTaskParam3.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        commitTaskParam3.setTaskInstanceId(commitTaskResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam3.setExtendProperties(copyExtendProperties(commitTaskResult.getExtendProperties(), 1));
        List<InstanceData> variables3 = new ArrayList<>();
        variables3.add(new InstanceData("danxuankuang_ytgyk", 3));
        commitTaskParam3.setVariables(variables3);
        CommitTaskResult commitTaskResult3 = runtimeProcessor.commit(commitTaskParam3);
        Assert.assertEquals(commitTaskResult3.getErrCode(), ErrorEnum.COMMIT_FAILED.getErrNo());
    }

    /**
     *                                                                                                    -❌-> UserTask_1ram9jm --> UserTask_01tuns9
     *                                                                                                   |                                         |
     *  StartEvent_2s70149 --> ParallelGateway_38ad233 --> UserTask_0iv55sh --> InclusiveGateway_1djgrgp -✅-> UserTask_2npcbgp --> UserTask_32ed01b --> InclusiveGateway_3a1nn9f --> ParallelGateway_10lo44j --> EndEvent_2c8j53d
     *                                                 |                                                                                                                        |
     *                                                 |                                                                                                                        |
     *                                                  -------------------------------------------------> UserTask_0m7qih6 ----------------------------------------------------
     */
    @Test
    public void testInclusiveGateway() throws Exception {
        // Start -> UserTask_0iv55sh & UserTask_0m7qih6
        StartProcessResult startProcessResult = startInclusiveProcess("normal");
        Assert.assertEquals(startProcessResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getModelKey(), "UserTask_0iv55sh"));
        Assert.assertTrue(StringUtils.equals(startProcessResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getModelKey(), "UserTask_0m7qih6"));

        // UserTask_0iv55sh -> UserTask_2npcbgp
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 0));
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", 0));
        commitTaskParam.setVariables(variables);
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        Assert.assertEquals(commitTaskResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertEquals(commitTaskResult.getNodeExecuteResults().size(), 1);
        Assert.assertTrue(StringUtils.equals(commitTaskResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getModelKey(), "UserTask_2npcbgp"));

        // UserTask_2npcbgp -> UserTask_01tuns9
        CommitTaskParam commitTaskParam1 = new CommitTaskParam();
        commitTaskParam1.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        commitTaskParam1.setTaskInstanceId(commitTaskResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam1.setExtendProperties(copyExtendProperties(commitTaskResult.getExtendProperties(), 0));
        List<InstanceData> variables1 = new ArrayList<>();
        variables1.add(new InstanceData("danxuankuang_ytgyk", 1));
        commitTaskParam1.setVariables(variables1);
        CommitTaskResult commitTaskResult1 = runtimeProcessor.commit(commitTaskParam1);
        Assert.assertEquals(commitTaskResult1.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult1.getActiveTaskInstance().getModelKey(), "UserTask_01tuns9"));

        // UserTask_01tuns9 -> ParallelGateway_10lo44j
        CommitTaskParam commitTaskParam2 = new CommitTaskParam();
        commitTaskParam2.setFlowInstanceId(commitTaskResult1.getFlowInstanceId());
        commitTaskParam2.setTaskInstanceId(commitTaskResult1.getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam2.setExtendProperties(copyExtendProperties(commitTaskResult1.getExtendProperties(), 0));
        List<InstanceData> variables2 = new ArrayList<>();
        variables2.add(new InstanceData("danxuankuang_ytgyk", 2));
        commitTaskParam2.setVariables(variables2);
        CommitTaskResult commitTaskResult2 = runtimeProcessor.commit(commitTaskParam2);
        Assert.assertEquals(commitTaskResult2.getErrCode(), ParallelErrorEnum.WAITING_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult2.getActiveTaskInstance().getModelKey(), "ParallelGateway_10lo44j"));

        // UserTask_0m7qih6 -> End
        CommitTaskParam commitTaskParam5 = new CommitTaskParam();
        commitTaskParam5.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam5.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam5.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 1));
        List<InstanceData> variables5 = new ArrayList<>();
        variables5.add(new InstanceData("danxuankuang_ytgyk", 5));
        commitTaskParam5.setVariables(variables5);
        CommitTaskResult commitTaskResult5 = runtimeProcessor.commit(commitTaskParam5);
        Assert.assertEquals(commitTaskResult5.getErrCode(), ErrorEnum.SUCCESS.getErrNo());
    }

    /**
     *                                                                                                    -❌-> UserTask_1ram9jm --> UserTask_01tuns9
     *                                                                                                   |                                         |
     *  StartEvent_2s70149 --> ParallelGateway_38ad233 --> UserTask_0iv55sh --> InclusiveGateway_1djgrgp -✅-> UserTask_2npcbgp --> UserTask_32ed01b --> InclusiveGateway_3a1nn9f --> ParallelGateway_10lo44j --> EndEvent_2c8j53d
     *                                                 |                                                                                                                        |
     *                                                 |                                                                                                                        |
     *                                                  -------------------------------------------------> UserTask_0m7qih6 ----------------------------------------------------
     */
    @Test
    public void testInclusiveGatewayRollback() throws Exception {
        // Start -> UserTask_0iv55sh & UserTask_0m7qih6
        StartProcessResult startProcessResult = startInclusiveProcess("rollback");
        Assert.assertEquals(startProcessResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getModelKey(), "UserTask_0iv55sh"));
        Assert.assertTrue(StringUtils.equals(startProcessResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getModelKey(), "UserTask_0m7qih6"));

        // UserTask_0iv55sh -> UserTask_2npcbgp
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 0));
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", 0));
        commitTaskParam.setVariables(variables);
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        Assert.assertEquals(commitTaskResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertEquals(commitTaskResult.getNodeExecuteResults().size(), 1);
        Assert.assertTrue(StringUtils.equals(commitTaskResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getModelKey(), "UserTask_2npcbgp"));

        // UserTask_2npcbgp -> UserTask_01tuns9
        CommitTaskParam commitTaskParam1 = new CommitTaskParam();
        commitTaskParam1.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        commitTaskParam1.setTaskInstanceId(commitTaskResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam1.setExtendProperties(copyExtendProperties(commitTaskResult.getExtendProperties(), 0));
        List<InstanceData> variables1 = new ArrayList<>();
        variables1.add(new InstanceData("danxuankuang_ytgyk", 1));
        commitTaskParam1.setVariables(variables1);
        CommitTaskResult commitTaskResult1 = runtimeProcessor.commit(commitTaskParam1);
        Assert.assertEquals(commitTaskResult1.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult1.getActiveTaskInstance().getModelKey(), "UserTask_01tuns9"));

        // UserTask_01tuns9 -> UserTask_2npcbgp
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(commitTaskResult1.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(commitTaskResult1.getActiveTaskInstance().getNodeInstanceId());
        rollbackTaskParam.setExtendProperties(copyExtendProperties(commitTaskResult1.getExtendProperties(), 0));
        RollbackTaskResult rollbackTaskResult = runtimeProcessor.rollback(rollbackTaskParam);
        Assert.assertEquals(rollbackTaskResult.getErrCode(), ErrorEnum.ROLLBACK_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(rollbackTaskResult.getActiveTaskInstance().getModelKey(), "UserTask_2npcbgp"));

        // UserTask_2npcbgp -> InclusiveGateway_1djgrgp (Exception)
        RollbackTaskParam rollbackTaskParam1 = new RollbackTaskParam();
        rollbackTaskParam1.setFlowInstanceId(rollbackTaskResult.getFlowInstanceId());
        rollbackTaskParam1.setTaskInstanceId(rollbackTaskResult.getActiveTaskInstance().getNodeInstanceId());
        rollbackTaskParam1.setExtendProperties(copyExtendProperties(rollbackTaskResult.getExtendProperties(), 0));
        RollbackTaskResult rollbackTaskResult1 = runtimeProcessor.rollback(rollbackTaskParam1);
        Assert.assertEquals(rollbackTaskResult1.getErrCode(), ParallelErrorEnum.NOT_SUPPORT_ROLLBACK.getErrNo());

        // UserTask_2npcbgp -> UserTask_01tuns9
        CommitTaskParam commitTaskParam2 = new CommitTaskParam();
        commitTaskParam2.setFlowInstanceId(rollbackTaskResult.getFlowInstanceId());
        commitTaskParam2.setTaskInstanceId(rollbackTaskResult.getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam2.setExtendProperties(copyExtendProperties(rollbackTaskResult.getExtendProperties(), 0));
        List<InstanceData> variables2 = new ArrayList<>();
        variables2.add(new InstanceData("danxuankuang_ytgyk", 2));
        commitTaskParam2.setVariables(variables2);
        CommitTaskResult commitTaskResult2 = runtimeProcessor.commit(commitTaskParam2);
        Assert.assertEquals(commitTaskResult2.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult2.getActiveTaskInstance().getModelKey(), "UserTask_01tuns9"));

        // UserTask_01tuns9 -> ParallelGateway_10lo44j
        CommitTaskParam commitTaskParam3 = new CommitTaskParam();
        commitTaskParam3.setFlowInstanceId(commitTaskResult2.getFlowInstanceId());
        commitTaskParam3.setTaskInstanceId(commitTaskResult2.getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam3.setExtendProperties(copyExtendProperties(commitTaskResult2.getExtendProperties(), 0));
        List<InstanceData> variables3 = new ArrayList<>();
        variables3.add(new InstanceData("danxuankuang_ytgyk", 3));
        commitTaskParam3.setVariables(variables3);
        CommitTaskResult commitTaskResult3 = runtimeProcessor.commit(commitTaskParam3);
        Assert.assertEquals(commitTaskResult3.getErrCode(), ParallelErrorEnum.WAITING_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult3.getActiveTaskInstance().getModelKey(), "ParallelGateway_10lo44j"));

        // UserTask_0m7qih6 -> End
        CommitTaskParam commitTaskParam5 = new CommitTaskParam();
        commitTaskParam5.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam5.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam5.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 1));
        List<InstanceData> variables5 = new ArrayList<>();
        variables5.add(new InstanceData("danxuankuang_ytgyk", 5));
        commitTaskParam5.setVariables(variables5);
        CommitTaskResult commitTaskResult5 = runtimeProcessor.commit(commitTaskParam5);
        Assert.assertEquals(commitTaskResult5.getErrCode(), ErrorEnum.SUCCESS.getErrNo());
    }

    /**
     *                                                                                                    -❌-> UserTask_1ram9jm --> UserTask_01tuns9
     *                                                                                                   |                                         |
     *  StartEvent_2s70149 --> ParallelGateway_38ad233 --> UserTask_0iv55sh --> InclusiveGateway_1djgrgp -✅-> UserTask_2npcbgp --> UserTask_32ed01b --> InclusiveGateway_3a1nn9f --> ParallelGateway_10lo44j --> EndEvent_2c8j53d
     *                                                 |                                                                                                                        |
     *                                                 |                                                                                                                        |
     *                                                  -------------------------------------------------> UserTask_0m7qih6 ----------------------------------------------------
     */
    @Test
    public void testInclusiveGatewayTerminate() throws Exception {
        // Start -> UserTask_0iv55sh & UserTask_0m7qih6
        StartProcessResult startProcessResult = startInclusiveProcess("terminate");
        Assert.assertEquals(startProcessResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getModelKey(), "UserTask_0iv55sh"));
        Assert.assertTrue(StringUtils.equals(startProcessResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getModelKey(), "UserTask_0m7qih6"));

        // UserTask_0iv55sh -> UserTask_2npcbgp
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 0));
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", 0));
        commitTaskParam.setVariables(variables);
        CommitTaskResult commitTaskResult = runtimeProcessor.commit(commitTaskParam);
        Assert.assertEquals(commitTaskResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertEquals(commitTaskResult.getNodeExecuteResults().size(), 1);
        Assert.assertTrue(StringUtils.equals(commitTaskResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getModelKey(), "UserTask_2npcbgp"));

        // UserTask_2npcbgp Terminate
        TerminateResult terminateResult = runtimeProcessor.terminateProcess(commitTaskResult.getFlowInstanceId(), false);
        Assert.assertEquals(terminateResult.getErrCode(), ErrorEnum.SUCCESS.getErrNo());

        // UserTask_0m7qih6 Commit (Exception)
        CommitTaskParam commitTaskParam5 = new CommitTaskParam();
        commitTaskParam5.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam5.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam5.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 1));
        List<InstanceData> variables5 = new ArrayList<>();
        variables5.add(new InstanceData("danxuankuang_ytgyk", 5));
        commitTaskParam5.setVariables(variables5);
        CommitTaskResult commitTaskResult5 = runtimeProcessor.commit(commitTaskParam5);
        Assert.assertEquals(commitTaskResult5.getErrCode(), ErrorEnum.COMMIT_REJECTRD.getErrNo());
    }

    /**
     *                                                                     |---> 二级ParallelFork ---> UserTask_1e0chov(1-1) -d4-|
     *                                  |---> ExclusiveGateway_3uecfsr ----|                                                   |---> 二级ParallelJoin --|
     *                                  |                                  |---> 二级ParallelFork ---> UserTask_0skk1nb(1-2) -b2-|                         |
     *                                  |                                                                                                                |
     *  StartEvent ---> 一级ParallelFork |---> ExclusiveGateway_30qligf ---> UserTask_1sirm1d(2) -------c3-------------------------------------------------|---> 一级ParallelJoin ---> UserTask_21bshkk(success) ---> EndEvent
     *                                  |                                                                                                                |
     *                                  |---> ExclusiveGateway_3ad9clv ---> UserTask_321tjcu(3) -------a1--------------------------------------------------|
     */
    @Test
    public void testNestedParallelGateway() throws Exception {
        // startEvent -> UserTask_1e0chov、UserTask_0skk1nb、UserTask_1sirm1d、UserTask_321tjcu
        StartProcessResult startProcessResult = startNestedParallelProcess("test");
        Assert.assertEquals(startProcessResult.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertEquals(startProcessResult.getNodeExecuteResults().size(), 4);
        // 找到各个任务的索引
        int task1e0chovIndex = -1;
        int task0skk1nbIndex = -1;
        int task1sirm1dIndex = -1;
        int task321tjcuIndex = -1;
        for (int i = 0; i < startProcessResult.getNodeExecuteResults().size(); i++) {
            String modelKey = startProcessResult.getNodeExecuteResults().get(i).getActiveTaskInstance().getModelKey();
            if (StringUtils.equals(modelKey, "UserTask_1e0chov")) {
                task1e0chovIndex = i;
            } else if (StringUtils.equals(modelKey, "UserTask_0skk1nb")) {
                task0skk1nbIndex = i;
            } else if (StringUtils.equals(modelKey, "UserTask_1sirm1d")) {
                task1sirm1dIndex = i;
            } else if (StringUtils.equals(modelKey, "UserTask_321tjcu")) {
                task321tjcuIndex = i;
            }
        }
        Assert.assertTrue(task1e0chovIndex >= 0);
        Assert.assertTrue(task0skk1nbIndex >= 0);
        Assert.assertTrue(task1sirm1dIndex >= 0);
        Assert.assertTrue(task321tjcuIndex >= 0);

        // commit UserTask_321tjcu -> 一级ParallelJoin(waiting)
        CommitTaskParam commitTaskParam1 = new CommitTaskParam();
        commitTaskParam1.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam1.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(task321tjcuIndex).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam1.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), task321tjcuIndex));
        List<InstanceData> variables1 = new ArrayList<>();
        variables1.add(new InstanceData("a", 1));
        commitTaskParam1.setVariables(variables1);
        CommitTaskResult commitTaskResult1 = runtimeProcessor.commit(commitTaskParam1);
        Assert.assertEquals(commitTaskResult1.getErrCode(), ParallelErrorEnum.WAITING_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult1.getActiveTaskInstance().getModelKey(), "ParallelGateway_00ic9ii"));

        // commit UserTask_0skk1nb -> 二级ParallelJoin(waiting)
        CommitTaskParam commitTaskParam2 = new CommitTaskParam();
        commitTaskParam2.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam2.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(task0skk1nbIndex).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam2.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), task0skk1nbIndex));
        List<InstanceData> variables2 = new ArrayList<>();
        variables2.add(new InstanceData("b", 2));
        commitTaskParam2.setVariables(variables2);
        CommitTaskResult commitTaskResult2 = runtimeProcessor.commit(commitTaskParam2);
        Assert.assertEquals(commitTaskResult2.getErrCode(), ParallelErrorEnum.WAITING_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult2.getActiveTaskInstance().getModelKey(), "ParallelGateway_28cge4l"));

        // commit UserTask_1sirm1d -> 一级ParallelJoin(waiting)
        CommitTaskParam commitTaskParam3 = new CommitTaskParam();
        commitTaskParam3.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam3.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(task1sirm1dIndex).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam3.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), task1sirm1dIndex));
        List<InstanceData> variables3 = new ArrayList<>();
        variables3.add(new InstanceData("c", 3));
        commitTaskParam3.setVariables(variables3);
        CommitTaskResult commitTaskResult3 = runtimeProcessor.commit(commitTaskParam3);
        Assert.assertEquals(commitTaskResult3.getErrCode(), ParallelErrorEnum.WAITING_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult3.getActiveTaskInstance().getModelKey(), "ParallelGateway_00ic9ii"));

        // commit UserTask_1e0chov -> 二级ParallelJoin -> 一级ParallelJoin -> UserTask_21bshkk
        CommitTaskParam commitTaskParam4 = new CommitTaskParam();
        commitTaskParam4.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam4.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(task1e0chovIndex).getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam4.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), task1e0chovIndex));
        List<InstanceData> variables4 = new ArrayList<>();
        variables4.add(new InstanceData("d", 4));
        commitTaskParam4.setVariables(variables4);
        CommitTaskResult commitTaskResult4 = runtimeProcessor.commit(commitTaskParam4);
        Assert.assertEquals(commitTaskResult4.getErrCode(), ErrorEnum.COMMIT_SUSPEND.getErrNo());
        Assert.assertTrue(StringUtils.equals(commitTaskResult4.getActiveTaskInstance().getModelKey(), "UserTask_21bshkk"));

        // commit UserTask_21bshkk -> EndEvent
        CommitTaskParam commitTaskParam5 = new CommitTaskParam();
        commitTaskParam5.setFlowInstanceId(commitTaskResult4.getFlowInstanceId());
        commitTaskParam5.setTaskInstanceId(commitTaskResult4.getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam5.setExtendProperties(copyExtendProperties(commitTaskResult4.getExtendProperties(), 0));
        List<InstanceData> variables5 = new ArrayList<>(commitTaskResult4.getVariables());
        variables5.add(new InstanceData("e", 5));
        commitTaskParam5.setVariables(variables5);
        CommitTaskResult commitTaskResult5 = runtimeProcessor.commit(commitTaskParam5);
        Assert.assertEquals(commitTaskResult5.getErrCode(), ErrorEnum.SUCCESS.getErrNo());
    }

    /**
     * Verifies that getHistoryElementList returns ALL sequence-flow edges for join nodes in a
     * completed parallel gateway flow, including the edges from branches that arrived non-first.
     *
     * Before the fix, only the FIRST branch's edge was emitted for each join gateway.
     * After the fix, all N branches' incoming edges appear (via ei_node_instance_join_source).
     *
     * Flow:
     *                                                                                                    --> UserTask_1ram9jm --> UserTask_32ed01b
     *                                                                                                   |                                         |
     *  StartEvent_2s70149 --> ParallelGateway_38ad233 --> UserTask_0iv55sh --> ParallelGateway_1djgrgp --> UserTask_2npcbgp --> UserTask_01tuns9 --> ParallelGateway_3a1nn9f --> ParallelGateway_10lo44j --> EndEvent_2c8j53d
     *                                                 |                                                                                                                      |
     *                                                  -------------------------------------------------> UserTask_0m7qih6 --------------------------------------------------
     *
     * Expected element count after full completion = 27:
     *   1 (StartEvent) + 11 * 2 (normal nodes with 1 edge each) + 2 * 1 (extra edge for each 2-branch join) = 27
     */
    @Test
    public void testParallelGatewayGetHistoryElementList() throws Exception {
        // Run full parallel flow to completion (same steps as testParallelGateway, flag="history_element")
        StartProcessResult startProcessResult = startParallelProcess("history_element");
        Assert.assertEquals(ErrorEnum.COMMIT_SUSPEND.getErrNo(), startProcessResult.getErrCode());

        // UserTask_0iv55sh -> ParallelGateway_1djgrgp (fork) -> UserTask_2npcbgp & UserTask_1ram9jm
        CommitTaskParam cp1 = new CommitTaskParam();
        cp1.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        cp1.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        cp1.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 0));
        cp1.setVariables(List.of(new InstanceData("danxuankuang_ytgyk", 0)));
        CommitTaskResult cr1 = runtimeProcessor.commit(cp1);
        Assert.assertEquals(ErrorEnum.COMMIT_SUSPEND.getErrNo(), cr1.getErrCode());

        // UserTask_2npcbgp -> UserTask_01tuns9
        CommitTaskParam cp2 = new CommitTaskParam();
        cp2.setFlowInstanceId(cr1.getFlowInstanceId());
        cp2.setTaskInstanceId(cr1.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        cp2.setExtendProperties(copyExtendProperties(cr1.getExtendProperties(), 0));
        cp2.setVariables(List.of(new InstanceData("danxuankuang_ytgyk", 1)));
        CommitTaskResult cr2 = runtimeProcessor.commit(cp2);
        Assert.assertEquals(ErrorEnum.COMMIT_SUSPEND.getErrNo(), cr2.getErrCode());

        // UserTask_01tuns9 -> ParallelGateway_3a1nn9f (first arrival, WAITING)
        CommitTaskParam cp3 = new CommitTaskParam();
        cp3.setFlowInstanceId(cr2.getFlowInstanceId());
        cp3.setTaskInstanceId(cr2.getActiveTaskInstance().getNodeInstanceId());
        cp3.setExtendProperties(copyExtendProperties(cr2.getExtendProperties(), 0));
        cp3.setVariables(List.of(new InstanceData("danxuankuang_ytgyk", 2)));
        CommitTaskResult cr3 = runtimeProcessor.commit(cp3);
        Assert.assertEquals(ParallelErrorEnum.WAITING_SUSPEND.getErrNo(), cr3.getErrCode());

        // UserTask_1ram9jm -> UserTask_32ed01b
        CommitTaskParam cp4 = new CommitTaskParam();
        cp4.setFlowInstanceId(cr1.getFlowInstanceId());
        cp4.setTaskInstanceId(cr1.getNodeExecuteResults().get(1).getActiveTaskInstance().getNodeInstanceId());
        cp4.setExtendProperties(copyExtendProperties(cr1.getExtendProperties(), 1));
        cp4.setVariables(List.of(new InstanceData("danxuankuang_ytgyk", 3)));
        CommitTaskResult cr4 = runtimeProcessor.commit(cp4);
        Assert.assertEquals(ErrorEnum.COMMIT_SUSPEND.getErrNo(), cr4.getErrCode());

        // UserTask_32ed01b -> ParallelGateway_3a1nn9f (second arrival, COMPLETED) -> ParallelGateway_10lo44j (first, WAITING)
        CommitTaskParam cp5 = new CommitTaskParam();
        cp5.setFlowInstanceId(cr4.getFlowInstanceId());
        cp5.setTaskInstanceId(cr4.getActiveTaskInstance().getNodeInstanceId());
        cp5.setExtendProperties(copyExtendProperties(cr4.getExtendProperties(), 0));
        cp5.setVariables(List.of(new InstanceData("danxuankuang_ytgyk", 4)));
        CommitTaskResult cr5 = runtimeProcessor.commit(cp5);
        Assert.assertEquals(ParallelErrorEnum.WAITING_SUSPEND.getErrNo(), cr5.getErrCode());

        // UserTask_0m7qih6 -> ParallelGateway_10lo44j (second arrival, COMPLETED) -> EndEvent -> SUCCESS
        CommitTaskParam cp6 = new CommitTaskParam();
        cp6.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        cp6.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getNodeInstanceId());
        cp6.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 1));
        cp6.setVariables(List.of(new InstanceData("danxuankuang_ytgyk", 5)));
        CommitTaskResult cr6 = runtimeProcessor.commit(cp6);
        Assert.assertEquals(ErrorEnum.SUCCESS.getErrNo(), cr6.getErrCode());

        // Verify getHistoryElementList
        String flowInstanceId = startProcessResult.getFlowInstanceId();
        ElementInstanceListResult result = runtimeProcessor.getHistoryElementList(flowInstanceId, false);

        Assert.assertEquals(ErrorEnum.SUCCESS.getErrNo(), result.getErrCode());
        List<ElementInstance> elements = result.getElementInstanceList();

        // Collect all element model keys for easy assertion
        List<String> elementKeys = elements.stream()
                .map(ElementInstance::getModelKey)
                .collect(Collectors.toList());
        LOGGER.info("testParallelGatewayGetHistoryElementList.||elementKeys={}", elementKeys);

        // ---- Verify join node ParallelGateway_3a1nn9f has BOTH incoming edges ----
        // SequenceFlow_1h65e8t: UserTask_01tuns9 -> ParallelGateway_3a1nn9f (stored in sourceNodeKey - first branch)
        Assert.assertTrue("Missing SequenceFlow_1h65e8t (first branch → PG_3a1nn9f)",
                elementKeys.contains("SequenceFlow_1h65e8t"));
        // SequenceFlow_25kdv36: UserTask_32ed01b -> ParallelGateway_3a1nn9f (from additionalSourceNodeKeys - fix)
        Assert.assertTrue("Missing SequenceFlow_25kdv36 (second branch → PG_3a1nn9f) - getHistory fix regression",
                elementKeys.contains("SequenceFlow_25kdv36"));

        // ---- Verify join node ParallelGateway_10lo44j has BOTH incoming edges ----
        // SequenceFlow_3jkd63g: ParallelGateway_3a1nn9f -> ParallelGateway_10lo44j (first branch)
        Assert.assertTrue("Missing SequenceFlow_3jkd63g (first branch → PG_10lo44j)",
                elementKeys.contains("SequenceFlow_3jkd63g"));
        // SequenceFlow_3bgdrp0: UserTask_0m7qih6 -> ParallelGateway_10lo44j (from additionalSourceNodeKeys - fix)
        Assert.assertTrue("Missing SequenceFlow_3bgdrp0 (second branch → PG_10lo44j) - getHistory fix regression",
                elementKeys.contains("SequenceFlow_3bgdrp0"));

        // ---- Verify all nodes are present ----
        Assert.assertTrue(elementKeys.contains("StartEvent_2s70149"));
        Assert.assertTrue(elementKeys.contains("ParallelGateway_38ad233"));
        Assert.assertTrue(elementKeys.contains("UserTask_0iv55sh"));
        Assert.assertTrue(elementKeys.contains("UserTask_0m7qih6"));
        Assert.assertTrue(elementKeys.contains("ParallelGateway_1djgrgp"));
        Assert.assertTrue(elementKeys.contains("UserTask_2npcbgp"));
        Assert.assertTrue(elementKeys.contains("UserTask_1ram9jm"));
        Assert.assertTrue(elementKeys.contains("UserTask_01tuns9"));
        Assert.assertTrue(elementKeys.contains("UserTask_32ed01b"));
        Assert.assertTrue(elementKeys.contains("ParallelGateway_3a1nn9f"));
        Assert.assertTrue(elementKeys.contains("ParallelGateway_10lo44j"));
        Assert.assertTrue(elementKeys.contains("EndEvent_2c8j53d"));

        // ---- Verify exact element count: 27 ----
        // Breakdown: 12 node instances each contribute:
        //   StartEvent                       : 0 edges + 1 node =  1
        //   PG_38ad233, UT_0iv55sh, UT_0m7qih6, PG_1djgrgp,
        //   UT_2npcbgp, UT_1ram9jm, UT_01tuns9, UT_32ed01b, EndEvent (9 nodes): 1 edge + 1 node = 18
        //   PG_3a1nn9f (2-branch join)       : 2 edges + 1 node =  3  ← additionalSourceNodeKeys fix
        //   PG_10lo44j (2-branch join)       : 2 edges + 1 node =  3  ← additionalSourceNodeKeys fix
        //   Total = 1 + 18 + 3 + 3 = 27
        Assert.assertEquals("Element list size must be exactly 27 for completed parallel flow", 27, elements.size());
    }

    /**
     * Verifies that getHistoryUserTaskList is NOT broken by the parallel gateway fix.
     * It should still return all completed user tasks in a completed parallel flow.
     *
     * Flow same as testParallelGatewayGetHistoryElementList.
     * Expected: 6 user tasks in DESC id order:
     *   UserTask_32ed01b, UserTask_01tuns9, UserTask_1ram9jm, UserTask_2npcbgp, UserTask_0m7qih6, UserTask_0iv55sh
     */
    @Test
    public void testParallelGatewayGetHistoryUserTaskList() throws Exception {
        // Run full parallel flow to completion (flag="history_usertask")
        StartProcessResult startProcessResult = startParallelProcess("history_usertask");
        Assert.assertEquals(ErrorEnum.COMMIT_SUSPEND.getErrNo(), startProcessResult.getErrCode());

        CommitTaskParam cp1 = new CommitTaskParam();
        cp1.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        cp1.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        cp1.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 0));
        cp1.setVariables(List.of(new InstanceData("danxuankuang_ytgyk", 0)));
        CommitTaskResult cr1 = runtimeProcessor.commit(cp1);

        CommitTaskParam cp2 = new CommitTaskParam();
        cp2.setFlowInstanceId(cr1.getFlowInstanceId());
        cp2.setTaskInstanceId(cr1.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        cp2.setExtendProperties(copyExtendProperties(cr1.getExtendProperties(), 0));
        cp2.setVariables(List.of(new InstanceData("danxuankuang_ytgyk", 1)));
        CommitTaskResult cr2 = runtimeProcessor.commit(cp2);

        CommitTaskParam cp3 = new CommitTaskParam();
        cp3.setFlowInstanceId(cr2.getFlowInstanceId());
        cp3.setTaskInstanceId(cr2.getActiveTaskInstance().getNodeInstanceId());
        cp3.setExtendProperties(copyExtendProperties(cr2.getExtendProperties(), 0));
        cp3.setVariables(List.of(new InstanceData("danxuankuang_ytgyk", 2)));
        runtimeProcessor.commit(cp3); // WAITING at PG_3a1nn9f

        CommitTaskParam cp4 = new CommitTaskParam();
        cp4.setFlowInstanceId(cr1.getFlowInstanceId());
        cp4.setTaskInstanceId(cr1.getNodeExecuteResults().get(1).getActiveTaskInstance().getNodeInstanceId());
        cp4.setExtendProperties(copyExtendProperties(cr1.getExtendProperties(), 1));
        cp4.setVariables(List.of(new InstanceData("danxuankuang_ytgyk", 3)));
        CommitTaskResult cr4 = runtimeProcessor.commit(cp4);

        CommitTaskParam cp5 = new CommitTaskParam();
        cp5.setFlowInstanceId(cr4.getFlowInstanceId());
        cp5.setTaskInstanceId(cr4.getActiveTaskInstance().getNodeInstanceId());
        cp5.setExtendProperties(copyExtendProperties(cr4.getExtendProperties(), 0));
        cp5.setVariables(List.of(new InstanceData("danxuankuang_ytgyk", 4)));
        runtimeProcessor.commit(cp5); // WAITING at PG_10lo44j

        CommitTaskParam cp6 = new CommitTaskParam();
        cp6.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        cp6.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getNodeInstanceId());
        cp6.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 1));
        cp6.setVariables(List.of(new InstanceData("danxuankuang_ytgyk", 5)));
        CommitTaskResult cr6 = runtimeProcessor.commit(cp6);
        Assert.assertEquals(ErrorEnum.SUCCESS.getErrNo(), cr6.getErrCode());

        // Verify getHistoryUserTaskList
        String flowInstanceId = startProcessResult.getFlowInstanceId();
        NodeInstanceListResult result = runtimeProcessor.getHistoryUserTaskList(flowInstanceId, false);

        Assert.assertEquals(ErrorEnum.SUCCESS.getErrNo(), result.getErrCode());
        List<NodeInstance> userTasks = result.getNodeInstanceList();
        LOGGER.info("testParallelGatewayGetHistoryUserTaskList.||userTasks={}", userTasks);

        // 6 user tasks: UserTask_0iv55sh, UserTask_0m7qih6, UserTask_2npcbgp, UserTask_1ram9jm, UserTask_01tuns9, UserTask_32ed01b
        Assert.assertEquals("Expected 6 completed user tasks in the parallel flow", 6, userTasks.size());

        List<String> taskKeys = userTasks.stream()
                .map(NodeInstance::getModelKey)
                .collect(Collectors.toList());
        Assert.assertTrue(taskKeys.contains("UserTask_0iv55sh"));
        Assert.assertTrue(taskKeys.contains("UserTask_0m7qih6"));
        Assert.assertTrue(taskKeys.contains("UserTask_2npcbgp"));
        Assert.assertTrue(taskKeys.contains("UserTask_1ram9jm"));
        Assert.assertTrue(taskKeys.contains("UserTask_01tuns9"));
        Assert.assertTrue(taskKeys.contains("UserTask_32ed01b"));
        // DESC order: UserTask_32ed01b inserted last (highest id) should appear first
        Assert.assertEquals("UserTask_32ed01b", userTasks.get(0).getModelKey());
    }

    /**
     * Verifies getHistoryElementList for a completed inclusive gateway flow.
     * The inclusive gateway fork selects only ONE branch (a=11 satisfies a>=10),
     * so the inner join (InclusiveGateway_3a1nn9f) receives only ONE branch → no additionalSourceNodeKeys.
     * The outer join (ParallelGateway_10lo44j) still receives TWO branches (InclusiveGateway_3a1nn9f + UserTask_0m7qih6)
     * and MUST have both edges present after the fix.
     *
     * Flow:
     *  StartEvent_2s70149 --> ParallelGateway_38ad233 --> UserTask_0iv55sh --> InclusiveGateway_1djgrgp --> UserTask_2npcbgp --> UserTask_01tuns9 --> InclusiveGateway_3a1nn9f --> ParallelGateway_10lo44j --> EndEvent_2c8j53d
     *                                                 |                                                                                                                        |
     *                                                  -------------------------------------------------> UserTask_0m7qih6 ----------------------------------------------------
     */
    @Test
    public void testInclusiveGatewayGetHistoryElementList() throws Exception {
        // Run full inclusive flow to completion (flag="history_inclusive")
        StartProcessResult startProcessResult = startInclusiveProcess("history_inclusive");
        Assert.assertEquals(ErrorEnum.COMMIT_SUSPEND.getErrNo(), startProcessResult.getErrCode());

        // UserTask_0iv55sh -> InclusiveGateway_1djgrgp -> UserTask_2npcbgp (only branch, a=11 >= 10)
        CommitTaskParam cp1 = new CommitTaskParam();
        cp1.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        cp1.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        cp1.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 0));
        cp1.setVariables(List.of(new InstanceData("danxuankuang_ytgyk", 0)));
        CommitTaskResult cr1 = runtimeProcessor.commit(cp1);
        Assert.assertEquals(ErrorEnum.COMMIT_SUSPEND.getErrNo(), cr1.getErrCode());

        // UserTask_2npcbgp -> UserTask_01tuns9
        CommitTaskParam cp2 = new CommitTaskParam();
        cp2.setFlowInstanceId(cr1.getFlowInstanceId());
        cp2.setTaskInstanceId(cr1.getNodeExecuteResults().get(0).getActiveTaskInstance().getNodeInstanceId());
        cp2.setExtendProperties(copyExtendProperties(cr1.getExtendProperties(), 0));
        cp2.setVariables(List.of(new InstanceData("danxuankuang_ytgyk", 1)));
        CommitTaskResult cr2 = runtimeProcessor.commit(cp2);
        Assert.assertEquals(ErrorEnum.COMMIT_SUSPEND.getErrNo(), cr2.getErrCode());

        // UserTask_01tuns9 -> InclusiveGateway_3a1nn9f (single branch, immediately COMPLETED) -> ParallelGateway_10lo44j (WAITING)
        CommitTaskParam cp3 = new CommitTaskParam();
        cp3.setFlowInstanceId(cr2.getFlowInstanceId());
        cp3.setTaskInstanceId(cr2.getActiveTaskInstance().getNodeInstanceId());
        cp3.setExtendProperties(copyExtendProperties(cr2.getExtendProperties(), 0));
        cp3.setVariables(List.of(new InstanceData("danxuankuang_ytgyk", 2)));
        CommitTaskResult cr3 = runtimeProcessor.commit(cp3);
        Assert.assertEquals(ParallelErrorEnum.WAITING_SUSPEND.getErrNo(), cr3.getErrCode());

        // UserTask_0m7qih6 -> ParallelGateway_10lo44j (second arrival, COMPLETED) -> EndEvent -> SUCCESS
        CommitTaskParam cp4 = new CommitTaskParam();
        cp4.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        cp4.setTaskInstanceId(startProcessResult.getNodeExecuteResults().get(1).getActiveTaskInstance().getNodeInstanceId());
        cp4.setExtendProperties(copyExtendProperties(startProcessResult.getExtendProperties(), 1));
        cp4.setVariables(List.of(new InstanceData("danxuankuang_ytgyk", 5)));
        CommitTaskResult cr4 = runtimeProcessor.commit(cp4);
        Assert.assertEquals(ErrorEnum.SUCCESS.getErrNo(), cr4.getErrCode());

        // Verify getHistoryElementList
        String flowInstanceId = startProcessResult.getFlowInstanceId();
        ElementInstanceListResult result = runtimeProcessor.getHistoryElementList(flowInstanceId, false);

        Assert.assertEquals(ErrorEnum.SUCCESS.getErrNo(), result.getErrCode());
        List<ElementInstance> elements = result.getElementInstanceList();

        List<String> elementKeys = elements.stream()
                .map(ElementInstance::getModelKey)
                .collect(Collectors.toList());
        LOGGER.info("testInclusiveGatewayGetHistoryElementList.||elementKeys={}", elementKeys);

        // ---- Inner join (InclusiveGateway_3a1nn9f): single branch → only ONE incoming edge ----
        // SequenceFlow_1h65e8t: UserTask_01tuns9 → InclusiveGateway_3a1nn9f
        Assert.assertTrue("Missing SequenceFlow_1h65e8t (UT_01tuns9 → IG_3a1nn9f)",
                elementKeys.contains("SequenceFlow_1h65e8t"));
        // SequenceFlow_25kdv36 (UserTask_32ed01b → IG_3a1nn9f) should NOT be present since that branch wasn't activated
        Assert.assertFalse("SequenceFlow_25kdv36 should not appear (branch not activated)",
                elementKeys.contains("SequenceFlow_25kdv36"));

        // ---- Outer join (ParallelGateway_10lo44j): TWO branches → both edges present (fix) ----
        // SequenceFlow_3jkd63g: InclusiveGateway_3a1nn9f → ParallelGateway_10lo44j (first branch)
        Assert.assertTrue("Missing SequenceFlow_3jkd63g (IG_3a1nn9f → PG_10lo44j)",
                elementKeys.contains("SequenceFlow_3jkd63g"));
        // SequenceFlow_3bgdrp0: UserTask_0m7qih6 → ParallelGateway_10lo44j (second branch, from additionalSourceNodeKeys)
        Assert.assertTrue("Missing SequenceFlow_3bgdrp0 (UT_0m7qih6 → PG_10lo44j) - getHistory fix regression",
                elementKeys.contains("SequenceFlow_3bgdrp0"));

        // ---- All expected nodes are present ----
        Assert.assertTrue(elementKeys.contains("StartEvent_2s70149"));
        Assert.assertTrue(elementKeys.contains("ParallelGateway_38ad233"));
        Assert.assertTrue(elementKeys.contains("UserTask_0iv55sh"));
        Assert.assertTrue(elementKeys.contains("UserTask_0m7qih6"));
        Assert.assertTrue(elementKeys.contains("InclusiveGateway_1djgrgp"));
        Assert.assertTrue(elementKeys.contains("UserTask_2npcbgp"));
        Assert.assertTrue(elementKeys.contains("UserTask_01tuns9"));
        Assert.assertTrue(elementKeys.contains("InclusiveGateway_3a1nn9f"));
        Assert.assertTrue(elementKeys.contains("ParallelGateway_10lo44j"));
        Assert.assertTrue(elementKeys.contains("EndEvent_2c8j53d"));

        // UserTask_1ram9jm and UserTask_32ed01b were NOT activated; must not appear
        Assert.assertFalse("UserTask_1ram9jm should not appear (branch not activated)",
                elementKeys.contains("UserTask_1ram9jm"));
        Assert.assertFalse("UserTask_32ed01b should not appear (branch not activated)",
                elementKeys.contains("UserTask_32ed01b"));

        // ---- Verify exact element count: 22 ----
        // Breakdown: 10 node instances each contribute:
        //   StartEvent                       : 0 edges + 1 node =  1
        //   PG_38ad233, UT_0iv55sh, UT_0m7qih6, IG_1djgrgp,
        //   UT_2npcbgp, UT_01tuns9, EndEvent (7 nodes)         : 1 edge + 1 node = 14
        //   IG_3a1nn9f (single-branch join)  : 1 edge + 1 node =  2  (no additionalSourceNodeKeys)
        //   PG_10lo44j (2-branch join)       : 2 edges + 1 node =  3  ← additionalSourceNodeKeys fix
        //   Total = 1 + 14 + 2 + 3 = 20 ... nope wait, 7 nodes = 7*2=14, 1+14+2+3=20
        // Actually: StartEvent(1) + PG_38ad233(2) + UT_0iv55sh(2) + UT_0m7qih6(2) + IG_1djgrgp(2)
        //         + UT_2npcbgp(2) + UT_01tuns9(2) + IG_3a1nn9f(2) + PG_10lo44j(3) + EndEvent(2) = 22
        Assert.assertEquals("Element list size must be exactly 22 for completed inclusive gateway flow", 22, elements.size());
    }

    private Map<String, Object> copyExtendProperties(Map<String, Object> extendProperties, int i) {
        Map<String, Object> copyExtendProperties = new HashMap<>();
        List<ParallelRuntimeContext> parallelRuntimeContextList = (List<ParallelRuntimeContext>) extendProperties.get("parallelRuntimeContextList");
        List<ParallelRuntimeContext> copyParallelRuntimeContextList = new ArrayList<>();
        if (parallelRuntimeContextList != null && parallelRuntimeContextList.size() > i) {
            copyParallelRuntimeContextList.add(parallelRuntimeContextList.get(i));
            copyExtendProperties.put("executeId", parallelRuntimeContextList.get(i).getExecuteId());
        }
        copyExtendProperties.put("parallelRuntimeContextList", copyParallelRuntimeContextList);
        return copyExtendProperties;
    }
}
