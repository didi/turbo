package com.didiglobal.turbo.demo.runner;

import com.didiglobal.turbo.demo.util.EntityBuilder;
import com.didiglobal.turbo.engine.engine.ProcessEngine;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.param.CommitTaskParam;
import com.didiglobal.turbo.engine.param.CreateFlowParam;
import com.didiglobal.turbo.engine.param.DeployFlowParam;
import com.didiglobal.turbo.engine.param.RollbackTaskParam;
import com.didiglobal.turbo.engine.param.StartProcessParam;
import com.didiglobal.turbo.engine.param.UpdateFlowParam;
import com.didiglobal.turbo.engine.result.CommitTaskResult;
import com.didiglobal.turbo.engine.result.CreateFlowResult;
import com.didiglobal.turbo.engine.result.DeployFlowResult;
import com.didiglobal.turbo.engine.result.RollbackTaskResult;
import com.didiglobal.turbo.engine.result.StartProcessResult;
import com.didiglobal.turbo.engine.result.UpdateFlowResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Order(1)
@Component
public class CommonDemoRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonDemoRunner.class);

    @Resource
    private ProcessEngine processEngine;

    private static final String tenant = "testTenant";
    private static final String caller = "testCaller";
    private static final String operator = "xiaoming";

    private CreateFlowParam createFlowParam;
    private CreateFlowResult createFlowResult;
    private UpdateFlowResult updateFlowResult;
    private DeployFlowResult deployFlowResult;

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Turbo Common Demo run:");

        LOGGER.info("Turbo definition:");

        createFlow();

        updateFlow();

        deployFlow();

        LOGGER.info("Turbo runtime:");

        startProcessToEnd();
    }

    public void createFlow() {
        createFlowParam = new CreateFlowParam(tenant, caller);
        createFlowParam.setFlowKey("flowKey1");
        createFlowParam.setFlowName("flowName1");
        createFlowParam.setRemark("这个人很懒，什么都没写");
        createFlowParam.setOperator(operator);
        createFlowResult = processEngine.createFlow(createFlowParam);
        LOGGER.info("createFlow.||createFlowResult={}", createFlowResult);
    }

    //   Test flow model
    //
    //
    //                                    |--> UserTask2 -> EndEvent1
    //                                    |
    //   StartEvent1 -> UserTask1 -> ExclusiveGateway1
    //                                    |
    //                                    |--> UserTask3 -> EndEvent2
    //
    //   Describe:
    //   1.from ExclusiveGateway1 to UserTask2 condition is message.equals("open")
    //   2.from ExclusiveGateway1 to UserTask3 condition is defaultCondition
    public void updateFlow() {
        UpdateFlowParam updateFlowParam = new UpdateFlowParam(tenant, caller);
        updateFlowParam.setFlowModel(EntityBuilder.buildFlowModelStr());
        updateFlowParam.setFlowModuleId(createFlowResult.getFlowModuleId());
        updateFlowResult = processEngine.updateFlow(updateFlowParam);
        LOGGER.info("updateFlow.||updateFlowResult={}", updateFlowResult);
    }

    public void deployFlow() {
        DeployFlowParam param = new DeployFlowParam(tenant, caller);
        param.setFlowModuleId(createFlowResult.getFlowModuleId());
        deployFlowResult = processEngine.deployFlow(param);
        LOGGER.info("deployFlow.||deployFlowResult={}", deployFlowResult);
    }

    public void startProcessToEnd() {
        StartProcessResult startProcessResult = startProcessToUserTask1();
        CommitTaskResult commitTaskResult = commitToUserTask2(startProcessResult);
        RollbackTaskResult rollbackTaskResult = rollbackToUserTask1(commitTaskResult);
        CommitTaskResult commitTaskResult1 = commitToUserTask3(rollbackTaskResult);
        CommitTaskResult commitTaskResult2 = commitToEndEvent2(commitTaskResult1);
    }

    // StartEvent1 -> UserTask1
    public StartProcessResult startProcessToUserTask1() {
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId(deployFlowResult.getFlowDeployId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("commitTime", 1));
        startProcessParam.setVariables(variables);
        StartProcessResult startProcessResult = processEngine.startProcess(startProcessParam);

        LOGGER.info("startProcessToUserTask1.||startProcessResult={}", startProcessResult);
        return startProcessResult;
    }

    // UserTask1 -> ExclusiveGateway1 -> UserTask2
    public CommitTaskResult commitToUserTask2(StartProcessResult startProcessResult) {
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("commitTime", 2));
        variables.add(new InstanceData("message", "open"));
        commitTaskParam.setVariables(variables);

        CommitTaskResult commitTaskResult = processEngine.commitTask(commitTaskParam);
        LOGGER.info("commitToUserTask2.||commitTaskResult={}", commitTaskResult);
        return commitTaskResult;
    }

    // UserTask1 <- ExclusiveGateway1 <- UserTask2
    public RollbackTaskResult rollbackToUserTask1(CommitTaskResult commitTaskResult) {
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        RollbackTaskResult rollbackTaskResult = processEngine.rollbackTask(rollbackTaskParam);

        LOGGER.info("rollbackToUserTask1.||rollbackTaskResult={}", rollbackTaskResult);
        return rollbackTaskResult;
    }

    // UserTask1 -> ExclusiveGateway1 -> UserTask3
    public CommitTaskResult commitToUserTask3(RollbackTaskResult rollbackTaskResult) {
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(rollbackTaskResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(rollbackTaskResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("commitTime", 3));
        variables.add(new InstanceData("message", "close"));
        commitTaskParam.setVariables(variables);

        CommitTaskResult commitTaskResult = processEngine.commitTask(commitTaskParam);
        LOGGER.info("commitToUserTask3.||commitTaskResult={}", commitTaskResult);
        return commitTaskResult;
    }

    // UserTask3 -> EndEvent2
    public CommitTaskResult commitToEndEvent2(CommitTaskResult commitTaskResult) {
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("commitTime", 4));
        commitTaskParam.setVariables(variables);

        CommitTaskResult commitTaskResult1 = processEngine.commitTask(commitTaskParam);
        LOGGER.info("commitToEndEvent2.||commitTaskResult1={}", commitTaskResult1);
        return commitTaskResult1;
    }
}
