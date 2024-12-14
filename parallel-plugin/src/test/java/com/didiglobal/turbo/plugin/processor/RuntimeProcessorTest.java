package com.didiglobal.turbo.plugin.processor;

import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.param.CommitTaskParam;
import com.didiglobal.turbo.engine.param.RollbackTaskParam;
import com.didiglobal.turbo.engine.param.StartProcessParam;
import com.didiglobal.turbo.engine.processor.RuntimeProcessor;
import com.didiglobal.turbo.engine.result.CommitTaskResult;
import com.didiglobal.turbo.engine.result.RollbackTaskResult;
import com.didiglobal.turbo.engine.result.StartProcessResult;
import com.didiglobal.turbo.engine.result.TerminateResult;
import com.didiglobal.turbo.mybatis.dao.mapper.FlowDeploymentMapper;
import com.didiglobal.turbo.mybatis.entity.FlowDeploymentEntity;
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
                flowDeploymentMapper.insert(FlowDeploymentEntity.of(flowDeploymentPO));
            }
        } else {
            flowDeploymentMapper.insert(FlowDeploymentEntity.of(flowDeploymentPO));
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
                flowDeploymentMapper.insert(FlowDeploymentEntity.of(flowDeploymentPO));
            }
        } else {
            flowDeploymentMapper.insert(FlowDeploymentEntity.of(flowDeploymentPO));
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
                flowDeploymentMapper.insert(FlowDeploymentEntity.of(flowDeploymentPO));
            }
        } else {
            flowDeploymentMapper.insert(FlowDeploymentEntity.of(flowDeploymentPO));
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

    private Map<String, Object> copyExtendProperties(Map<String, Object> extendProperties, int i) {
        Map<String, Object> copyExtendProperties = new HashMap<>();
        List<ParallelRuntimeContext> parallelRuntimeContextList = (List<ParallelRuntimeContext>) extendProperties.get("parallelRuntimeContextList");
        List<ParallelRuntimeContext> copyParallelRuntimeContextList = new ArrayList<>();
        copyParallelRuntimeContextList.add(parallelRuntimeContextList.get(i));
        copyExtendProperties.put("parallelRuntimeContextList", copyParallelRuntimeContextList);
        copyExtendProperties.put("executeId", parallelRuntimeContextList.get(i).getExecuteId());
        return copyExtendProperties;
    }
}
