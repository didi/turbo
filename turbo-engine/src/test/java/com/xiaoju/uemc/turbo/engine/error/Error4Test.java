package com.xiaoju.uemc.turbo.engine.error;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaoju.uemc.turbo.engine.bo.NodeInstanceBO;
import com.xiaoju.uemc.turbo.engine.common.*;
import com.xiaoju.uemc.turbo.engine.dto.CommitTaskDTO;
import com.xiaoju.uemc.turbo.engine.dto.RecallTaskDTO;
import com.xiaoju.uemc.turbo.engine.dto.StartProcessDTO;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
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

    private StartProcessDTO startProcess() throws Exception {
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId("zk_deploy_id_1");
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("orderId", "string", "123"));
        startProcessParam.setVariables(variables);
        return runtimeProcessor.startProcess(startProcessParam);
    }

    // COMMIT_FAILED
    // UserTask node don't allow commit in FAILED status
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
        int errNo = ErrorEnum.SUCCESS.getErrNo();
        try {
            userTaskExecutor.commit(runtimeContext);
        } catch (ProcessException e) {
            e.printStackTrace();
            errNo = e.getErrNo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue(errNo == ErrorEnum.COMMIT_FAILED.getErrNo());
    }

    // ROLLBACK_FAILED
    // The flowInstance does not exist
    @Test
    public void error_4002() {
        RuntimeContext runtimeContext = new RuntimeContext();
        runtimeContext.setFlowInstanceId("notExistFlowInstanceId");
        NodeInstanceBO suspendNodeInstance = new NodeInstanceBO();
        suspendNodeInstance.setNodeInstanceId("testNodeInstanceId");
        runtimeContext.setSuspendNodeInstance(suspendNodeInstance);
        int errNo = ErrorEnum.SUCCESS.getErrNo();
        try {
            flowExecutor.rollback(runtimeContext);
        } catch (ProcessException e) {
            e.printStackTrace();
            errNo = e.getErrNo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue(errNo == ErrorEnum.ROLLBACK_FAILED.getErrNo());
    }

    // TERMINATE_CANNOT_COMMIT
    @Test
    public void error_4003() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        // terminate process
        runtimeProcessor.terminateProcess(startProcessDTO.getFlowInstanceId());
        // commit
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.TERMINATE_CANNOT_COMMIT.getErrNo());
    }

    // FLOW_INSTANCE_CANNOT_ROLLBACK
    @Test
    public void error_4004() throws Exception {
        // start process
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);
        // UserTask-> EndEvent
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        // rollback EndEvent
        RecallTaskParam recallTaskParam = new RecallTaskParam();
        recallTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        recallTaskParam.setTaskInstanceId(commitTaskDTO.getActiveTaskInstance().getNodeInstanceId());
        RecallTaskDTO recallTaskDTO = runtimeProcessor.recall(recallTaskParam);

        Assert.assertTrue(recallTaskDTO.getErrCode() == ErrorEnum.FLOW_INSTANCE_CANNOT_ROLLBACK.getErrNo());
    }

    // NO_NODE_TO_ROLLBACK
    @Test
    public void error_4005() {
        // no use
    }

    // NO_USER_TASK_TO_ROLLBACK
    @Test
    public void error_4006() throws Exception {
        // start process
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);
        // UserTask -> ExclusiveGateway -> UserTask
        // commit
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        // rollback
        // UserTask <- ExclusiveGateway <- UserTask
        RecallTaskParam recallTaskParam = new RecallTaskParam();
        recallTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        recallTaskParam.setTaskInstanceId(commitTaskDTO.getActiveTaskInstance().getNodeInstanceId());
        RecallTaskDTO recallTaskDTO = runtimeProcessor.recall(recallTaskParam);
        // rollback
        // StartEvent <- UserTask
        recallTaskParam = new RecallTaskParam();
        recallTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        recallTaskParam.setTaskInstanceId(recallTaskDTO.getActiveTaskInstance().getNodeInstanceId());
        recallTaskDTO = runtimeProcessor.recall(recallTaskParam);

        Assert.assertTrue(recallTaskDTO.getErrCode() == ErrorEnum.NO_USER_TASK_TO_ROLLBACK.getErrNo());

    }

    // GET_FLOW_DEPLOYMENT_FAILED
    @Test
    public void error_4007() throws Exception {
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId("notExistFlowDeployId");
        StartProcessDTO startProcessDTO = runtimeProcessor.startProcess(startProcessParam);
        Assert.assertTrue(startProcessDTO.getErrCode() == ErrorEnum.GET_FLOW_DEPLOYMENT_FAILED.getErrNo());
    }

    // GET_FLOW_INSTANCE_FAILED
    @Test
    public void error_4008() throws Exception {
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId("notExistFlowInstanceId");
        commitTaskParam.setTaskInstanceId("test");
        commitTaskParam.setVariables(new ArrayList<>());
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.GET_FLOW_INSTANCE_FAILED.getErrNo());
    }

    // GET_NODE_FAILED
    // Cannot get StartEvent
    @Test
    public void error_4009() {
        RuntimeContext runtimeContext = new RuntimeContext();
        runtimeContext.setFlowElementMap(Maps.newHashMap());
        runtimeContext.setInstanceDataMap(Maps.newHashMap());
        int errNo = ErrorEnum.SUCCESS.getErrNo();
        try {
            flowExecutor.execute(runtimeContext);
        } catch (ProcessException e) {
            e.printStackTrace();
            errNo = e.getErrNo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue(errNo == ErrorEnum.GET_NODE_FAILED.getErrNo());
    }

    // GET_NODE_INSTANCE_FAILED
    @Test
    public void error_4010() throws Exception {
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId("notExistNodeInstanceId");
        commitTaskParam.setVariables(new ArrayList<>());
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.GET_NODE_INSTANCE_FAILED.getErrNo());
    }

    // GET_INSTANCE_DATA_FAILED(4011, "获取不到实例数据信息")
    @Test
    public void error_4011() {
        // depend on db data
    }

    // GET_HOOK_CONFIG_FAILED
    @Test
    public void error_4012() throws Exception {
        // first, config hookInfoIds in ExclusiveGateway node
        // second, set hook.url empty
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);

        // UserTask -> ExclusiveGateway -> UserTask
        CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
        Assert.assertTrue(commitTaskDTO.getErrCode() == ErrorEnum.GET_HOOK_CONFIG_FAILED.getErrNo());
    }

    // GET_OUTGOING_FAILED
    @Test
    public void error_4013() {
        int errNo = ErrorEnum.SUCCESS.getErrNo();
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
            errNo = e.getErrNo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue(errNo == ErrorEnum.GET_OUTGOING_FAILED.getErrNo());
    }

    // UNSUPPORTED_ELEMENT_TYPE
    @Test
    public void error_4014() {
        int errNo = ErrorEnum.SUCCESS.getErrNo();
        FlowElement unSupportedElement = new FlowElement();
        unSupportedElement.setType(100);
        try {
            ElementExecutor elementExecutor = executorFactory.getElementExecutor(unSupportedElement);
        } catch (ProcessException e) {
            e.printStackTrace();
            errNo = e.getErrNo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue(errNo == ErrorEnum.UNSUPPORTED_ELEMENT_TYPE.getErrNo());
    }

    // MISSING_DATA
    @Test
    public void error_4015() {
        int errNo = ErrorEnum.SUCCESS.getErrNo();
        try {
            GroovyUtil.calculate("orderId.equals(\"123\")", new HashMap<>());
        } catch (ProcessException e) {
            e.printStackTrace();
            errNo = e.getErrNo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue(errNo == ErrorEnum.MISSING_DATA.getErrNo());
    }

    // SAVE_FLOW_INSTANCE_FAILED
    @Test
    public void error_4016() { }

    // SAVE_INSTANCE_DATA_FAILED
    @Test
    public void error_4017() { }

    // GROOVY_CALCULATE_FAILED
    @Test
    public void error_4018() {
        int errNo = ErrorEnum.SUCCESS.getErrNo();
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
            errNo = e.getErrNo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // No such property: orderId for class: Script1
        Assert.assertTrue(errNo == ErrorEnum.GROOVY_CALCULATE_FAILED.getErrNo());
    }
}
