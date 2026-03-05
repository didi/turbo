package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.common.FlowInstanceStatus;
import com.didiglobal.turbo.engine.entity.FlowInstancePO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryProcessInstanceDAOTest {

    private InMemoryProcessInstanceDAO dao;

    @BeforeEach
    public void setUp() {
        dao = new InMemoryProcessInstanceDAO();
    }

    @Test
    public void testInsert() {
        FlowInstancePO po = buildPO("instanceId1");
        int result = dao.insert(po);
        Assertions.assertEquals(1, result);
        Assertions.assertNotNull(po.getId());
    }

    @Test
    public void testInsertNullReturnsError() {
        Assertions.assertEquals(-1, dao.insert(null));
    }

    @Test
    public void testSelectByFlowInstanceId() {
        FlowInstancePO po = buildPO("instanceId2");
        dao.insert(po);
        FlowInstancePO found = dao.selectByFlowInstanceId("instanceId2");
        Assertions.assertNotNull(found);
        Assertions.assertEquals("instanceId2", found.getFlowInstanceId());
    }

    @Test
    public void testSelectByFlowInstanceIdNotFound() {
        Assertions.assertNull(dao.selectByFlowInstanceId("nonExistent"));
    }

    @Test
    public void testUpdateStatusByFlowInstancePO() {
        FlowInstancePO po = buildPO("instanceId3");
        dao.insert(po);
        dao.updateStatus(po, FlowInstanceStatus.COMPLETED);
        FlowInstancePO found = dao.selectByFlowInstanceId("instanceId3");
        Assertions.assertEquals(Integer.valueOf(FlowInstanceStatus.COMPLETED), found.getStatus());
        Assertions.assertNotNull(found.getModifyTime());
    }

    @Test
    public void testUpdateStatusByFlowInstanceId() {
        FlowInstancePO po = buildPO("instanceId4");
        dao.insert(po);
        dao.updateStatus("instanceId4", FlowInstanceStatus.RUNNING);
        FlowInstancePO found = dao.selectByFlowInstanceId("instanceId4");
        Assertions.assertEquals(Integer.valueOf(FlowInstanceStatus.RUNNING), found.getStatus());
    }

    @Test
    public void testUpdateStatusByFlowInstanceIdNotFound() {
        // Should not throw exception when instance not found
        dao.updateStatus("nonExistent", FlowInstanceStatus.COMPLETED);
    }

    private FlowInstancePO buildPO(String flowInstanceId) {
        FlowInstancePO po = new FlowInstancePO();
        po.setFlowInstanceId(flowInstanceId);
        po.setFlowDeployId("deployId1");
        po.setFlowModuleId("moduleId1");
        po.setStatus(FlowInstanceStatus.RUNNING);
        return po;
    }
}
