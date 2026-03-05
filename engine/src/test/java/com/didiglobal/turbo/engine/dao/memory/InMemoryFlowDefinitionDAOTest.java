package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.entity.FlowDefinitionPO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryFlowDefinitionDAOTest {

    private InMemoryFlowDefinitionDAO dao;

    @BeforeEach
    public void setUp() {
        dao = new InMemoryFlowDefinitionDAO();
    }

    @Test
    public void testInsert() {
        FlowDefinitionPO po = buildPO("moduleId1");
        int result = dao.insert(po);
        Assertions.assertEquals(1, result);
        Assertions.assertNotNull(po.getId());
    }

    @Test
    public void testInsertNullReturnsError() {
        Assertions.assertEquals(-1, dao.insert(null));
    }

    @Test
    public void testSelectByModuleId() {
        FlowDefinitionPO po = buildPO("moduleId2");
        dao.insert(po);
        FlowDefinitionPO found = dao.selectByModuleId("moduleId2");
        Assertions.assertNotNull(found);
        Assertions.assertEquals("moduleId2", found.getFlowModuleId());
    }

    @Test
    public void testSelectByModuleIdNotFound() {
        Assertions.assertNull(dao.selectByModuleId("nonExistent"));
    }

    @Test
    public void testSelectByModuleIdNull() {
        Assertions.assertNull(dao.selectByModuleId(null));
    }

    @Test
    public void testUpdateByModuleId() {
        FlowDefinitionPO po = buildPO("moduleId3");
        dao.insert(po);

        FlowDefinitionPO update = new FlowDefinitionPO();
        update.setFlowModuleId("moduleId3");
        update.setFlowName("updatedName");
        update.setFlowModel("updatedModel");
        int result = dao.updateByModuleId(update);

        Assertions.assertEquals(1, result);
        FlowDefinitionPO found = dao.selectByModuleId("moduleId3");
        Assertions.assertEquals("updatedName", found.getFlowName());
        Assertions.assertEquals("updatedModel", found.getFlowModel());
    }

    @Test
    public void testUpdateByModuleIdNotFound() {
        FlowDefinitionPO update = new FlowDefinitionPO();
        update.setFlowModuleId("nonExistent");
        Assertions.assertEquals(0, dao.updateByModuleId(update));
    }

    @Test
    public void testUpdateByModuleIdNull() {
        Assertions.assertEquals(-1, dao.updateByModuleId(null));
    }

    @Test
    public void testUpdateByModuleIdOnlyUpdatesNonNullFields() {
        FlowDefinitionPO po = buildPO("moduleId4");
        po.setFlowKey("originalKey");
        po.setStatus(1);
        dao.insert(po);

        FlowDefinitionPO update = new FlowDefinitionPO();
        update.setFlowModuleId("moduleId4");
        update.setFlowName("newName");
        // flowKey and status not set (null) — should remain unchanged
        dao.updateByModuleId(update);

        FlowDefinitionPO found = dao.selectByModuleId("moduleId4");
        Assertions.assertEquals("newName", found.getFlowName());
        Assertions.assertEquals("originalKey", found.getFlowKey());
        Assertions.assertEquals(Integer.valueOf(1), found.getStatus());
    }

    private FlowDefinitionPO buildPO(String flowModuleId) {
        FlowDefinitionPO po = new FlowDefinitionPO();
        po.setFlowModuleId(flowModuleId);
        po.setFlowName("testFlow");
        po.setFlowKey("testKey");
        po.setFlowModel("{}");
        po.setStatus(0);
        return po;
    }
}
