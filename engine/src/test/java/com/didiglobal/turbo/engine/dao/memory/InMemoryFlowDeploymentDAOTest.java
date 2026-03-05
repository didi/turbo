package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryFlowDeploymentDAOTest {

    private InMemoryFlowDeploymentDAO dao;

    @BeforeEach
    public void setUp() {
        dao = new InMemoryFlowDeploymentDAO();
    }

    @Test
    public void testInsert() {
        FlowDeploymentPO po = buildPO("deployId1", "moduleId1");
        int result = dao.insert(po);
        Assertions.assertEquals(1, result);
        Assertions.assertNotNull(po.getId());
    }

    @Test
    public void testInsertNullReturnsError() {
        Assertions.assertEquals(-1, dao.insert(null));
    }

    @Test
    public void testSelectByDeployId() {
        FlowDeploymentPO po = buildPO("deployId2", "moduleId2");
        dao.insert(po);
        FlowDeploymentPO found = dao.selectByDeployId("deployId2");
        Assertions.assertNotNull(found);
        Assertions.assertEquals("deployId2", found.getFlowDeployId());
    }

    @Test
    public void testSelectByDeployIdNotFound() {
        Assertions.assertNull(dao.selectByDeployId("nonExistent"));
    }

    @Test
    public void testSelectRecentByFlowModuleId() {
        FlowDeploymentPO po1 = buildPO("deployId3", "moduleId3");
        FlowDeploymentPO po2 = buildPO("deployId4", "moduleId3");
        dao.insert(po1);
        dao.insert(po2);

        FlowDeploymentPO recent = dao.selectRecentByFlowModuleId("moduleId3");
        Assertions.assertNotNull(recent);
        // po2 was inserted after po1 so it has a higher id
        Assertions.assertEquals("deployId4", recent.getFlowDeployId());
    }

    @Test
    public void testSelectRecentByFlowModuleIdNotFound() {
        Assertions.assertNull(dao.selectRecentByFlowModuleId("nonExistent"));
    }

    @Test
    public void testSelectRecentByFlowModuleIdNull() {
        Assertions.assertNull(dao.selectRecentByFlowModuleId(null));
    }

    private FlowDeploymentPO buildPO(String flowDeployId, String flowModuleId) {
        FlowDeploymentPO po = new FlowDeploymentPO();
        po.setFlowDeployId(flowDeployId);
        po.setFlowModuleId(flowModuleId);
        po.setFlowName("testFlow");
        po.setFlowKey("testKey");
        po.setFlowModel("{}");
        po.setStatus(1);
        return po;
    }
}
