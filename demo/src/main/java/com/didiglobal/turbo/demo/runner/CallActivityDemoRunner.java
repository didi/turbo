package com.didiglobal.turbo.demo.runner;

import com.didiglobal.turbo.demo.util.EntityBuilder;
import com.didiglobal.turbo.engine.common.Constants;
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
import com.didiglobal.turbo.engine.result.UpdateFlowResult;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

@Order(0)
@Component
public class CallActivityDemoRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallActivityDemoRunner.class);

    @Resource
    private ProcessEngine processEngine;

    private static final String tenant = "testTenant";
    private static final String caller = "testCaller";
    private static final String operator = "xiaoming";

    private CreateFlowParam createMainFlowParam;
    private CreateFlowResult createMainFlowResult;
    private UpdateFlowResult updateMainFlowResult;
    private DeployFlowResult deployMainFlowResult;

    private CreateFlowParam createSubFlowParam;
    private CreateFlowResult createSubFlowResult;
    private UpdateFlowResult updateSubFlowResult;
    private DeployFlowResult deploySubFlowResult;

    @Override
    public void run(String... args) {
        LOGGER.info("Turbo CallActivity Demo run:");

        LOGGER.info("Turbo definition:");

        createFlow();

        updateFlow();

        deployFlow();

        LOGGER.info("Turbo runtime:");

        // Show authorized situations
        startProcessToEnd(true);

        // Show unauthorized situations
        startProcessToEnd(false);
    }

    public void createFlow() {
        // Create main flow
        createMainFlowParam = new CreateFlowParam(tenant, caller);
        createMainFlowParam.setFlowKey("mainFlowKey");
        createMainFlowParam.setFlowName("mainFlowName");
        createMainFlowParam.setRemark("主流程");
        createMainFlowParam.setOperator(operator);
        createMainFlowResult = processEngine.createFlow(createMainFlowParam);
        LOGGER.info("createMainFlow.||createMainFlowResult={}", createMainFlowResult);
        // Create sub flow
        createSubFlowParam = new CreateFlowParam(tenant, caller);
        createSubFlowParam.setFlowKey("subFlowKey");
        createSubFlowParam.setFlowName("subFlowName");
        createSubFlowParam.setRemark("子流程");
        createSubFlowParam.setOperator(operator);
        createSubFlowResult = processEngine.createFlow(createSubFlowParam);
        LOGGER.info("createSubFlow.||createSubFlowResult={}", createSubFlowResult);
    }

    public void updateFlow() {
        // Update main flow
        UpdateFlowParam updateMainFlowParam = new UpdateFlowParam(tenant, caller);
        // The sub flowModuleId referenced by CallActivity can be configured here, and can also be specified later
        updateMainFlowParam.setFlowModel(EntityBuilder.buildFlowModelStr2(createSubFlowResult.getFlowModuleId()));
        updateMainFlowParam.setFlowModuleId(createMainFlowResult.getFlowModuleId());
        updateMainFlowResult = processEngine.updateFlow(updateMainFlowParam);
        LOGGER.info("updateMainFlow.||updateMainFlowResult={}", updateMainFlowResult);
        // Update sub flow
        UpdateFlowParam updateSubFlowParam = new UpdateFlowParam(tenant, caller);
        updateSubFlowParam.setFlowModel(EntityBuilder.buildFlowModelStr3());
        updateSubFlowParam.setFlowModuleId(createSubFlowResult.getFlowModuleId());
        updateSubFlowResult = processEngine.updateFlow(updateSubFlowParam);
        LOGGER.info("updateSubFlow.||updateSubFlowResult={}", updateSubFlowResult);
    }

    public void deployFlow() {
        // Deploy main flow
        DeployFlowParam deployMainFlowParam = new DeployFlowParam(tenant, caller);
        deployMainFlowParam.setFlowModuleId(createMainFlowResult.getFlowModuleId());
        deployMainFlowResult = processEngine.deployFlow(deployMainFlowParam);
        LOGGER.info("deployMainFlow.||deployMainFlowResult={}", deployMainFlowResult);
        // Deploy sub flow
        DeployFlowParam deploySubFlowParam = new DeployFlowParam(tenant, caller);
        deploySubFlowParam.setFlowModuleId(createSubFlowResult.getFlowModuleId());
        deploySubFlowResult = processEngine.deployFlow(deploySubFlowParam);
        LOGGER.info("deploySubFlow.||deploySubFlowResult={}", deploySubFlowResult);
    }

    public void startProcessToEnd(boolean auth) {
        // Encountered the sub process node of 'Authorization'
        StartProcessResult startProcessResult = startProcessToCallActivity();
        // Driver 'Authorization' sub process node
        CommitTaskResult commitTaskResult = commitCallActivityToUserTask1(startProcessResult, auth);
        if (!auth) {
            // Now we are stuck at the first user node of the sub process 'User Authorization'
            commitTaskResult = commitCallActivityUserTask(commitTaskResult);
        }
        // Now we are stuck at the first user node of the parent process 'write complaint information'
        commitTaskResult = commitMainFlowUserTask1(commitTaskResult);
        // Now it is stuck in the second user node of the parent process, 'Generate Work Order', driving the completion of the parent process
        commitTaskResult = commitMainFlowUserTask2(commitTaskResult);

        assert commitTaskResult.getStatus() == ErrorEnum.SUCCESS.getErrNo();
    }

    private StartProcessResult startProcessToCallActivity() {
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId(deployMainFlowResult.getFlowDeployId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("userId", "001"));
        startProcessParam.setVariables(variables);
        StartProcessResult startProcessResult = processEngine.startProcess(startProcessParam);
        LOGGER.info("startProcessToCallActivity.||startProcessResult={}", startProcessResult);
        return startProcessResult;
    }

    public CommitTaskResult commitCallActivityToUserTask1(StartProcessResult startProcessResult, boolean auth) {
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());

        // Here we select the previously configured sub flowModuleId
        String callActivityFlowModuleId = startProcessResult.getActiveTaskInstance().getProperties()
            .get(Constants.ELEMENT_PROPERTIES.CALL_ACTIVITY_FLOW_MODULE_ID).toString();
        commitTaskParam.setCallActivityFlowModuleId(callActivityFlowModuleId);

        // Data required for assembling authorization sub processes: authorization status
        List<InstanceData> variables = new ArrayList<>();

        // If 0 is passed in, it means that the current authorization is not granted,
        // and it will be suspended at the first user node of the sub process,
        // at which point it will stay at the [User Authorization] user node of the sub process

        // If 1 is passed in, it means that the current authorization has been granted.
        // The sub process will be executed and the engine will automatically return to the upper parent process for driving.
        // At this point, it will stay in the user node of the parent process's' Fill in Complaint Information '
        variables.add(new InstanceData("authStatus", auth ? 1 : 0));
        commitTaskParam.setVariables(variables);

        CommitTaskResult commitTaskResult = processEngine.commitTask(commitTaskParam);
        LOGGER.info("commitCallActivityToUserTask1.||commitTaskResult={}", commitTaskResult);
        return commitTaskResult;
    }

    // When submitting the CallActivityUserTask node, special attention needs to be paid.
    // We need to submit 【 Parent Process Instance ID 】 and 【 Node Instance ID of Child Process 】
    private CommitTaskResult commitCallActivityUserTask(CommitTaskResult commitTaskResult) {
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        List<RuntimeResult> subNodeResultList = commitTaskResult.getActiveTaskInstance().getSubNodeResultList();
        assert subNodeResultList != null && subNodeResultList.size() == 1; // sync & single
        // Remind: The submitted node instance ID of the subprocess instance is mainly
        // used to solve the problem of request idempotence
        String nodeInstanceId = subNodeResultList.get(0).getActiveTaskInstance().getNodeInstanceId();
        commitTaskParam.setTaskInstanceId(nodeInstanceId);

        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("userAuth", Boolean.TRUE)); // User Consent Authorization
        commitTaskParam.setVariables(variables);

        commitTaskResult = processEngine.commitTask(commitTaskParam);
        LOGGER.info("commitCallActivityUserTask.||commitTaskResult={}", commitTaskResult);
        return commitTaskResult;
    }

    private CommitTaskResult commitMainFlowUserTask1(CommitTaskResult commitTaskResult) {
        {
            // Complaint content filled in by the user
        }
        String userComplaintContent = "Network Error.";

        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());

        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("userComplaintContent", userComplaintContent));
        commitTaskParam.setVariables(variables);

        commitTaskResult = processEngine.commitTask(commitTaskParam);
        LOGGER.info("commitMainFlowUserTask1.||commitTaskResult={}", commitTaskResult);
        return commitTaskResult;
    }

    private CommitTaskResult commitMainFlowUserTask2(CommitTaskResult commitTaskResult) {
        {
            // According to the complaint content filled in by the user,
            // the upper application calls the backend interface to generate a work order
        }

        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        commitTaskParam.setVariables(Lists.newArrayList());

        commitTaskResult = processEngine.commitTask(commitTaskParam);
        LOGGER.info("commitMainFlowUserTask2.||commitTaskResult={}", commitTaskResult);
        return commitTaskResult;
    }
}
