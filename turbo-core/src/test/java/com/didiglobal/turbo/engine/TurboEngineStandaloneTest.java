package com.didiglobal.turbo.engine;

import com.alibaba.fastjson.JSON;
import com.didiglobal.turbo.engine.bo.NodeInstance;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.common.FlowElementType;
import com.didiglobal.turbo.engine.dao.memory.InMemoryFlowDefinitionDAO;
import com.didiglobal.turbo.engine.dao.memory.InMemoryFlowDeploymentDAO;
import com.didiglobal.turbo.engine.dao.memory.InMemoryFlowInstanceMappingDAO;
import com.didiglobal.turbo.engine.dao.memory.InMemoryInstanceDataDAO;
import com.didiglobal.turbo.engine.dao.memory.InMemoryNodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.memory.InMemoryNodeInstanceLogDAO;
import com.didiglobal.turbo.engine.dao.memory.InMemoryProcessInstanceDAO;
import com.didiglobal.turbo.engine.engine.ProcessEngine;
import com.didiglobal.turbo.engine.engine.TurboEngineBuilder;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.model.FlowModel;
import com.didiglobal.turbo.engine.param.CommitTaskParam;
import com.didiglobal.turbo.engine.param.CreateFlowParam;
import com.didiglobal.turbo.engine.param.DeployFlowParam;
import com.didiglobal.turbo.engine.param.StartProcessParam;
import com.didiglobal.turbo.engine.param.UpdateFlowParam;
import com.didiglobal.turbo.engine.result.CommitTaskResult;
import com.didiglobal.turbo.engine.result.CreateFlowResult;
import com.didiglobal.turbo.engine.result.DeployFlowResult;
import com.didiglobal.turbo.engine.result.StartProcessResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Pure Java end-to-end test — no Spring context required.
 *
 * <p>Demonstrates the complete Turbo workflow using in-memory DAO implementations
 * from the {@code com.didiglobal.turbo.engine.dao.memory} package:
 * <ol>
 *   <li>Build engine via {@link TurboEngineBuilder}</li>
 *   <li>Create a flow definition</li>
 *   <li>Update the flow with a model (StartEvent → UserTask → EndEvent)</li>
 *   <li>Deploy the flow</li>
 *   <li>Start a process instance</li>
 *   <li>Commit the suspended UserTask</li>
 * </ol>
 *
 * <p>This test serves as living documentation of the standalone integration path.
 * New integrators can use the {@code InMemoryXxxDAO} classes directly (or as a reference)
 * and adapt them to any persistence layer (JDBC, JPA, MyBatis, etc.) without Spring.
 */
public class TurboEngineStandaloneTest {

    private ProcessEngine engine;
    private InMemoryFlowDefinitionDAO flowDefinitionDAO;

    @Before
    public void setUp() {
        flowDefinitionDAO = new InMemoryFlowDefinitionDAO();

        engine = TurboEngineBuilder.create()
                .flowDefinitionDAO(flowDefinitionDAO)
                .flowDeploymentDAO(new InMemoryFlowDeploymentDAO())
                .processInstanceDAO(new InMemoryProcessInstanceDAO())
                .nodeInstanceDAO(new InMemoryNodeInstanceDAO())
                .instanceDataDAO(new InMemoryInstanceDataDAO())
                .flowInstanceMappingDAO(new InMemoryFlowInstanceMappingDAO())
                .nodeInstanceLogDAO(new InMemoryNodeInstanceLogDAO())
                .build();
    }

    @Test
    public void testBuildEngineWithoutSpring() {
        Assert.assertNotNull("Engine should be created without any IoC container", engine);
    }

    @Test
    public void testMissingDAOThrowsDescriptiveError() {
        try {
            TurboEngineBuilder.create()
                    .flowDefinitionDAO(flowDefinitionDAO)
                    // intentionally omit other required DAOs
                    .build();
            Assert.fail("Expected IllegalStateException for missing DAOs");
        } catch (IllegalStateException e) {
            Assert.assertTrue("Error message should name the missing DAO",
                    e.getMessage().contains("flowDeploymentDAO"));
        }
    }

    /**
     * Full workflow test: create → update (set model) → deploy → startProcess.
     *
     * <p>The flow model used here is:
     * <pre>
     *   StartEvent(e1) --seq1--> UserTask(ut1) --seq2--> EndEvent(e2)
     * </pre>
     */
    @Test
    public void testFullWorkflowCreateDeployAndStartProcess() {
        // --- Step 1: Create a flow definition ---
        CreateFlowParam createParam = new CreateFlowParam("test-tenant", "test-caller");
        createParam.setFlowName("Standalone Integration Test Flow");
        CreateFlowResult created = engine.createFlow(createParam);

        Assert.assertEquals("createFlow should succeed",
                ErrorEnum.SUCCESS.getErrNo(), created.getErrCode());
        Assert.assertNotNull("flowModuleId must be assigned", created.getFlowModuleId());
        String flowModuleId = created.getFlowModuleId();

        // --- Step 2: Set the flow model (StartEvent -> UserTask -> EndEvent) ---
        String flowModelJson = buildSimpleFlowModel();
        UpdateFlowParam updateParam = new UpdateFlowParam("test-tenant", "test-caller");
        updateParam.setFlowModuleId(flowModuleId);
        updateParam.setFlowModel(flowModelJson);
        engine.updateFlow(updateParam);

        // --- Step 3: Deploy the flow ---
        DeployFlowParam deployParam = new DeployFlowParam("test-tenant", "test-caller");
        deployParam.setFlowModuleId(flowModuleId);
        DeployFlowResult deployed = engine.deployFlow(deployParam);

        Assert.assertEquals("deployFlow should succeed",
                ErrorEnum.SUCCESS.getErrNo(), deployed.getErrCode());
        Assert.assertNotNull("flowDeployId must be assigned", deployed.getFlowDeployId());
        String flowDeployId = deployed.getFlowDeployId();

        // --- Step 4: Start a process instance ---
        StartProcessParam startParam = new StartProcessParam();
        startParam.setFlowDeployId(flowDeployId);
        StartProcessResult started = engine.startProcess(startParam);

        Assert.assertTrue("startProcess should succeed (reach UserTask or EndEvent)",
                ErrorEnum.isSuccess(started.getErrCode()));
        Assert.assertNotNull("flowInstanceId must be assigned", started.getFlowInstanceId());
        Assert.assertNotNull("activeTaskInstance should be the UserTask",
                started.getActiveTaskInstance());
        Assert.assertEquals("Active task should be the UserTask node",
                "ut1", started.getActiveTaskInstance().getModelKey());

        String flowInstanceId = started.getFlowInstanceId();
        String nodeInstanceId = started.getActiveTaskInstance().getNodeInstanceId();

        // --- Step 5: Commit the UserTask ---
        CommitTaskParam commitParam = new CommitTaskParam();
        commitParam.setFlowInstanceId(flowInstanceId);
        commitParam.setTaskInstanceId(nodeInstanceId);
        CommitTaskResult committed = engine.commitTask(commitParam);

        Assert.assertEquals("commitTask should succeed",
                ErrorEnum.SUCCESS.getErrNo(), committed.getErrCode());
        // After committing the UserTask, the flow reaches the EndEvent and completes.
        // The EndEvent is returned as the active task instance (indicating flow completion).
        NodeInstance activeTask = committed.getActiveTaskInstance();
        if (activeTask != null) {
            Assert.assertEquals("After commit, should reach EndEvent",
                    "e2", activeTask.getModelKey());
            Assert.assertEquals("EndEvent type should be END_EVENT",
                    FlowElementType.END_EVENT, activeTask.getFlowElementType());
        }
        // Either the process completed (null activeTask) or it reached EndEvent - both are valid end states
    }

    // ==================== Flow model builder ====================

    /**
     * Builds a minimal flow model JSON:
     * <pre>StartEvent(e1) --seq1--> UserTask(ut1) --seq2--> EndEvent(e2)</pre>
     *
     * <p>Structure follows the Turbo FlowElement model:
     * <ul>
     *   <li>Nodes (StartEvent, UserTask, EndEvent) have {@code outgoing}: list of outgoing sequence flow keys</li>
     *   <li>SequenceFlows have {@code incoming}: source node key, {@code outgoing}: target node key</li>
     * </ul>
     */
    private static String buildSimpleFlowModel() {
        List<FlowElement> elements = new ArrayList<>();

        // StartEvent: outgoing = [seq1]
        FlowElement startEvent = new FlowElement();
        startEvent.setKey("e1");
        startEvent.setType(FlowElementType.START_EVENT);
        startEvent.setOutgoing(Arrays.asList("seq1"));
        startEvent.setProperties(new java.util.HashMap<>());
        elements.add(startEvent);

        // SequenceFlow: seq1 (e1 → ut1)
        FlowElement seq1 = new FlowElement();
        seq1.setKey("seq1");
        seq1.setType(FlowElementType.SEQUENCE_FLOW);
        seq1.setIncoming(Arrays.asList("e1"));
        seq1.setOutgoing(Arrays.asList("ut1"));
        seq1.setProperties(new java.util.HashMap<>());
        elements.add(seq1);

        // UserTask: incoming = [seq1], outgoing = [seq2]
        FlowElement userTask = new FlowElement();
        userTask.setKey("ut1");
        userTask.setType(FlowElementType.USER_TASK);
        userTask.setIncoming(Arrays.asList("seq1"));
        userTask.setOutgoing(Arrays.asList("seq2"));
        userTask.setProperties(new java.util.HashMap<>());
        elements.add(userTask);

        // SequenceFlow: seq2 (ut1 → e2)
        FlowElement seq2 = new FlowElement();
        seq2.setKey("seq2");
        seq2.setType(FlowElementType.SEQUENCE_FLOW);
        seq2.setIncoming(Arrays.asList("ut1"));
        seq2.setOutgoing(Arrays.asList("e2"));
        seq2.setProperties(new java.util.HashMap<>());
        elements.add(seq2);

        // EndEvent: incoming = [seq2]
        FlowElement endEvent = new FlowElement();
        endEvent.setKey("e2");
        endEvent.setType(FlowElementType.END_EVENT);
        endEvent.setIncoming(Arrays.asList("seq2"));
        endEvent.setProperties(new java.util.HashMap<>());
        elements.add(endEvent);

        FlowModel model = new FlowModel();
        model.setFlowElementList(elements);
        return JSON.toJSONString(model);
    }
}

