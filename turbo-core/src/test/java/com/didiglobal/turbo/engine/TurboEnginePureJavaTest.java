package com.didiglobal.turbo.engine;

import com.didiglobal.turbo.engine.common.ErrorEnum;
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
import com.didiglobal.turbo.engine.param.CreateFlowParam;
import com.didiglobal.turbo.engine.result.CreateFlowResult;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Pure Java test - no Spring context required.
 * Validates that TurboEngineBuilder correctly wires the engine for standalone use.
 */
public class TurboEnginePureJavaTest {

    /**
     * Build an engine with stub DAOs and validate that the engine can be created
     * and that basic parameter validation works without any IoC container.
     */
    @Test
    public void testEngineCreatesWithoutSpring() {
        ProcessEngine engine = TurboEngineBuilder.create()
                .flowDefinitionDAO(noopFlowDefinitionDAO())
                .flowDeploymentDAO(noopFlowDeploymentDAO())
                .processInstanceDAO(noopProcessInstanceDAO())
                .nodeInstanceDAO(noopNodeInstanceDAO())
                .instanceDataDAO(noopInstanceDataDAO())
                .flowInstanceMappingDAO(noopFlowInstanceMappingDAO())
                .nodeInstanceLogDAO(noopNodeInstanceLogDAO())
                .build();

        Assert.assertNotNull("Engine should be created without Spring", engine);
    }

    /**
     * Test that the engine correctly validates invalid parameters (no Spring needed).
     */
    @Test
    public void testCreateFlowParamValidation() {
        ProcessEngine engine = TurboEngineBuilder.create()
                .flowDefinitionDAO(noopFlowDefinitionDAO())
                .flowDeploymentDAO(noopFlowDeploymentDAO())
                .processInstanceDAO(noopProcessInstanceDAO())
                .nodeInstanceDAO(noopNodeInstanceDAO())
                .instanceDataDAO(noopInstanceDataDAO())
                .flowInstanceMappingDAO(noopFlowInstanceMappingDAO())
                .nodeInstanceLogDAO(noopNodeInstanceLogDAO())
                .build();

        // createFlow with valid params returns success (stub DAO returns 1 for insert)
        CreateFlowParam param = new CreateFlowParam("test-tenant", "test-caller");
        param.setFlowName("MyTestFlow");
        CreateFlowResult result = engine.createFlow(param);
        Assert.assertNotNull("Result should not be null", result);
        Assert.assertEquals("createFlow with stubs should succeed",
                ErrorEnum.SUCCESS.getErrNo(), result.getErrCode());
        Assert.assertNotNull("FlowModuleId should be populated", result.getFlowModuleId());
    }

    // ==================== Stub DAO implementations ====================

    private FlowDefinitionDAO noopFlowDefinitionDAO() {
        return new FlowDefinitionDAO() {
            @Override
            public int insert(FlowDefinitionPO po) { return 1; }
            @Override
            public int updateByModuleId(FlowDefinitionPO po) { return 1; }
            @Override
            public FlowDefinitionPO selectByModuleId(String flowModuleId) { return null; }
        };
    }

    private FlowDeploymentDAO noopFlowDeploymentDAO() {
        return new FlowDeploymentDAO() {
            @Override
            public int insert(FlowDeploymentPO po) { return 1; }
            @Override
            public FlowDeploymentPO selectByDeployId(String flowDeployId) { return null; }
            @Override
            public FlowDeploymentPO selectRecentByFlowModuleId(String flowModuleId) { return null; }
        };
    }

    private ProcessInstanceDAO noopProcessInstanceDAO() {
        return new ProcessInstanceDAO() {
            @Override
            public FlowInstancePO selectByFlowInstanceId(String flowInstanceId) { return null; }
            @Override
            public int insert(FlowInstancePO po) { return 1; }
            @Override
            public void updateStatus(String flowInstanceId, int status) {}
            @Override
            public void updateStatus(FlowInstancePO po, int status) {}
        };
    }

    private NodeInstanceDAO noopNodeInstanceDAO() {
        return new NodeInstanceDAO() {
            @Override
            public int insert(NodeInstancePO po) { return 1; }
            @Override
            public boolean insertOrUpdateList(List<NodeInstancePO> list) { return true; }
            @Override
            public NodeInstancePO selectByNodeInstanceId(String flowInstanceId, String nodeInstanceId) { return null; }
            @Override
            public NodeInstancePO selectBySourceInstanceId(String flowInstanceId, String sourceNodeInstanceId, String nodeKey) { return null; }
            @Override
            public NodeInstancePO selectRecentOne(String flowInstanceId) { return null; }
            @Override
            public NodeInstancePO selectRecentActiveOne(String flowInstanceId) { return null; }
            @Override
            public NodeInstancePO selectRecentCompletedOne(String flowInstanceId) { return null; }
            @Override
            public NodeInstancePO selectEnabledOne(String flowInstanceId) { return null; }
            @Override
            public List<NodeInstancePO> selectByFlowInstanceId(String flowInstanceId) { return List.of(); }
            @Override
            public List<NodeInstancePO> selectDescByFlowInstanceId(String flowInstanceId) { return List.of(); }
            @Override
            public void updateStatus(NodeInstancePO po, int status) {}
            @Override
            public List<NodeInstancePO> selectByFlowInstanceIdAndNodeKey(String flowInstanceId, String nodeKey) { return List.of(); }
        };
    }

    private InstanceDataDAO noopInstanceDataDAO() {
        return new InstanceDataDAO() {
            @Override
            public InstanceDataPO select(String flowInstanceId, String instanceDataId) { return null; }
            @Override
            public InstanceDataPO selectRecentOne(String flowInstanceId) { return null; }
            @Override
            public int insert(InstanceDataPO po) { return 1; }
            @Override
            public int updateData(InstanceDataPO po) { return 1; }
            @Override
            public int insertOrUpdate(InstanceDataPO po) { return 1; }
        };
    }

    private FlowInstanceMappingDAO noopFlowInstanceMappingDAO() {
        return new FlowInstanceMappingDAO() {
            @Override
            public List<FlowInstanceMappingPO> selectFlowInstanceMappingPOList(String flowInstanceId, String nodeInstanceId) { return List.of(); }
            @Override
            public FlowInstanceMappingPO selectFlowInstanceMappingPO(String flowInstanceId, String nodeInstanceId) { return null; }
            @Override
            public int insert(FlowInstanceMappingPO po) { return 1; }
            @Override
            public void updateType(String flowInstanceId, String nodeInstanceId, int type) {}
        };
    }

    private NodeInstanceLogDAO noopNodeInstanceLogDAO() {
        return new NodeInstanceLogDAO() {
            @Override
            public int insert(NodeInstanceLogPO po) { return 1; }
            @Override
            public boolean insertList(List<NodeInstanceLogPO> list) { return true; }
        };
    }
}
