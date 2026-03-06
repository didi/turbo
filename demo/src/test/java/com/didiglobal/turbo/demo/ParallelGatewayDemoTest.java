package com.didiglobal.turbo.demo;

import com.didiglobal.turbo.demo.util.Constant;
import com.didiglobal.turbo.demo.util.EntityBuilder;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.engine.ProcessEngine;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.param.CommitTaskParam;
import com.didiglobal.turbo.engine.param.CreateFlowParam;
import com.didiglobal.turbo.engine.param.DeployFlowParam;
import com.didiglobal.turbo.engine.param.StartProcessParam;
import com.didiglobal.turbo.engine.param.UpdateFlowParam;
import com.didiglobal.turbo.engine.result.CommitTaskResult;
import com.didiglobal.turbo.engine.result.CreateFlowResult;
import com.didiglobal.turbo.engine.result.DeployFlowResult;
import com.didiglobal.turbo.engine.result.RuntimeResult;
import com.didiglobal.turbo.engine.result.StartProcessResult;
import com.didiglobal.turbo.plugin.common.ParallelErrorEnum;
import com.didiglobal.turbo.plugin.common.ParallelRuntimeContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 并行网关与包容网关 Demo 验证测试
 *
 * 验证 DemoApplication 在不修改 parallel-plugin 模块的前提下
 * （通过扩展 scanBasePackages 扫描 com.didiglobal.turbo.plugin），
 * 能够正确加载并运行并行网关与包容网关场景。
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class ParallelGatewayDemoTest {

    @Resource
    private ProcessEngine processEngine;

    /**
     * 验证并行网关（2个分支）：
     * StartEvent → Fork → Finance + Manager → Join → EndEvent
     *
     * 期望行为：
     * - startProcess  → COMMIT_SUSPEND(1002)，返回2个活跃分支
     * - commitTask(Finance)  → WAITING_SUSPEND(1601)，Join网关等待Manager分支
     * - commitTask(Manager)  → SUCCESS(0)，所有分支完成，流程结束
     */
    @Test
    public void testParallelGateway() throws Exception {
        DeployFlowResult deployResult = createAndDeployFlow(
                "pgTest", "并行网关测试", EntityBuilder.buildParallelGatewayFlowModelStr());

        // 启动流程
        StartProcessResult startResult = startProcess(deployResult.getFlowDeployId(),
                new InstanceData("amount", 5000));
        // 并行网关fork：返回 COMMIT_SUSPEND，有2个活跃分支
        Assert.assertEquals(ErrorEnum.COMMIT_SUSPEND.getErrNo(), startResult.getErrCode());
        List<RuntimeResult.NodeExecuteResult> branches = startResult.getNodeExecuteResults();
        Assert.assertEquals("并行网关应激活2个分支", 2, branches.size());

        // 提交分支0（Finance）：Join网关等待 → WAITING_SUSPEND
        CommitTaskResult r0 = commitBranch(startResult.getFlowInstanceId(),
                branches.get(0).getActiveTaskInstance().getNodeInstanceId(),
                startResult.getExtendProperties(), 0,
                new InstanceData("financeApproval", "通过"));
        Assert.assertEquals("Finance提交后Join等待中，期望WAITING_SUSPEND",
                ParallelErrorEnum.WAITING_SUSPEND.getErrNo(), r0.getErrCode());

        // 提交分支1（Manager）：最后一个分支，Join完成 → SUCCESS
        CommitTaskResult r1 = commitBranch(startResult.getFlowInstanceId(),
                branches.get(1).getActiveTaskInstance().getNodeInstanceId(),
                startResult.getExtendProperties(), 1,
                new InstanceData("managerApproval", "通过"));
        Assert.assertEquals("Manager提交后所有分支完成，期望SUCCESS",
                ErrorEnum.SUCCESS.getErrNo(), r1.getErrCode());
    }

    /**
     * 验证包容网关：小额采购（金额=3000）
     * 仅触发部门主管审批分支（1个分支）
     */
    @Test
    public void testInclusiveGatewaySmall() throws Exception {
        StartProcessResult startResult = startInclusiveProcess(3000, "igSmall");
        List<RuntimeResult.NodeExecuteResult> branches = startResult.getNodeExecuteResults();
        Assert.assertEquals("小额采购应仅激活1个分支（部门主管）", 1, branches.size());
        Assert.assertEquals("UserTask_Dept", branches.get(0).getActiveTaskInstance().getModelKey());

        // 仅1个分支：提交后Join立即完成 → SUCCESS
        CommitTaskResult r = commitBranch(startResult.getFlowInstanceId(),
                branches.get(0).getActiveTaskInstance().getNodeInstanceId(),
                startResult.getExtendProperties(), 0,
                new InstanceData("approval", "通过"));
        Assert.assertEquals("小额采购唯一分支完成后期望SUCCESS",
                ErrorEnum.SUCCESS.getErrNo(), r.getErrCode());
    }

    /**
     * 验证包容网关：大额采购（金额=15000）
     * 仅触发CFO审批分支（1个分支）
     */
    @Test
    public void testInclusiveGatewayLarge() throws Exception {
        StartProcessResult startResult = startInclusiveProcess(15000, "igLarge");
        List<RuntimeResult.NodeExecuteResult> branches = startResult.getNodeExecuteResults();
        Assert.assertEquals("大额采购应仅激活1个分支（CFO）", 1, branches.size());
        Assert.assertEquals("UserTask_CFO", branches.get(0).getActiveTaskInstance().getModelKey());

        // 仅1个分支：提交后Join立即完成 → SUCCESS
        CommitTaskResult r = commitBranch(startResult.getFlowInstanceId(),
                branches.get(0).getActiveTaskInstance().getNodeInstanceId(),
                startResult.getExtendProperties(), 0,
                new InstanceData("approval", "通过"));
        Assert.assertEquals("大额采购唯一分支完成后期望SUCCESS",
                ErrorEnum.SUCCESS.getErrNo(), r.getErrCode());
    }

    /**
     * 验证包容网关：中等金额采购（金额=7000）
     * 同时触发部门主管审批和CFO审批（2个分支）
     */
    @Test
    public void testInclusiveGatewayMedium() throws Exception {
        StartProcessResult startResult = startInclusiveProcess(7000, "igMedium");
        List<RuntimeResult.NodeExecuteResult> branches = startResult.getNodeExecuteResults();
        Assert.assertEquals("中等金额采购应激活2个分支（部门主管+CFO）", 2, branches.size());

        // 提交第1个分支 → Join等待 → WAITING_SUSPEND
        CommitTaskResult r0 = commitBranch(startResult.getFlowInstanceId(),
                branches.get(0).getActiveTaskInstance().getNodeInstanceId(),
                startResult.getExtendProperties(), 0,
                new InstanceData("approval", "通过"));
        Assert.assertEquals("中等金额第一分支完成后Join等待，期望WAITING_SUSPEND",
                ParallelErrorEnum.WAITING_SUSPEND.getErrNo(), r0.getErrCode());

        // 提交第2个分支（最后一个）→ Join完成 → SUCCESS
        CommitTaskResult r1 = commitBranch(startResult.getFlowInstanceId(),
                branches.get(1).getActiveTaskInstance().getNodeInstanceId(),
                startResult.getExtendProperties(), 1,
                new InstanceData("approval", "通过"));
        Assert.assertEquals("中等金额最后分支完成后期望SUCCESS",
                ErrorEnum.SUCCESS.getErrNo(), r1.getErrCode());
    }

    // ==================== 辅助方法 ====================

    private DeployFlowResult createAndDeployFlow(String flowKey, String flowName, String flowModelStr) {
        CreateFlowParam createParam = new CreateFlowParam(Constant.tenant, Constant.caller);
        createParam.setFlowKey(flowKey);
        createParam.setFlowName(flowName);
        createParam.setOperator(Constant.operator);
        CreateFlowResult createResult = processEngine.createFlow(createParam);
        Assert.assertNotNull("flowModuleId不应为空", createResult.getFlowModuleId());

        UpdateFlowParam updateParam = new UpdateFlowParam(Constant.tenant, Constant.caller);
        updateParam.setFlowModuleId(createResult.getFlowModuleId());
        updateParam.setFlowModel(flowModelStr);
        processEngine.updateFlow(updateParam);

        DeployFlowParam deployParam = new DeployFlowParam(Constant.tenant, Constant.caller);
        deployParam.setFlowModuleId(createResult.getFlowModuleId());
        DeployFlowResult deployResult = processEngine.deployFlow(deployParam);
        Assert.assertNotNull("flowDeployId不应为空", deployResult.getFlowDeployId());
        return deployResult;
    }

    private StartProcessResult startProcess(String flowDeployId, InstanceData... vars) {
        StartProcessParam startParam = new StartProcessParam();
        startParam.setFlowDeployId(flowDeployId);
        List<InstanceData> variables = new ArrayList<>();
        for (InstanceData v : vars) {
            variables.add(v);
        }
        startParam.setVariables(variables);
        return processEngine.startProcess(startParam);
    }

    private StartProcessResult startInclusiveProcess(int purchaseAmount, String flowKeySuffix) {
        // flowKey 长度限制 32 字符，igSmall/igLarge/igMedium 均在限制内
        DeployFlowResult deployResult = createAndDeployFlow(
                flowKeySuffix,
                "包容网关测试_" + flowKeySuffix,
                EntityBuilder.buildInclusiveGatewayFlowModelStr());
        return startProcess(deployResult.getFlowDeployId(),
                new InstanceData("purchaseAmount", purchaseAmount));
    }

    private CommitTaskResult commitBranch(String flowInstanceId, String taskInstanceId,
                                          Map<String, Object> extendProperties, int branchIndex,
                                          InstanceData... vars) {
        CommitTaskParam commit = new CommitTaskParam();
        commit.setFlowInstanceId(flowInstanceId);
        commit.setTaskInstanceId(taskInstanceId);
        List<InstanceData> variables = new ArrayList<>();
        for (InstanceData v : vars) {
            variables.add(v);
        }
        commit.setVariables(variables);
        commit.setExtendProperties(extractBranchContext(extendProperties, branchIndex));
        return processEngine.commitTask(commit);
    }

    private Map<String, Object> extractBranchContext(Map<String, Object> extendProperties, int branchIndex) {
        Map<String, Object> result = new HashMap<>();
        if (extendProperties == null) {
            return result;
        }
        @SuppressWarnings("unchecked")
        List<ParallelRuntimeContext> contexts =
                (List<ParallelRuntimeContext>) extendProperties.get("parallelRuntimeContextList");
        List<ParallelRuntimeContext> singleContext = new ArrayList<>();
        if (contexts != null && contexts.size() > branchIndex) {
            singleContext.add(contexts.get(branchIndex));
            result.put("executeId", contexts.get(branchIndex).getExecuteId());
        }
        result.put("parallelRuntimeContextList", singleContext);
        return result;
    }
}
