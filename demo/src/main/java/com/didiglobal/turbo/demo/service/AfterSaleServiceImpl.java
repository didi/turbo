package com.didiglobal.turbo.demo.service;

import com.didiglobal.turbo.demo.util.Constant;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rick
 * @Date 2022/4/6 14:07
 * 案例1：某团购售后流程
 * 用户A在订单列表中选择订单，判断订单状态，如果状态为未发货，则直接跳转至退款申请页，
 * 如果状态为待收货则提示不支持售后，跳转至物流信息页，如果状态为已收货，则跳转至售后页填写售后信息并提交。
 * <p>
 * 未发货
 * --->   用户节点（申请取消）     --->
 * 未收到货
 * 开始节点 ->  用户节点（输入订单相关信息) --->排他节点(判断订单状态)  --->   用户节点（展示物流信息）  --->   结束节点
 * 已收到货
 * --->    用户节点（填写售后原因） --->
 */
@Service
public class AfterSaleServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(AfterSaleServiceImpl.class);

    @Resource
    private ProcessEngine processEngine;

    private CreateFlowParam createFlowParam;
    private CreateFlowResult createFlowResult;
    private UpdateFlowResult updateFlowResult;
    private DeployFlowResult deployFlowResult;

    public void run() {
        LOGGER.info("AfterSale Demo run:");

        LOGGER.info("AfterSale definition:");

        createFlow();

        updateFlow();

        deployFlow();

        LOGGER.info("AfterSale runtime:");

        startProcessToEnd();
    }

    private void createFlow() {
        createFlowParam = new CreateFlowParam(Constant.tenant, Constant.caller);
        createFlowParam.setFlowKey("after_sale");
        createFlowParam.setFlowName("售后处理SOP");
        createFlowParam.setRemark("demo");
        createFlowParam.setOperator(Constant.operator);
        createFlowResult = processEngine.createFlow(createFlowParam);
        LOGGER.info("createFlow.||createFlowResult={}", createFlowResult);
    }

    private void updateFlow() {
        UpdateFlowParam updateFlowParam = new UpdateFlowParam(Constant.tenant, Constant.caller);
        updateFlowParam.setFlowModel(EntityBuilder.buildAfterSaleFlowModelStr());
        updateFlowParam.setFlowModuleId(createFlowResult.getFlowModuleId());
        updateFlowResult = processEngine.updateFlow(updateFlowParam);
        LOGGER.info("updateFlow.||updateFlowResult={}", updateFlowResult);
    }

    private void deployFlow() {
        DeployFlowParam param = new DeployFlowParam(Constant.tenant, Constant.caller);
        param.setFlowModuleId(createFlowResult.getFlowModuleId());
        deployFlowResult = processEngine.deployFlow(param);
        LOGGER.info("deployFlow.||deployFlowResult={}", deployFlowResult);
    }

    private void startProcessToEnd() {
        StartProcessResult startProcessResult = startProcess();
        CommitTaskResult commitTaskResult = chooseUnreleaseOrder(startProcessResult);
        RollbackTaskResult rollbackTaskResult = rollbackToChoose(commitTaskResult);
        CommitTaskResult result = chooseReleaseOrder(rollbackTaskResult);
        commitCompleteProcess(result);
    }

    // 用户拉起售后sop
    private StartProcessResult startProcess() {
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId(deployFlowResult.getFlowDeployId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("user_id", "userId"));
        startProcessParam.setVariables(variables);
        StartProcessResult startProcessResult = processEngine.startProcess(startProcessParam);

        LOGGER.info("startProcess.||startProcessResult={}", startProcessResult);
        return startProcessResult;
    }

    // 选择未发货的订单
    private CommitTaskResult chooseUnreleaseOrder(StartProcessResult startProcessResult) {
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("order_id", "orderID"));
        variables.add(new InstanceData("status", "0"));
        commitTaskParam.setVariables(variables);

        CommitTaskResult commitTaskResult = processEngine.commitTask(commitTaskParam);
        LOGGER.info("chooseUnrelesseOrder.||commitTaskResult={}", commitTaskResult);
        return commitTaskResult;
    }

    // 回退，重新选取订单
    private RollbackTaskResult rollbackToChoose(CommitTaskResult commitTaskResult) {
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        RollbackTaskResult rollbackTaskResult = processEngine.rollbackTask(rollbackTaskParam);

        LOGGER.info("rollbackToChoose.||rollbackTaskResult={}", rollbackTaskResult);
        return rollbackTaskResult;
    }

    //选取已发货，未收到货 订单
    private CommitTaskResult chooseReleaseOrder(RollbackTaskResult rollbackTaskResult) {
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(rollbackTaskResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(rollbackTaskResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("order_id", "orderID"));
        variables.add(new InstanceData("status", "1"));
        commitTaskParam.setVariables(variables);

        CommitTaskResult commitTaskResult = processEngine.commitTask(commitTaskParam);
        LOGGER.info("chooseReleaseOrder.||commitTaskResult={}", commitTaskResult);
        return commitTaskResult;
    }

    //BadCase:已完成流程,再次提交订单和状态，此时流程已经结束会报错。
    private CommitTaskResult commitCompleteProcess(CommitTaskResult commitTaskResult) {
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("order_id", "orderID"));
        variables.add(new InstanceData("status", "2"));
        commitTaskParam.setVariables(variables);

        CommitTaskResult result = processEngine.commitTask(commitTaskParam);
        LOGGER.info("chooseReceivedOrderBadCase.||commitTaskResult={}", result);
        return result;
    }
}
