package com.didiglobal.turbo.engine;

import com.alibaba.fastjson.JSON;
import com.didiglobal.turbo.engine.bo.NodeInstance;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.common.FlowElementType;
import com.didiglobal.turbo.engine.common.NodeInstanceStatus;
import com.didiglobal.turbo.engine.dao.FlowDefinitionDAO;
import com.didiglobal.turbo.engine.dao.FlowDeploymentDAO;
import com.didiglobal.turbo.engine.dao.FlowInstanceMappingDAO;
import com.didiglobal.turbo.engine.dao.InstanceDataDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceLogDAO;
import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.engine.engine.ProcessEngine;
import com.didiglobal.turbo.engine.engine.TurboEngineBuilder;
import com.didiglobal.turbo.engine.entity.FlowDefinitionPO;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import com.didiglobal.turbo.engine.entity.FlowInstanceMappingPO;
import com.didiglobal.turbo.engine.entity.FlowInstancePO;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Pure Java end-to-end test — no Spring context required.
 *
 * <p>Demonstrates the complete Turbo workflow using in-memory DAO implementations:
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
 * New integrators can copy the in-memory DAO implementations and adapt them to any
 * persistence layer (JDBC, JPA, MyBatis, etc.) without Spring.
 */
public class TurboEngineStandaloneTest {

    private ProcessEngine engine;
    private InMemoryFlowDefinitionDAO flowDefinitionDAO;
    private InMemoryFlowDeploymentDAO flowDeploymentDAO;
    private InMemoryProcessInstanceDAO processInstanceDAO;
    private InMemoryNodeInstanceDAO nodeInstanceDAO;
    private InMemoryInstanceDataDAO instanceDataDAO;
    private InMemoryFlowInstanceMappingDAO flowInstanceMappingDAO;
    private InMemoryNodeInstanceLogDAO nodeInstanceLogDAO;

    @Before
    public void setUp() {
        flowDefinitionDAO = new InMemoryFlowDefinitionDAO();
        flowDeploymentDAO = new InMemoryFlowDeploymentDAO();
        processInstanceDAO = new InMemoryProcessInstanceDAO();
        nodeInstanceDAO = new InMemoryNodeInstanceDAO();
        instanceDataDAO = new InMemoryInstanceDataDAO();
        flowInstanceMappingDAO = new InMemoryFlowInstanceMappingDAO();
        nodeInstanceLogDAO = new InMemoryNodeInstanceLogDAO();

        engine = TurboEngineBuilder.create()
                .flowDefinitionDAO(flowDefinitionDAO)
                .flowDeploymentDAO(flowDeploymentDAO)
                .processInstanceDAO(processInstanceDAO)
                .nodeInstanceDAO(nodeInstanceDAO)
                .instanceDataDAO(instanceDataDAO)
                .flowInstanceMappingDAO(flowInstanceMappingDAO)
                .nodeInstanceLogDAO(nodeInstanceLogDAO)
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

    // ==================== In-memory DAO implementations ====================
    // These show exactly what a non-Spring integrator needs to implement.

    /** Simple in-memory store for flow definitions. */
    static class InMemoryFlowDefinitionDAO implements FlowDefinitionDAO {
        private final Map<String, FlowDefinitionPO> store = new ConcurrentHashMap<>();

        @Override
        public int insert(FlowDefinitionPO po) {
            store.put(po.getFlowModuleId(), po);
            return 1;
        }

        @Override
        public int updateByModuleId(FlowDefinitionPO po) {
            FlowDefinitionPO existing = store.get(po.getFlowModuleId());
            if (existing == null) return 0;
            if (po.getFlowModel() != null) existing.setFlowModel(po.getFlowModel());
            if (po.getFlowName() != null) existing.setFlowName(po.getFlowName());
            if (po.getFlowKey() != null) existing.setFlowKey(po.getFlowKey());
            if (po.getStatus() != null) existing.setStatus(po.getStatus());
            return 1;
        }

        @Override
        public FlowDefinitionPO selectByModuleId(String flowModuleId) {
            return store.get(flowModuleId);
        }
    }

    /** Simple in-memory store for flow deployments. */
    static class InMemoryFlowDeploymentDAO implements FlowDeploymentDAO {
        private final Map<String, FlowDeploymentPO> byDeployId = new ConcurrentHashMap<>();
        private final Map<String, FlowDeploymentPO> latestByModuleId = new ConcurrentHashMap<>();

        @Override
        public int insert(FlowDeploymentPO po) {
            byDeployId.put(po.getFlowDeployId(), po);
            latestByModuleId.put(po.getFlowModuleId(), po);
            return 1;
        }

        @Override
        public FlowDeploymentPO selectByDeployId(String flowDeployId) {
            return byDeployId.get(flowDeployId);
        }

        @Override
        public FlowDeploymentPO selectRecentByFlowModuleId(String flowModuleId) {
            return latestByModuleId.get(flowModuleId);
        }
    }

    /** Simple in-memory store for flow (process) instances. */
    static class InMemoryProcessInstanceDAO implements ProcessInstanceDAO {
        private final Map<String, FlowInstancePO> store = new ConcurrentHashMap<>();

        @Override
        public FlowInstancePO selectByFlowInstanceId(String flowInstanceId) {
            return store.get(flowInstanceId);
        }

        @Override
        public int insert(FlowInstancePO po) {
            store.put(po.getFlowInstanceId(), po);
            return 1;
        }

        @Override
        public void updateStatus(String flowInstanceId, int status) {
            FlowInstancePO po = store.get(flowInstanceId);
            if (po != null) po.setStatus(status);
        }

        @Override
        public void updateStatus(FlowInstancePO po, int status) {
            po.setStatus(status);
        }
    }

    /** Simple in-memory store for node instances. */
    static class InMemoryNodeInstanceDAO implements NodeInstanceDAO {
        private final AtomicLong idSeq = new AtomicLong(1);
        private final Map<String, NodeInstancePO> byNodeInstanceId = new ConcurrentHashMap<>();
        // flowInstanceId -> list of node instances
        private final Map<String, List<NodeInstancePO>> byFlowInstanceId = new ConcurrentHashMap<>();

        @Override
        public int insert(NodeInstancePO po) {
            if (po.getId() == null) po.setId(idSeq.getAndIncrement());
            byNodeInstanceId.put(key(po.getFlowInstanceId(), po.getNodeInstanceId()), po);
            byFlowInstanceId.computeIfAbsent(po.getFlowInstanceId(), k -> new ArrayList<>()).add(po);
            return 1;
        }

        @Override
        public boolean insertOrUpdateList(List<NodeInstancePO> list) {
            for (NodeInstancePO po : list) {
                if (po.getId() == null) {
                    insert(po);
                } else {
                    // Update existing entry: replace in byNodeInstanceId and update in-place in byFlowInstanceId
                    String nodeKey = key(po.getFlowInstanceId(), po.getNodeInstanceId());
                    NodeInstancePO existing = byNodeInstanceId.get(nodeKey);
                    if (existing != null) {
                        // Update fields on the existing object so references in byFlowInstanceId are also updated
                        existing.setStatus(po.getStatus());
                        existing.setInstanceDataId(po.getInstanceDataId());
                        existing.setSourceNodeInstanceId(po.getSourceNodeInstanceId());
                        existing.setSourceNodeKey(po.getSourceNodeKey());
                    } else {
                        byNodeInstanceId.put(nodeKey, po);
                        byFlowInstanceId.computeIfAbsent(po.getFlowInstanceId(), k -> new ArrayList<>()).add(po);
                    }
                }
            }
            return true;
        }

        @Override
        public NodeInstancePO selectByNodeInstanceId(String flowInstanceId, String nodeInstanceId) {
            return byNodeInstanceId.get(key(flowInstanceId, nodeInstanceId));
        }

        @Override
        public NodeInstancePO selectBySourceInstanceId(String flowInstanceId, String sourceNodeInstanceId, String nodeKey) {
            List<NodeInstancePO> list = byFlowInstanceId.getOrDefault(flowInstanceId, java.util.Collections.emptyList());
            return list.stream()
                    .filter(n -> sourceNodeInstanceId.equals(n.getSourceNodeInstanceId()) && nodeKey.equals(n.getNodeKey()))
                    .findFirst().orElse(null);
        }

        @Override
        public NodeInstancePO selectRecentOne(String flowInstanceId) {
            List<NodeInstancePO> list = byFlowInstanceId.getOrDefault(flowInstanceId, java.util.Collections.emptyList());
            return list.isEmpty() ? null : list.get(list.size() - 1);
        }

        @Override
        public NodeInstancePO selectRecentActiveOne(String flowInstanceId) {
            List<NodeInstancePO> list = new ArrayList<>(byFlowInstanceId.getOrDefault(flowInstanceId, java.util.Collections.emptyList()));
            for (int i = list.size() - 1; i >= 0; i--) {
                Integer status = list.get(i).getStatus();
                if (status != null && NodeInstanceStatus.ACTIVE == status) return list.get(i);
            }
            return null;
        }

        @Override
        public NodeInstancePO selectRecentCompletedOne(String flowInstanceId) {
            List<NodeInstancePO> list = new ArrayList<>(byFlowInstanceId.getOrDefault(flowInstanceId, java.util.Collections.emptyList()));
            for (int i = list.size() - 1; i >= 0; i--) {
                Integer status = list.get(i).getStatus();
                if (status != null && NodeInstanceStatus.COMPLETED == status) return list.get(i);
            }
            return null;
        }

        @Override
        public NodeInstancePO selectEnabledOne(String flowInstanceId) {
            NodeInstancePO active = selectRecentActiveOne(flowInstanceId);
            return active != null ? active : selectRecentCompletedOne(flowInstanceId);
        }

        @Override
        public List<NodeInstancePO> selectByFlowInstanceId(String flowInstanceId) {
            return new ArrayList<>(byFlowInstanceId.getOrDefault(flowInstanceId, java.util.Collections.emptyList()));
        }

        @Override
        public List<NodeInstancePO> selectDescByFlowInstanceId(String flowInstanceId) {
            List<NodeInstancePO> list = new ArrayList<>(byFlowInstanceId.getOrDefault(flowInstanceId, java.util.Collections.emptyList()));
            java.util.Collections.reverse(list);
            return list;
        }

        @Override
        public void updateStatus(NodeInstancePO po, int status) {
            po.setStatus(status);
        }

        @Override
        public List<NodeInstancePO> selectByFlowInstanceIdAndNodeKey(String flowInstanceId, String nodeKey) {
            return byFlowInstanceId.getOrDefault(flowInstanceId, java.util.Collections.emptyList()).stream()
                    .filter(n -> nodeKey.equals(n.getNodeKey()))
                    .collect(Collectors.toList());
        }

        private static String key(String flowInstanceId, String nodeInstanceId) {
            return flowInstanceId + ":" + nodeInstanceId;
        }
    }

    /** Simple in-memory store for instance data (process variables). */
    static class InMemoryInstanceDataDAO implements InstanceDataDAO {
        private final AtomicLong idSeq = new AtomicLong(1);
        private final Map<String, InstanceDataPO> store = new ConcurrentHashMap<>();

        @Override
        public InstanceDataPO select(String flowInstanceId, String instanceDataId) {
            return store.get(key(flowInstanceId, instanceDataId));
        }

        @Override
        public InstanceDataPO selectRecentOne(String flowInstanceId) {
            return store.values().stream()
                    .filter(p -> flowInstanceId.equals(p.getFlowInstanceId()))
                    .reduce((a, b) -> b) // last inserted
                    .orElse(null);
        }

        @Override
        public int insert(InstanceDataPO po) {
            if (po.getId() == null) po.setId(idSeq.getAndIncrement());
            store.put(key(po.getFlowInstanceId(), po.getInstanceDataId()), po);
            return 1;
        }

        @Override
        public int updateData(InstanceDataPO po) {
            store.put(key(po.getFlowInstanceId(), po.getInstanceDataId()), po);
            return 1;
        }

        @Override
        public int insertOrUpdate(InstanceDataPO po) {
            return po.getId() != null ? updateData(po) : insert(po);
        }

        private static String key(String flowInstanceId, String instanceDataId) {
            return flowInstanceId + ":" + instanceDataId;
        }
    }

    /** Simple in-memory store for flow instance mappings (used by CallActivity). */
    static class InMemoryFlowInstanceMappingDAO implements FlowInstanceMappingDAO {
        private final Map<String, List<FlowInstanceMappingPO>> store = new ConcurrentHashMap<>();

        @Override
        public List<FlowInstanceMappingPO> selectFlowInstanceMappingPOList(String flowInstanceId, String nodeInstanceId) {
            return store.getOrDefault(key(flowInstanceId, nodeInstanceId), java.util.Collections.emptyList());
        }

        @Override
        public FlowInstanceMappingPO selectFlowInstanceMappingPO(String flowInstanceId, String nodeInstanceId) {
            List<FlowInstanceMappingPO> list = selectFlowInstanceMappingPOList(flowInstanceId, nodeInstanceId);
            return list.isEmpty() ? null : list.get(list.size() - 1);
        }

        @Override
        public int insert(FlowInstanceMappingPO po) {
            store.computeIfAbsent(key(po.getFlowInstanceId(), po.getNodeInstanceId()), k -> new ArrayList<>()).add(po);
            return 1;
        }

        @Override
        public void updateType(String flowInstanceId, String nodeInstanceId, int type) {
            FlowInstanceMappingPO po = selectFlowInstanceMappingPO(flowInstanceId, nodeInstanceId);
            if (po != null) po.setType(type);
        }

        private static String key(String flowInstanceId, String nodeInstanceId) {
            return flowInstanceId + ":" + nodeInstanceId;
        }
    }

    /** Simple in-memory store for node instance logs. */
    static class InMemoryNodeInstanceLogDAO implements NodeInstanceLogDAO {
        private final List<NodeInstanceLogPO> logs = new ArrayList<>();

        @Override
        public int insert(NodeInstanceLogPO po) {
            logs.add(po);
            return 1;
        }

        @Override
        public boolean insertList(List<NodeInstanceLogPO> list) {
            logs.addAll(list);
            return true;
        }
    }
}
