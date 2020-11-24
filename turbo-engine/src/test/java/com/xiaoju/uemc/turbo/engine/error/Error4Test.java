package com.xiaoju.uemc.turbo.engine.error;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaoju.uemc.turbo.engine.bo.NodeInstanceBO;
import com.xiaoju.uemc.turbo.engine.common.*;
import com.xiaoju.uemc.turbo.engine.dto.CommitTaskDTO;
import com.xiaoju.uemc.turbo.engine.dto.RecallTaskDTO;
import com.xiaoju.uemc.turbo.engine.dto.StartProcessDTO;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import com.xiaoju.uemc.turbo.engine.exception.SuspendException;
import com.xiaoju.uemc.turbo.engine.executor.ElementExecutor;
import com.xiaoju.uemc.turbo.engine.executor.ExecutorFactory;
import com.xiaoju.uemc.turbo.engine.executor.FlowExecutor;
import com.xiaoju.uemc.turbo.engine.executor.UserTaskExecutor;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.CommitTaskParam;
import com.xiaoju.uemc.turbo.engine.param.RecallTaskParam;
import com.xiaoju.uemc.turbo.engine.param.StartProcessParam;
import com.xiaoju.uemc.turbo.engine.processor.RuntimeProcessor;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.FlowModelUtil;
import com.xiaoju.uemc.turbo.engine.util.GroovyUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 错误码覆盖单元测试类之4000~4999 流程执行错误
 */
public class Error4Test extends BaseTest {

    @Resource
    private RuntimeProcessor runtimeProcessor;

    @Resource
    private ExecutorFactory executorFactory;

    @Resource
    private UserTaskExecutor userTaskExecutor;

    @Resource
    private FlowExecutor flowExecutor;

    private StartProcessDTO startProcess() {
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId("zk_deploy_id_1");
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("orderId", "string", "123"));
        startProcessParam.setVariables(variables);
        try {
            StartProcessDTO startProcessDTO = runtimeProcessor.startProcess(startProcessParam);
            return startProcessDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // COMMIT_FAILED(4001, "提交失败")
    @Test
    public void error_4001() {
        RuntimeContext runtimeContext = new RuntimeContext();
        FlowElement currentNodeModel = new FlowElement();
        currentNodeModel.setKey(StringUtils.EMPTY);
        runtimeContext.setCurrentNodeModel(currentNodeModel);
        runtimeContext.setNodeInstanceList(Lists.newArrayList());
        NodeInstanceBO suspendNodeInstance = new NodeInstanceBO();
        suspendNodeInstance.setNodeInstanceId("testNodeInstanceId");
        suspendNodeInstance.setNodeKey(StringUtils.EMPTY);
        suspendNodeInstance.setStatus(NodeInstanceStatus.FAILED);
        runtimeContext.setSuspendNodeInstance(suspendNodeInstance);
        try {
            userTaskExecutor.commit(runtimeContext);
        } catch (SuspendException e) {
            e.printStackTrace();
            Assert.assertTrue(e.getErrNo() == ErrorEnum.COMMIT_FAILED.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ROLLBACK_FAILED(4002, "撤销失败")
    @Test
    public void error_4002() {
        RuntimeContext runtimeContext = new RuntimeContext();
        runtimeContext.setFlowInstanceId("testFlowInstanceId");
        NodeInstanceBO suspendNodeInstance = new NodeInstanceBO();
        suspendNodeInstance.setNodeInstanceId("testNodeInstanceId");
        runtimeContext.setSuspendNodeInstance(suspendNodeInstance);
        try {
            flowExecutor.rollback(runtimeContext);
        } catch (ProcessException e) {
            e.printStackTrace();
            Assert.assertTrue(e.getErrNo() == ErrorEnum.ROLLBACK_FAILED.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TERMINATE_CANNOT_COMMIT(4003, "流程实例已终止, 无法继续提交")
    @Test
    public void error_4003() {
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        try {
            runtimeProcessor.terminateProcess(startProcessDTO.getFlowInstanceId());
            CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
            Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.TERMINATE_CANNOT_COMMIT.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // FLOW_INSTANCE_CANNOT_ROLLBACK(4004, "非执行中流程实例, 无法撤销")
    @Test
    public void error_4004() {
        // start process
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);
        try {
            // user task -> end node
            // commit
            CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);

            // rollback end node
            RecallTaskParam recallTaskParam = new RecallTaskParam();
            recallTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
            recallTaskParam.setTaskInstanceId(commitTaskDTO.getActiveTaskInstance().getNodeInstanceId());
            RecallTaskDTO recallTaskDTO = runtimeProcessor.recall(recallTaskParam);

            Assert.assertTrue(recallTaskDTO.getErrCode() == ErrorEnum.FLOW_INSTANCE_CANNOT_ROLLBACK.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // NO_NODE_TO_ROLLBACK(4005, "该实例没有可撤销的节点")
    @Test
    public void error_4005() {
        // no use
    }

    // NO_USER_TASK_TO_ROLLBACK(4006, "已完成第一个用户节点的撤销, 无法继续撤销")
    @Test
    public void error_4006() {
        // start process
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);
        try {
            // user task -> exclusive gateway node -> user task
            // commit
            CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);

            // rollback
            // user task <- exclusive gateway node <- user task
            RecallTaskParam recallTaskParam = new RecallTaskParam();
            recallTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
            recallTaskParam.setTaskInstanceId(commitTaskDTO.getActiveTaskInstance().getNodeInstanceId());
            RecallTaskDTO recallTaskDTO = runtimeProcessor.recall(recallTaskParam);

            // rollback
            // start node <- user task
            recallTaskParam = new RecallTaskParam();
            recallTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
            recallTaskParam.setTaskInstanceId(recallTaskDTO.getActiveTaskInstance().getNodeInstanceId());
            recallTaskDTO = runtimeProcessor.recall(recallTaskParam);

            Assert.assertTrue(recallTaskDTO.getErrCode() == ErrorEnum.NO_USER_TASK_TO_ROLLBACK.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GET_FLOW_DEPLOYMENT_FAILED(4007, "获取不到流程部署信息")
    @Test
    public void error_4007() {
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId("notExistFlowDeployId");
        try {
            StartProcessDTO startProcessDTO = runtimeProcessor.startProcess(startProcessParam);
            Assert.assertTrue(startProcessDTO.getErrCode() == ErrorEnum.GET_FLOW_DEPLOYMENT_FAILED.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GET_FLOW_INSTANCE_FAILED(4008, "获取不到流程实例信息")
    @Test
    public void error_4008() {
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId("notExistFlowInstanceId");
        commitTaskParam.setTaskInstanceId("test");
        commitTaskParam.setVariables(new ArrayList<>());
        try {
            CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
            Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.GET_FLOW_INSTANCE_FAILED.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GET_NODE_FAILED(4009, "获取不到待处理的节点")
    @Test
    public void error_4009() {
        RuntimeContext runtimeContext = new RuntimeContext();
        runtimeContext.setFlowElementMap(Maps.newHashMap());
        runtimeContext.setInstanceDataMap(Maps.newHashMap());
        try {
            flowExecutor.execute(runtimeContext);
        } catch (ProcessException e) {
            e.printStackTrace();
            Assert.assertTrue(e.getErrNo() == ErrorEnum.GET_NODE_FAILED.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GET_NODE_INSTANCE_FAILED(4010, "获取不到节点实例信息")
    @Test
    public void error_4010() {
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId("notExistNodeInstanceId");
        commitTaskParam.setVariables(new ArrayList<>());
        try {
            CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
            Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.GET_NODE_INSTANCE_FAILED.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GET_INSTANCE_DATA_FAILED(4011, "获取不到实例数据信息")
    @Test
    public void error_4011() {
        // depend on db data
    }

    // GET_HOOK_CONFIG_FAILED(4012, "获取不到hook配置")
    @Test
    public void error_4012() {
        // first, config hookInfoIds in ExclusiveGateway node
        // second, set optimus_prime.hook.url empty
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);
        try {
            // user task -> exclusive gateway node -> user task
            CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
            Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.GET_HOOK_CONFIG_FAILED.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GET_OUTGOING_FAILED(4013, "找不到下一个待执行节点")
    @Test
    public void error_4013() {
        try {
            FlowElement currentFlowElement = new FlowElement();
            currentFlowElement.setKey("ExclusiveGateway_0yq2l0s");
            currentFlowElement.setType(FlowElementType.EXCLUSIVE_GATEWAY);
            currentFlowElement.setIncoming(Lists.newArrayList());
            currentFlowElement.setOutgoing(Lists.newArrayList("SequenceFlow_15rfdft", "SequenceFlow_1lc9xoo"));

            FlowElement SequenceFlow_15rfdft = new FlowElement();
            SequenceFlow_15rfdft.setKey("SequenceFlow_15rfdft");
            SequenceFlow_15rfdft.setType(FlowElementType.SEQUENCE_FLOW);
            SequenceFlow_15rfdft.setIncoming(Lists.newArrayList("ExclusiveGateway_0yq2l0s"));
            SequenceFlow_15rfdft.setOutgoing(Lists.newArrayList());
            Map<String, Object> properties = new HashMap<>();
            properties.put(Constants.ELEMENT_PROPERTIES.CONDITION, "orderId.equals(\"123\")");
            SequenceFlow_15rfdft.setProperties(properties);

            FlowElement SequenceFlow_1lc9xoo = new FlowElement();
            SequenceFlow_1lc9xoo.setKey("SequenceFlow_1lc9xoo");
            SequenceFlow_1lc9xoo.setType(FlowElementType.SEQUENCE_FLOW);
            SequenceFlow_1lc9xoo.setIncoming(Lists.newArrayList("ExclusiveGateway_0yq2l0s"));
            SequenceFlow_1lc9xoo.setOutgoing(Lists.newArrayList());
            Map<String, Object> _properties = new HashMap<>();
            _properties.put(Constants.ELEMENT_PROPERTIES.CONDITION, "orderId.equals(\"456\")");
            SequenceFlow_1lc9xoo.setProperties(_properties);

            Map<String, FlowElement> flowElementMap = new HashMap<>();
            flowElementMap.put("ExclusiveGateway_0yq2l0s", currentFlowElement);
            flowElementMap.put("SequenceFlow_15rfdft", SequenceFlow_15rfdft);
            flowElementMap.put("SequenceFlow_1lc9xoo", SequenceFlow_1lc9xoo);

            Map<String, InstanceData> instanceDataMap = new HashMap<>();
            instanceDataMap.put("orderId", new InstanceData("orderId", "string", "123456"));

            FlowModelUtil.calculateNextNode(currentFlowElement, flowElementMap, instanceDataMap);
        } catch (ProcessException e) {
            e.printStackTrace();
            Assert.assertTrue(e.getErrNo() == ErrorEnum.GET_OUTGOING_FAILED.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // UNSUPPORTED_ELEMENT_TYPE(4014, "不支持的节点操作")
    @Test
    public void error_4014() {
        FlowElement unSupportedElement = new FlowElement();
        unSupportedElement.setType(100);
        try {
            ElementExecutor elementExecutor = executorFactory.getElementExecutor(unSupportedElement);
        } catch (ProcessException e) {
            e.printStackTrace();
            Assert.assertTrue(e.getErrNo() == ErrorEnum.UNSUPPORTED_ELEMENT_TYPE.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // MISSING_DATA(4015, "表达式运算缺少数据")
    @Test
    public void error_4015() {
        try {
            GroovyUtil.calculate("orderId.equals(\"123\")", new HashMap<>());
        } catch (ProcessException e) {
            e.printStackTrace();
            Assert.assertTrue(e.getErrNo() == ErrorEnum.MISSING_DATA.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // SAVE_FLOW_INSTANCE_FAILED(4016, "保存流程实例失败")
    @Test
    public void error_4016() {
        // oh shit, it is very hard.
    }

    // SAVE_INSTANCE_DATA_FAILED(4017, "保存实例数据失败")
    @Test
    public void error_4017() {
        // oh shit, it is very hard.
    }

    // GROOVY_CALCULATE_FAILED(4018, "表达式执行失败")
    @Test
    public void error_4018() {
        try {
            FlowElement currentFlowElement = new FlowElement();
            currentFlowElement.setKey("ExclusiveGateway_0yq2l0s");
            currentFlowElement.setType(FlowElementType.EXCLUSIVE_GATEWAY);
            currentFlowElement.setIncoming(Lists.newArrayList());
            currentFlowElement.setOutgoing(Lists.newArrayList("SequenceFlow_15rfdft", "SequenceFlow_1lc9xoo"));

            FlowElement SequenceFlow_15rfdft = new FlowElement();
            SequenceFlow_15rfdft.setKey("SequenceFlow_15rfdft");
            SequenceFlow_15rfdft.setType(FlowElementType.SEQUENCE_FLOW);
            SequenceFlow_15rfdft.setIncoming(Lists.newArrayList("ExclusiveGateway_0yq2l0s"));
            SequenceFlow_15rfdft.setOutgoing(Lists.newArrayList());
            Map<String, Object> properties = new HashMap<>();
            properties.put(Constants.ELEMENT_PROPERTIES.CONDITION, "orderId.equals(\"123\")");
            SequenceFlow_15rfdft.setProperties(properties);

            FlowElement SequenceFlow_1lc9xoo = new FlowElement();
            SequenceFlow_1lc9xoo.setKey("SequenceFlow_1lc9xoo");
            SequenceFlow_1lc9xoo.setType(FlowElementType.SEQUENCE_FLOW);
            SequenceFlow_1lc9xoo.setIncoming(Lists.newArrayList("ExclusiveGateway_0yq2l0s"));
            SequenceFlow_1lc9xoo.setOutgoing(Lists.newArrayList());
            Map<String, Object> _properties = new HashMap<>();
            _properties.put(Constants.ELEMENT_PROPERTIES.CONDITION, "orderId.equals(\"456\")");
            SequenceFlow_1lc9xoo.setProperties(_properties);

            Map<String, FlowElement> flowElementMap = new HashMap<>();
            flowElementMap.put("ExclusiveGateway_0yq2l0s", currentFlowElement);
            flowElementMap.put("SequenceFlow_15rfdft", SequenceFlow_15rfdft);
            flowElementMap.put("SequenceFlow_1lc9xoo", SequenceFlow_1lc9xoo);

            Map<String, InstanceData> instanceDataMap = new HashMap<>();
            instanceDataMap.put("notExist", new InstanceData("orderId", "string", "123456"));

            FlowModelUtil.calculateNextNode(currentFlowElement, flowElementMap, instanceDataMap);
        } catch (ProcessException e) {
            e.printStackTrace();
            // No such property: orderId for class: Script1
            Assert.assertTrue(e.getErrNo() == ErrorEnum.GROOVY_CALCULATE_FAILED.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
