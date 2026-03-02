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
import com.didiglobal.turbo.engine.result.UpdateFlowResult;
import com.didiglobal.turbo.plugin.common.ParallelRuntimeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parallel Gateway Demo
 *
 * Flow:
 *                         --> UserTask_A -->
 * StartEvent --> PGW_fork                   PGW_join --> EndEvent
 *                         --> UserTask_B -->
 *
 * After fork, Task A and Task B are both triggered in parallel.
 * The join gateway waits for both tasks to complete before proceeding.
 */
@Order(2)
@Component
public class ParallelGatewayDemoRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelGatewayDemoRunner.class);

    @Resource
    private ProcessEngine processEngine;

    private CreateFlowResult createFlowResult;
    private DeployFlowResult deployFlowResult;

    @Override
    public void run(String... args) {
        LOGGER.info("Turbo Parallel Gateway Demo run:");

        LOGGER.info("Turbo definition:");
        createFlow();
        updateFlow();
        deployFlow();

        LOGGER.info("Turbo runtime:");
        startProcessToEnd();
    }

    private void createFlow() {
        CreateFlowParam createFlowParam = new CreateFlowParam(Constant.tenant, Constant.caller);
        createFlowParam.setFlowKey("parallelGatewayDemo");
        createFlowParam.setFlowName("Parallel Gateway Demo");
        createFlowParam.setRemark("parallel gateway demo");
        createFlowParam.setOperator(Constant.operator);
        createFlowResult = processEngine.createFlow(createFlowParam);
        LOGGER.info("createFlow.||createFlowResult={}", createFlowResult);
    }

    private void updateFlow() {
        UpdateFlowParam updateFlowParam = new UpdateFlowParam(Constant.tenant, Constant.caller);
        updateFlowParam.setFlowModel(EntityBuilder.buildParallelGatewayFlowModelStr());
        updateFlowParam.setFlowModuleId(createFlowResult.getFlowModuleId());
        UpdateFlowResult updateFlowResult = processEngine.updateFlow(updateFlowParam);
        LOGGER.info("updateFlow.||updateFlowResult={}", updateFlowResult);
    }

    private void deployFlow() {
        DeployFlowParam param = new DeployFlowParam(Constant.tenant, Constant.caller);
        param.setFlowModuleId(createFlowResult.getFlowModuleId());
        deployFlowResult = processEngine.deployFlow(param);
        LOGGER.info("deployFlow.||deployFlowResult={}", deployFlowResult);
    }

    // StartEvent --> ParallelGateway_fork --> UserTask_A & UserTask_B --> ParallelGateway_join --> EndEvent
    private void startProcessToEnd() {
        // Start process: executes up to the parallel fork, returns two active tasks
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId(deployFlowResult.getFlowDeployId());
        startProcessParam.setVariables(new ArrayList<>());
        StartProcessResult startProcessResult = processEngine.startProcess(startProcessParam);
        LOGGER.info("startProcess.||startProcessResult={}", startProcessResult);

        List<RuntimeResult.NodeExecuteResult> nodeExecuteResults = startProcessResult.getNodeExecuteResults();
        LOGGER.info("After fork: Task A=[{}], Task B=[{}]",
            nodeExecuteResults.get(0).getActiveTaskInstance().getModelKey(),
            nodeExecuteResults.get(1).getActiveTaskInstance().getModelKey());

        // Commit Task A (branch 0)
        CommitTaskParam commitTaskA = new CommitTaskParam();
        commitTaskA.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskA.setTaskInstanceId(nodeExecuteResults.get(0).getActiveTaskInstance().getNodeInstanceId());
        commitTaskA.setVariables(List.of(new InstanceData("taskA", "completed")));
        commitTaskA.setExtendProperties(extractBranchExtendProperties(startProcessResult.getExtendProperties(), 0));
        CommitTaskResult commitTaskResultA = processEngine.commitTask(commitTaskA);
        LOGGER.info("commitTaskA.||commitTaskResultA={}", commitTaskResultA);

        // Commit Task B (branch 1) - join gateway fires after both tasks are done
        CommitTaskParam commitTaskB = new CommitTaskParam();
        commitTaskB.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskB.setTaskInstanceId(nodeExecuteResults.get(1).getActiveTaskInstance().getNodeInstanceId());
        commitTaskB.setVariables(List.of(new InstanceData("taskB", "completed")));
        commitTaskB.setExtendProperties(extractBranchExtendProperties(startProcessResult.getExtendProperties(), 1));
        CommitTaskResult commitTaskResultB = processEngine.commitTask(commitTaskB);
        LOGGER.info("commitTaskB.||commitTaskResultB={}", commitTaskResultB);

        LOGGER.info("Parallel Gateway Demo completed. errCode={}", commitTaskResultB.getErrCode());
    }

    /**
     * Extract the extend properties for a specific parallel branch by index.
     * This is required by the parallel plugin to identify which branch is being committed.
     */
    private Map<String, Object> extractBranchExtendProperties(Map<String, Object> extendProperties, int branchIndex) {
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
