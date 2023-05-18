package com.didiglobal.turbo.engine.service;

import com.didiglobal.turbo.engine.common.FlowElementType;
import com.didiglobal.turbo.engine.dao.FlowDeploymentDAO;
import com.didiglobal.turbo.engine.dao.FlowInstanceMappingDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import com.didiglobal.turbo.engine.entity.FlowInstanceMappingPO;
import com.didiglobal.turbo.engine.entity.FlowInstancePO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.util.FlowModelUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

@Service
public class FlowInstanceService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FlowInstanceService.class);

    @Resource
    private NodeInstanceDAO nodeInstanceDAO;

    @Resource
    private FlowInstanceMappingDAO flowInstanceMappingDAO;

    @Resource
    private ProcessInstanceDAO processInstanceDAO;

    @Resource
    private FlowDeploymentDAO flowDeploymentDAO;

    /**
     * According to rootFlowInstanceId and commitNodeInstanceId, build and return NodeInstance stack.
     * When the subProcessInstance of each layer is executed, stack needs to pop up.
     * <p>
     * e.g.
     * <p>
     * rootNodeInstanceId
     * ^
     * ..................
     * ^
     * commitNodeInstanceId
     *
     * @param rootFlowInstanceId
     * @param commitNodeInstanceId
     * @return
     */
    public Stack<String> getNodeInstanceIdStack(String rootFlowInstanceId, String commitNodeInstanceId) {
        if (StringUtils.isBlank(commitNodeInstanceId)) {
            LOGGER.info("getNodeInstanceId2RootStack result is empty.||rootFlowInstanceId={}||commitNodeInstanceId={}", rootFlowInstanceId, commitNodeInstanceId);
            return new Stack<>();
        }
        FlowInstanceTreeResult flowInstanceTreeResult = buildFlowInstanceTree(rootFlowInstanceId,
            nodeInstancePO -> nodeInstancePO.getNodeInstanceId().equals(commitNodeInstanceId));
        NodeInstancePOJO rightNodeInstance = flowInstanceTreeResult.getInterruptNodeInstancePOJO();
        Stack<String> stack = new Stack<>();
        while (rightNodeInstance != null) {
            stack.push(rightNodeInstance.getId());
            rightNodeInstance = rightNodeInstance.getFlowInstance().getBelongNodeInstance();
        }
        LOGGER.info("getNodeInstanceId2RootStack result.||rootFlowInstanceId={}||commitNodeInstanceId={}||result={}", rootFlowInstanceId, commitNodeInstanceId, stack);
        return stack;
    }

    /**
     * According to rootFlowInstanceId, get all subFlowInstanceIds from db.
     *
     * @param rootFlowInstanceId
     * @return
     */
    public Set<String> getAllSubFlowInstanceIds(String rootFlowInstanceId) {
        FlowInstanceTreeResult flowInstanceTreeResult = buildFlowInstanceTree(rootFlowInstanceId, null);
        FlowInstancePOJO flowInstancePOJO = flowInstanceTreeResult.getRootFlowInstancePOJO();
        Set<String> result = getAllSubFlowInstanceIdsInternal(flowInstancePOJO);
        result.remove(rootFlowInstanceId);
        LOGGER.info("getAllSubFlowInstanceIds result.||rootFlowInstanceId={}||result={}", rootFlowInstanceId, result);
        return result;
    }

    private Set<String> getAllSubFlowInstanceIdsInternal(FlowInstancePOJO flowInstancePOJO) {
        Set<String> result = new TreeSet<>();
        if (flowInstancePOJO == null) {
            return result;
        }
        result.add(flowInstancePOJO.getId());
        List<NodeInstancePOJO> nodeInstanceList = flowInstancePOJO.getNodeInstanceList();
        for (NodeInstancePOJO nodeInstancePOJO : nodeInstanceList) {
            if (CollectionUtils.isEmpty(nodeInstancePOJO.getSubFlowInstanceList())) {
                continue;
            }
            FlowInstancePOJO subFlowInstancePOJO = nodeInstancePOJO.getSubFlowInstanceList().get(0);
            Set<String> subFlowInstanceResult = getAllSubFlowInstanceIdsInternal(subFlowInstancePOJO);
            result.addAll(subFlowInstanceResult);
        }
        return result;
    }


    /**
     * According to rootFlowInstanceId and nodeInstanceId,
     * Return the FlowInstanceId where the nodeInstanceId is located.
     *
     * @param rootFlowInstanceId
     * @param nodeInstanceId
     * @return
     */
    public String getFlowInstanceIdByRootFlowInstanceIdAndNodeInstanceId(String rootFlowInstanceId, String nodeInstanceId) {
        if (StringUtils.isBlank(nodeInstanceId)) {
            return StringUtils.EMPTY;
        }
        FlowInstanceTreeResult flowInstanceTreeResult = buildFlowInstanceTree(rootFlowInstanceId,
            nodeInstancePO -> nodeInstancePO.getNodeInstanceId().equals(nodeInstanceId));
        NodeInstancePOJO rightNodeInstance = flowInstanceTreeResult.getInterruptNodeInstancePOJO();
        if (rightNodeInstance == null) {
            return StringUtils.EMPTY;
        }
        return rightNodeInstance.getFlowInstance().getId();
    }

    /**
     * According to rootFlowInstanceId and instanceDataId,
     * Return the FlowInstanceId where the instanceDataId is located.
     *
     * @param rootFlowInstanceId
     * @param instanceDataId
     * @return
     */
    public String getFlowInstanceIdByRootFlowInstanceIdAndInstanceDataId(String rootFlowInstanceId, String instanceDataId) {
        if (StringUtils.isBlank(instanceDataId)) {
            return StringUtils.EMPTY;
        }
        FlowInstanceTreeResult flowInstanceTreeResult = buildFlowInstanceTree(rootFlowInstanceId,
            nodeInstancePO -> nodeInstancePO.getInstanceDataId().equals(instanceDataId));
        NodeInstancePOJO rightNodeInstance = flowInstanceTreeResult.getInterruptNodeInstancePOJO();
        if (rightNodeInstance == null) {
            return StringUtils.EMPTY;
        }
        return rightNodeInstance.getFlowInstance().getId();
    }

    // common : build a flowInstanceAndNodeInstance tree
    private FlowInstanceTreeResult buildFlowInstanceTree(String rootFlowInstanceId, InterruptCondition interruptCondition) {
        FlowInstanceTreeResult flowInstanceTreeResult = new FlowInstanceTreeResult();
        FlowInstancePOJO flowInstance = new FlowInstancePOJO();
        flowInstance.setId(rootFlowInstanceId);
        flowInstanceTreeResult.setRootFlowInstancePOJO(flowInstance);

        FlowInstancePO rootFlowInstancePO = processInstanceDAO.selectByFlowInstanceId(rootFlowInstanceId);
        FlowDeploymentPO rootFlowDeploymentPO = flowDeploymentDAO.selectByDeployId(rootFlowInstancePO.getFlowDeployId());
        Map<String, FlowElement> rootFlowElementMap = FlowModelUtil.getFlowElementMap(rootFlowDeploymentPO.getFlowModel());

        List<NodeInstancePO> nodeInstancePOList = nodeInstanceDAO.selectDescByFlowInstanceId(rootFlowInstanceId);
        for (NodeInstancePO nodeInstancePO : nodeInstancePOList) {
            NodeInstancePOJO nodeInstance = new NodeInstancePOJO();
            nodeInstance.setId(nodeInstancePO.getNodeInstanceId());
            nodeInstance.setFlowInstance(flowInstance);
            flowInstance.getNodeInstanceList().add(nodeInstance);

            if (interruptCondition != null && interruptCondition.match(nodeInstancePO)) {
                flowInstanceTreeResult.setInterruptNodeInstancePOJO(nodeInstance);
                return flowInstanceTreeResult;
            }

            int elementType = FlowModelUtil.getElementType(nodeInstancePO.getNodeKey(), rootFlowElementMap);
            if (elementType != FlowElementType.CALL_ACTIVITY) {
                continue;
            }
            List<FlowInstanceMappingPO> flowInstanceMappingPOS = flowInstanceMappingDAO.selectFlowInstanceMappingPOList(nodeInstancePO.getFlowInstanceId(), nodeInstancePO.getNodeInstanceId());
            for (FlowInstanceMappingPO flowInstanceMappingPO : flowInstanceMappingPOS) {
                FlowInstanceTreeResult subFlowInstanceTreeResult = buildFlowInstanceTree(flowInstanceMappingPO.getSubFlowInstanceId(), interruptCondition);
                FlowInstancePOJO subFlowInstance = subFlowInstanceTreeResult.getRootFlowInstancePOJO();
                subFlowInstance.setBelongNodeInstance(nodeInstance);
                nodeInstance.getSubFlowInstanceList().add(subFlowInstance);
                if (subFlowInstanceTreeResult.needInterrupt()) {
                    flowInstanceTreeResult.setInterruptNodeInstancePOJO(subFlowInstanceTreeResult.getInterruptNodeInstancePOJO());
                    return flowInstanceTreeResult;
                }
            }
        }
        return flowInstanceTreeResult;
    }

    private static class FlowInstancePOJO {
        private String id;
        private NodeInstancePOJO belongNodeInstance;
        private List<NodeInstancePOJO> nodeInstanceList = new ArrayList<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public NodeInstancePOJO getBelongNodeInstance() {
            return belongNodeInstance;
        }

        public void setBelongNodeInstance(NodeInstancePOJO belongNodeInstance) {
            this.belongNodeInstance = belongNodeInstance;
        }

        public List<NodeInstancePOJO> getNodeInstanceList() {
            return nodeInstanceList;
        }

        public void setNodeInstanceList(List<NodeInstancePOJO> nodeInstanceList) {
            this.nodeInstanceList = nodeInstanceList;
        }
    }

    private static class NodeInstancePOJO {

        private String id;
        private FlowInstancePOJO flowInstance;
        private List<FlowInstancePOJO> subFlowInstanceList = new ArrayList<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public FlowInstancePOJO getFlowInstance() {
            return flowInstance;
        }

        public void setFlowInstance(FlowInstancePOJO flowInstance) {
            this.flowInstance = flowInstance;
        }

        public List<FlowInstancePOJO> getSubFlowInstanceList() {
            return subFlowInstanceList;
        }

        public void setSubFlowInstanceList(List<FlowInstancePOJO> subFlowInstanceList) {
            this.subFlowInstanceList = subFlowInstanceList;
        }
    }

    private static class FlowInstanceTreeResult {
        private FlowInstancePOJO rootFlowInstancePOJO;
        private NodeInstancePOJO interruptNodeInstancePOJO;

        public FlowInstancePOJO getRootFlowInstancePOJO() {
            return rootFlowInstancePOJO;
        }

        public void setRootFlowInstancePOJO(FlowInstancePOJO rootFlowInstancePOJO) {
            this.rootFlowInstancePOJO = rootFlowInstancePOJO;
        }

        public NodeInstancePOJO getInterruptNodeInstancePOJO() {
            return interruptNodeInstancePOJO;
        }

        public void setInterruptNodeInstancePOJO(NodeInstancePOJO interruptNodeInstancePOJO) {
            this.interruptNodeInstancePOJO = interruptNodeInstancePOJO;
        }

        public boolean needInterrupt() {
            return interruptNodeInstancePOJO != null;
        }
    }

    /**
     * When build a flowInstanceAndNodeInstance tree,
     * we allow timely interruption to improve response.
     */
    private interface InterruptCondition {

        /**
         * Returns true when the condition is match
         *
         * @param nodeInstancePO
         * @return
         */
        boolean match(NodeInstancePO nodeInstancePO);
    }
}
