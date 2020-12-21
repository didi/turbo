package com.xiaoju.uemc.turbo.engine.dao;

import com.xiaoju.uemc.turbo.engine.common.NodeInstanceStatus;
import com.xiaoju.uemc.turbo.engine.entity.NodeInstancePO;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefanie on 2020/11/20.
 */
public class NodeInstanceDAOTest extends BaseTest {

    @Resource
    private NodeInstanceDAO nodeInstanceDAO;

    @Test
    public void insert(){
        NodeInstancePO nodeInstancePO = EntityBuilder.buildNodeInstancePO();
        int result = nodeInstanceDAO.insert(nodeInstancePO);
        Assert.assertTrue(result == 1);
    }

    @Test
    public void insertOrUpdateList(){
        NodeInstancePO nodeInstancePO = EntityBuilder.buildNodeInstancePO();
        nodeInstanceDAO.insert(nodeInstancePO);
        nodeInstancePO = nodeInstanceDAO.selectByNodeInstanceId(nodeInstancePO.getFlowInstanceId(), nodeInstancePO.getNodeInstanceId());
        List<NodeInstancePO> nodeInstancePOList = new ArrayList<>();
        nodeInstancePO.setStatus(NodeInstanceStatus.COMPLETED); // change something
        nodeInstancePOList.add(nodeInstancePO); // update
        nodeInstancePOList.add(EntityBuilder.buildDynamicNodeInstancePO()); // batch insert
        nodeInstancePOList.add(EntityBuilder.buildDynamicNodeInstancePO()); // batch insert
        nodeInstanceDAO.insertOrUpdateList(nodeInstancePOList);
    }

    @Test
    public void updateStatus() {
        NodeInstancePO nodeInstancePO = EntityBuilder.buildNodeInstancePO();
        nodeInstanceDAO.insert(nodeInstancePO);

        nodeInstanceDAO.updateStatus(nodeInstancePO, NodeInstanceStatus.COMPLETED);
        NodeInstancePO result = nodeInstanceDAO.selectByNodeInstanceId(nodeInstancePO.getFlowInstanceId(), nodeInstancePO.getNodeInstanceId());
        Assert.assertTrue(result.getStatus() == NodeInstanceStatus.COMPLETED);
    }
}
