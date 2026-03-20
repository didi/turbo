package com.didiglobal.turbo.demo.runner;

import com.didiglobal.turbo.demo.util.Constant;
import com.didiglobal.turbo.demo.util.EntityBuilder;
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
import com.didiglobal.turbo.plugin.common.ParallelRuntimeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 并行网关与包容网关 Demo
 *
 * 场景一：并行网关 - 费用报销审批流程
 *   员工提交报销申请后，财务部审核与部门主管审批同时发起（并行执行），
 *   两者均完成后报销流程结束。
 *
 *   流程结构：
 *                                  --> UserTask_Finance（财务部审核）-->
 *   StartEvent --> ParallelGW_fork                                     ParallelGW_join --> EndEvent
 *                                  --> UserTask_Manager（部门主管审批）-->
 *
 * 场景二：包容网关 - 采购审批流程
 *   根据采购金额动态激活审批分支：
 *   - 小额采购（金额 < 5000）：仅触发部门主管审批
 *   - 大额采购（金额 >= 10000）：仅触发CFO审批
 *   - 中等金额（5000 <= 金额 < 10000）：同时触发部门主管审批和CFO审批
 *
 *   流程结构：
 *                                  --> UserTask_Dept（部门主管审批，条件: purchaseAmount < 10000）-->
 *   StartEvent --> InclusiveGW_fork                                                                  InclusiveGW_join --> EndEvent
 *                                  --> UserTask_CFO（CFO审批，条件: purchaseAmount >= 5000）      -->
 */
@Order(2)
@Component
public class ParallelGatewayDemoRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelGatewayDemoRunner.class);

    @Resource
    private ProcessEngine processEngine;

    @Override
    public void run(String... args) {
        LOGGER.info("========== Turbo 并行网关与包容网关 Demo 启动 ==========");

        // 场景一：并行网关 - 费用报销审批
        runParallelGatewayDemo();

        // 场景二：包容网关 - 采购审批（小额）
        runInclusiveGatewayDemo(3000, "小额采购（仅部门主管审批）");

        // 场景三：包容网关 - 采购审批（大额）
        runInclusiveGatewayDemo(15000, "大额采购（仅CFO审批）");

        // 场景四：包容网关 - 采购审批（中等金额）
        runInclusiveGatewayDemo(7000, "中等金额采购（部门主管+CFO双重审批）");

        LOGGER.info("========== Turbo 并行网关与包容网关 Demo 结束 ==========");
    }

    // ==================== 场景一：并行网关 - 费用报销审批 ====================

    /**
     * 并行网关Demo：费用报销审批流程
     * 财务部审核和部门主管审批同时进行，两者均完成后流程结束。
     */
    private void runParallelGatewayDemo() {
        LOGGER.info("---------- 场景一：并行网关 - 费用报销审批流程 ----------");

        // 1. 创建流程定义
        CreateFlowParam createParam = new CreateFlowParam(Constant.tenant, Constant.caller);
        createParam.setFlowKey("parallelGatewayDemo");
        createParam.setFlowName("费用报销审批流程（并行网关）");
        createParam.setRemark("财务部审核与部门主管审批并行执行，全部通过后完成报销");
        createParam.setOperator(Constant.operator);
        CreateFlowResult createResult = processEngine.createFlow(createParam);
        LOGGER.info("【定义流程】创建流程定义成功，flowModuleId={}", createResult.getFlowModuleId());

        // 2. 更新流程模型
        UpdateFlowParam updateParam = new UpdateFlowParam(Constant.tenant, Constant.caller);
        updateParam.setFlowModuleId(createResult.getFlowModuleId());
        updateParam.setFlowModel(EntityBuilder.buildParallelGatewayFlowModelStr());
        processEngine.updateFlow(updateParam);
        LOGGER.info("【定义流程】更新流程模型成功（并行分支：财务部审核 + 部门主管审批）");

        // 3. 部署流程
        DeployFlowParam deployParam = new DeployFlowParam(Constant.tenant, Constant.caller);
        deployParam.setFlowModuleId(createResult.getFlowModuleId());
        DeployFlowResult deployResult = processEngine.deployFlow(deployParam);
        LOGGER.info("【定义流程】流程部署成功，flowDeployId={}", deployResult.getFlowDeployId());

        // 4. 启动流程实例
        LOGGER.info("【运行流程】员工提交费用报销申请，启动审批流程...");
        StartProcessParam startParam = new StartProcessParam();
        startParam.setFlowDeployId(deployResult.getFlowDeployId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("applicant", "张三"));
        variables.add(new InstanceData("amount", 5000));
        startParam.setVariables(variables);
        StartProcessResult startResult = processEngine.startProcess(startParam);

        List<RuntimeResult.NodeExecuteResult> branches = startResult.getNodeExecuteResults();
        LOGGER.info("【运行流程】并行网关触发，同时激活 {} 个审批分支：", branches.size());
        for (int i = 0; i < branches.size(); i++) {
            LOGGER.info("  分支 {}: 任务节点=[{}]，任务实例ID=[{}]",
                    i + 1,
                    branches.get(i).getActiveTaskInstance().getModelKey(),
                    branches.get(i).getActiveTaskInstance().getNodeInstanceId());
        }

        // 5. 提交财务部审核（分支0）
        LOGGER.info("【运行流程】财务部完成审核，提交审批结果...");
        CommitTaskParam financeCommit = new CommitTaskParam();
        financeCommit.setFlowInstanceId(startResult.getFlowInstanceId());
        financeCommit.setTaskInstanceId(branches.get(0).getActiveTaskInstance().getNodeInstanceId());
        financeCommit.setVariables(Collections.singletonList(new InstanceData("financeApproval", "通过")));
        financeCommit.setExtendProperties(extractBranchContext(startResult.getExtendProperties(), 0));
        CommitTaskResult financeResult = processEngine.commitTask(financeCommit);
        LOGGER.info("【运行流程】财务部审核已提交，等待其他分支完成，errCode={}", financeResult.getErrCode());

        // 6. 提交部门主管审批（分支1）
        LOGGER.info("【运行流程】部门主管完成审批，提交审批结果...");
        CommitTaskParam managerCommit = new CommitTaskParam();
        managerCommit.setFlowInstanceId(startResult.getFlowInstanceId());
        managerCommit.setTaskInstanceId(branches.get(1).getActiveTaskInstance().getNodeInstanceId());
        managerCommit.setVariables(Collections.singletonList(new InstanceData("managerApproval", "通过")));
        managerCommit.setExtendProperties(extractBranchContext(startResult.getExtendProperties(), 1));
        CommitTaskResult managerResult = processEngine.commitTask(managerCommit);
        LOGGER.info("【运行流程】部门主管审批已提交，所有分支均已完成，errCode={}", managerResult.getErrCode());

        LOGGER.info("【运行结果】费用报销审批流程完成！财务部审核：通过，部门主管审批：通过。");
        LOGGER.info("---------- 场景一结束 ----------");
    }

    // ==================== 场景二/三/四：包容网关 - 采购审批 ====================

    /**
     * 包容网关Demo：采购审批流程
     * 根据采购金额（purchaseAmount）动态决定触发哪些审批分支：
     *   - purchaseAmount < 5000：仅触发部门主管审批
     *   - purchaseAmount >= 10000：仅触发CFO审批
     *   - 5000 <= purchaseAmount < 10000：同时触发部门主管审批和CFO审批
     *
     * @param purchaseAmount 采购金额
     * @param scenarioDesc   场景描述
     */
    private void runInclusiveGatewayDemo(int purchaseAmount, String scenarioDesc) {
        LOGGER.info("---------- 场景：包容网关 - {} ----------", scenarioDesc);

        // 1. 创建流程定义（每次使用不同的flowKey避免冲突）
        CreateFlowParam createParam = new CreateFlowParam(Constant.tenant, Constant.caller);
        createParam.setFlowKey("inclusiveGatewayDemo_" + purchaseAmount);
        createParam.setFlowName("采购审批流程（包容网关）- " + scenarioDesc);
        createParam.setRemark("根据采购金额动态激活审批分支");
        createParam.setOperator(Constant.operator);
        CreateFlowResult createResult = processEngine.createFlow(createParam);
        LOGGER.info("【定义流程】创建流程定义成功，flowModuleId={}", createResult.getFlowModuleId());

        // 2. 更新流程模型
        UpdateFlowParam updateParam = new UpdateFlowParam(Constant.tenant, Constant.caller);
        updateParam.setFlowModuleId(createResult.getFlowModuleId());
        updateParam.setFlowModel(EntityBuilder.buildInclusiveGatewayFlowModelStr());
        processEngine.updateFlow(updateParam);
        LOGGER.info("【定义流程】更新流程模型成功（包容分支：部门主管审批[金额<10000] + CFO审批[金额>=5000]）");

        // 3. 部署流程
        DeployFlowParam deployParam = new DeployFlowParam(Constant.tenant, Constant.caller);
        deployParam.setFlowModuleId(createResult.getFlowModuleId());
        DeployFlowResult deployResult = processEngine.deployFlow(deployParam);
        LOGGER.info("【定义流程】流程部署成功，flowDeployId={}", deployResult.getFlowDeployId());

        // 4. 启动流程实例（传入采购金额）
        LOGGER.info("【运行流程】发起采购审批申请，采购金额={}元，{}...", purchaseAmount, scenarioDesc);
        StartProcessParam startParam = new StartProcessParam();
        startParam.setFlowDeployId(deployResult.getFlowDeployId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("applicant", "李四"));
        variables.add(new InstanceData("purchaseAmount", purchaseAmount));
        startParam.setVariables(variables);
        StartProcessResult startResult = processEngine.startProcess(startParam);

        List<RuntimeResult.NodeExecuteResult> branches = startResult.getNodeExecuteResults();
        LOGGER.info("【运行流程】包容网关根据条件激活了 {} 个审批分支：", branches.size());
        for (int i = 0; i < branches.size(); i++) {
            LOGGER.info("  分支 {}: 任务节点=[{}]", i + 1, branches.get(i).getActiveTaskInstance().getModelKey());
        }

        // 5. 依次提交所有激活的分支
        for (int i = 0; i < branches.size(); i++) {
            String taskKey = branches.get(i).getActiveTaskInstance().getModelKey();
            String approver;
            if ("UserTask_Dept".equals(taskKey)) {
                approver = "部门主管";
            } else if ("UserTask_CFO".equals(taskKey)) {
                approver = "CFO";
            } else {
                approver = taskKey;
            }
            LOGGER.info("【运行流程】{} 完成审批，提交结果（分支 {}）...", approver, i + 1);

            CommitTaskParam commitParam = new CommitTaskParam();
            commitParam.setFlowInstanceId(startResult.getFlowInstanceId());
            commitParam.setTaskInstanceId(branches.get(i).getActiveTaskInstance().getNodeInstanceId());
            commitParam.setVariables(Collections.singletonList(new InstanceData(approver + "Approval", "通过")));
            commitParam.setExtendProperties(extractBranchContext(startResult.getExtendProperties(), i));
            CommitTaskResult commitResult = processEngine.commitTask(commitParam);
            LOGGER.info("【运行流程】{} 审批提交完成，errCode={}", approver, commitResult.getErrCode());
        }

        LOGGER.info("【运行结果】采购审批流程完成！采购金额={}元，激活审批分支数={}。", purchaseAmount, branches.size());
        LOGGER.info("---------- 场景结束 ----------");
    }

    /**
     * 从 extendProperties 中提取指定分支的上下文信息，
     * 用于提交并行/包容网关的某个分支任务时传递分支执行标识。
     *
     * @param extendProperties 流程扩展属性（来自 StartProcessResult 或 CommitTaskResult）
     * @param branchIndex      分支索引（从0开始）
     * @return 该分支对应的 extendProperties
     */
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
