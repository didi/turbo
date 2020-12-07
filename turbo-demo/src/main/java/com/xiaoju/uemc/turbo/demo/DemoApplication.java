package com.xiaoju.uemc.turbo.demo;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.xiaoju.uemc.turbo.demo.util.EntityBuilder;
import com.xiaoju.uemc.turbo.engine.engine.ProcessEngine;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.*;
import com.xiaoju.uemc.turbo.engine.result.*;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DruidDataSourceAutoConfigure.class})
@ComponentScan(basePackages = {"com.xiaoju.uemc.turbo.engine", "com.xiaoju.uemc.turbo.demo"})
@MapperScan(basePackages = {"com.xiaoju.uemc.turbo.engine.dao"})
public class DemoApplication implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoApplication.class);

    @Resource
    private ProcessEngine processEngine;

    private static final String tenant = "company";
    private static final String caller = "person";
    private static final String operator = "xiaoming";

    private CreateFlowParam createFlowParam;
    private CreateFlowResult createFlowResult;
    private UpdateFlowResult updateFlowResult;
    private DeployFlowResult deployFlowResult;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    public void run(String... args) throws Exception {
        LOGGER.info("Turbo Demo run:");

        LOGGER.info("Turbo definition:");

        createFlow();

        updateFlow();

        deployFlow();

        LOGGER.info("Turbo runtime:");

        String functionName = "startProcessToUserTaskAndcommitTaskAndRollbackTask";
        if(functionName.equals("startProcessToEnd")){
            startProcessToEnd();
        }else {
            startProcessToUserTaskAndcommitTaskAndRollbackTask();
        }
    }

    public void createFlow(){
        createFlowParam = new CreateFlowParam(tenant, caller);
        createFlowParam.setFlowKey("flowKey1");
        createFlowParam.setFlowName("flowName1");
        createFlowParam.setRemark("这个人很懒，什么都没写");
        createFlowParam.setOperator(operator);
        createFlowResult = processEngine.createFlow(createFlowParam);
        LOGGER.info("createFlow.||createFlowResult={}", createFlowResult);
    }

    // test flow model
    // StartEvent -> ExclusiveGateway -> UserTask -> UserTask -> EndEvent
    //                       |
    //                       |--> EndEvent
    public void updateFlow(){
        UpdateFlowParam updateFlowParam = new UpdateFlowParam(tenant, caller);
        updateFlowParam.setFlowModel(EntityBuilder.buildFlowModelStr());
        updateFlowParam.setFlowModuleId(createFlowResult.getFlowModuleId());
        updateFlowResult = processEngine.updateFlow(updateFlowParam);
        LOGGER.info("updateFlow.||updateFlowResult={}", updateFlowResult);
    }

    public void deployFlow(){
        DeployFlowParam param = new DeployFlowParam(tenant, caller);
        param.setFlowModuleId(createFlowResult.getFlowModuleId());
        deployFlowResult = processEngine.deployFlow(param);
        LOGGER.info("deployFlow.||deployFlowResult={}", deployFlowResult);
    }

    // StartEvent -> ExclusiveGateway -> EndEvent
    public void startProcessToEnd(){
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId(deployFlowResult.getFlowDeployId());
        List<InstanceData> variables = new ArrayList<>();
        // you can change orderId value in order to change flow direction
        variables.add(new InstanceData("orderId", "string", "123"));
        startProcessParam.setVariables(variables);
        StartProcessResult startProcessResult = processEngine.startProcess(startProcessParam);
        LOGGER.info("startProcessToEnd.||startProcessResult={}", startProcessResult);
    }

    // startProcess -> commit -> rollback
    public void startProcessToUserTaskAndcommitTaskAndRollbackTask(){
        StartProcessResult startProcessResult = startProcessToUserTask();
        CommitTaskResult commitTaskResult = commitTask(startProcessResult);
        RollbackTaskResult rollbackTaskResult = rollbackTask(commitTaskResult);
    }

    // StartEvent -> ExclusiveGateway -> UserTask
    public StartProcessResult startProcessToUserTask(){
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId(deployFlowResult.getFlowDeployId());
        List<InstanceData> variables = new ArrayList<>();
        // you can change orderId value in order to change flow direction
        variables.add(new InstanceData("orderId", "string", "456"));
        startProcessParam.setVariables(variables);
        StartProcessResult startProcessResult = processEngine.startProcess(startProcessParam);
        LOGGER.info("startProcessToUserTask.||startProcessResult={}", startProcessResult);
        return startProcessResult;
    }

    // UserTask -> UserTask
    public CommitTaskResult commitTask(StartProcessResult startProcessResult){
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("commitTime", "int", 1));
        commitTaskParam.setVariables(variables);

        CommitTaskResult commitTaskResult = processEngine.commitTask(commitTaskParam);
        LOGGER.info("commitTask.||commitTaskResult={}", commitTaskResult);
        return commitTaskResult;
    }

    // UserTask <- UserTask
    public RollbackTaskResult rollbackTask(CommitTaskResult commitTaskResult){
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        RollbackTaskResult rollbackTaskResult = processEngine.rollbackTask(rollbackTaskParam);
        LOGGER.info("rollbackTask.||rollbackTaskResult={}", rollbackTaskResult);
        return rollbackTaskResult;
    }
}
