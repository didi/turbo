package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.common.NodeInstanceStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class InMemoryNodeInstanceDAOTest {

    private InMemoryNodeInstanceDAO dao;

    @BeforeEach
    public void setUp() {
        dao = new InMemoryNodeInstanceDAO();
    }

    @Test
    public void testInsert() {
        NodeInstancePO po = buildPO("instanceId1", "nodeInstId1", NodeInstanceStatus.ACTIVE);
        int result = dao.insert(po);
        Assertions.assertEquals(1, result);
        Assertions.assertNotNull(po.getId());
    }

    @Test
    public void testSelectByNodeInstanceId() {
        NodeInstancePO po = buildPO("instanceId2", "nodeInstId2", NodeInstanceStatus.ACTIVE);
        dao.insert(po);
        NodeInstancePO found = dao.selectByNodeInstanceId("instanceId2", "nodeInstId2");
        Assertions.assertNotNull(found);
        Assertions.assertEquals("nodeInstId2", found.getNodeInstanceId());
    }

    @Test
    public void testSelectRecentOne() {
        NodeInstancePO po1 = buildPO("instanceId3", "nodeInstId3a", NodeInstanceStatus.COMPLETED);
        NodeInstancePO po2 = buildPO("instanceId3", "nodeInstId3b", NodeInstanceStatus.ACTIVE);
        dao.insert(po1);
        dao.insert(po2);
        NodeInstancePO recent = dao.selectRecentOne("instanceId3");
        Assertions.assertNotNull(recent);
        Assertions.assertEquals("nodeInstId3b", recent.getNodeInstanceId());
    }

    @Test
    public void testSelectRecentActiveOne() {
        NodeInstancePO po1 = buildPO("instanceId4", "nodeInstId4a", NodeInstanceStatus.COMPLETED);
        NodeInstancePO po2 = buildPO("instanceId4", "nodeInstId4b", NodeInstanceStatus.ACTIVE);
        dao.insert(po1);
        dao.insert(po2);
        NodeInstancePO active = dao.selectRecentActiveOne("instanceId4");
        Assertions.assertNotNull(active);
        Assertions.assertEquals("nodeInstId4b", active.getNodeInstanceId());
    }

    @Test
    public void testSelectEnabledOne_prefersActive() {
        NodeInstancePO po1 = buildPO("instanceId5", "nodeInstId5a", NodeInstanceStatus.COMPLETED);
        NodeInstancePO po2 = buildPO("instanceId5", "nodeInstId5b", NodeInstanceStatus.ACTIVE);
        dao.insert(po1);
        dao.insert(po2);
        NodeInstancePO enabled = dao.selectEnabledOne("instanceId5");
        Assertions.assertNotNull(enabled);
        Assertions.assertEquals("nodeInstId5b", enabled.getNodeInstanceId());
    }

    @Test
    public void testSelectEnabledOne_fallsBackToCompleted() {
        NodeInstancePO po1 = buildPO("instanceId6", "nodeInstId6a", NodeInstanceStatus.COMPLETED);
        dao.insert(po1);
        NodeInstancePO enabled = dao.selectEnabledOne("instanceId6");
        Assertions.assertNotNull(enabled);
        Assertions.assertEquals("nodeInstId6a", enabled.getNodeInstanceId());
    }

    @Test
    public void testSelectByFlowInstanceId() {
        NodeInstancePO po1 = buildPO("instanceId7", "nodeInstId7a", NodeInstanceStatus.COMPLETED);
        NodeInstancePO po2 = buildPO("instanceId7", "nodeInstId7b", NodeInstanceStatus.ACTIVE);
        dao.insert(po1);
        dao.insert(po2);
        List<NodeInstancePO> list = dao.selectByFlowInstanceId("instanceId7");
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("nodeInstId7a", list.get(0).getNodeInstanceId());
        Assertions.assertEquals("nodeInstId7b", list.get(1).getNodeInstanceId());
    }

    @Test
    public void testSelectDescByFlowInstanceId() {
        NodeInstancePO po1 = buildPO("instanceId8", "nodeInstId8a", NodeInstanceStatus.COMPLETED);
        NodeInstancePO po2 = buildPO("instanceId8", "nodeInstId8b", NodeInstanceStatus.ACTIVE);
        dao.insert(po1);
        dao.insert(po2);
        List<NodeInstancePO> list = dao.selectDescByFlowInstanceId("instanceId8");
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("nodeInstId8b", list.get(0).getNodeInstanceId());
        Assertions.assertEquals("nodeInstId8a", list.get(1).getNodeInstanceId());
    }

    @Test
    public void testUpdateStatus() {
        NodeInstancePO po = buildPO("instanceId9", "nodeInstId9", NodeInstanceStatus.ACTIVE);
        dao.insert(po);
        dao.updateStatus(po, NodeInstanceStatus.COMPLETED);
        Assertions.assertEquals(NodeInstanceStatus.COMPLETED, (int) po.getStatus());
        Assertions.assertNotNull(po.getModifyTime());
    }

    @Test
    public void testInsertOrUpdateList() {
        NodeInstancePO po1 = buildPO("instanceId10", "nodeInstId10a", NodeInstanceStatus.ACTIVE);
        NodeInstancePO po2 = buildPO("instanceId10", "nodeInstId10b", NodeInstanceStatus.ACTIVE);
        dao.insert(po1);
        po2.setStatus(NodeInstanceStatus.ACTIVE);
        boolean result = dao.insertOrUpdateList(Arrays.asList(po2, new NodeInstancePO()));
        Assertions.assertTrue(result);
    }

    @Test
    public void testInsertOrUpdateListEmpty() {
        Assertions.assertTrue(dao.insertOrUpdateList(null));
    }

    @Test
    public void testUpdateById() {
        NodeInstancePO po = buildPO("instanceId11", "nodeInstId11", NodeInstanceStatus.ACTIVE);
        dao.insert(po);
        po.setStatus(NodeInstanceStatus.COMPLETED);
        boolean updated = dao.updateById(po);
        Assertions.assertTrue(updated);
        NodeInstancePO found = dao.selectByNodeInstanceId("instanceId11", "nodeInstId11");
        Assertions.assertEquals(NodeInstanceStatus.COMPLETED, (int) found.getStatus());
    }

    @Test
    public void testSelectByFlowInstanceIdAndNodeKey() {
        NodeInstancePO po1 = buildPO("instanceId12", "nodeInstId12a", NodeInstanceStatus.ACTIVE);
        po1.setNodeKey("nodeKey1");
        NodeInstancePO po2 = buildPO("instanceId12", "nodeInstId12b", NodeInstanceStatus.COMPLETED);
        po2.setNodeKey("nodeKey1");
        NodeInstancePO po3 = buildPO("instanceId12", "nodeInstId12c", NodeInstanceStatus.ACTIVE);
        po3.setNodeKey("nodeKey2");
        dao.insert(po1);
        dao.insert(po2);
        dao.insert(po3);
        List<NodeInstancePO> list = dao.selectByFlowInstanceIdAndNodeKey("instanceId12", "nodeKey1");
        Assertions.assertEquals(2, list.size());
    }

    private NodeInstancePO buildPO(String flowInstanceId, String nodeInstanceId, int status) {
        NodeInstancePO po = new NodeInstancePO();
        po.setFlowInstanceId(flowInstanceId);
        po.setNodeInstanceId(nodeInstanceId);
        po.setFlowDeployId("deployId1");
        po.setNodeKey("nodeKey1");
        po.setStatus(status);
        return po;
    }
}
