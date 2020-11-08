package com.xiaoju.uemc.turbo.engine.validator;

import com.didiglobal.reportlogger.LoggerFactory;
import com.didiglobal.reportlogger.ReportLogger;
import com.xiaoju.uemc.turbo.engine.common.Constants;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.common.FlowElementType;
import com.xiaoju.uemc.turbo.engine.exception.ModelException;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.model.FlowModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.text.MessageFormat;
import java.util.*;

/**
 * 项目名称：optimus-prime
 * 类 名 称：FlowModelValidator
 * 类 描 述：
 * 创建时间：2019/12/9 11:00 AM
 * 创 建 人：didiwangxing
 */
public class FlowModelValidator {

    protected static final ReportLogger LOGGER = LoggerFactory.getLogger(FlowModelValidator.class);

    private static int visitedNum = 0;

    public static void validate(FlowModel flowModel) throws ModelException {

        List<FlowElement> flowElementList = flowModel.getFlowElementList();

        int startEventNum = 0;
        int endEventNum = 0;
        // 构建图结构
        Graph graph = new Graph();
        Map<String, GraphElement> elementMap = graph.getElementMap();
        String startNodeKey = null;

        for (FlowElement flowElement : flowElementList) {

            String elementName = (String) flowElement.getProperties().getOrDefault("name", StringUtils.EMPTY);
            String elementKey = flowElement.getKey();

            if (elementMap.containsKey(elementKey)) {
                String exceptionMsg = MessageFormat.format(Constants.MODEL_DEFINITION_ERROR_MSG_FORMAT,
                        ErrorEnum.ELEMENT_KEY_NOT_UNIQUE.getErrMsg(), elementName, elementKey);
                LOGGER.error(exceptionMsg);
                throw new ModelException(ErrorEnum.ELEMENT_KEY_NOT_UNIQUE.getErrNo(), exceptionMsg);
            } else {
                // 构建图元素(包括边和点)
                GraphElement graphElement = new GraphElement();
                BeanUtils.copyProperties(flowElement, graphElement);
                graphElement.setVisited(false);
                graphElement.setName(elementName);  //  可能为空，尤其在测试流程里
                elementMap.put(elementKey, graphElement);
            }

            // 开始节点
            if (FlowElementType.START_EVENT == flowElement.getType()) {
                // 开始节点计数
                startEventNum++;
                // 有且仅有一个开始节点
                if (startEventNum != 1) {
                    LOGGER.error(ErrorEnum.MODEL_MUST_ONE_START_NODE.getErrMsg());
                    throw new ModelException(ErrorEnum.MODEL_MUST_ONE_START_NODE);
                }
                // 获取开始节点的key
                startNodeKey = flowElement.getKey();
            }
            // 结束节点
            if (FlowElementType.END_EVENT == flowElement.getType()) {
                // 结束节点计数
                endEventNum++;
            }
        }

        // 至少有一个结束节点
        if (endEventNum < 1) {
            LOGGER.error(ErrorEnum.MODEL_NO_END_NODE.getErrMsg());
            throw new ModelException(ErrorEnum.MODEL_NO_END_NODE);
        }
        // 添加边，构造图邻接表
        addEdge(graph);
        //
        GraphElement startNode = graph.getElementMap().get(startNodeKey);
        // 开始节点可达任意节点
        if (!isUnicomGraph(graph, startNode)) {
            LOGGER.error(ErrorEnum.MODEL_NOT_UNICOM.getErrMsg());
            throw new ModelException(ErrorEnum.MODEL_NOT_UNICOM);
        }
        // 环校验 校验不通过内部抛异常
//        ringCheck(graph, startNode);  //暂时关掉环检测
    }

    private static boolean isUnicomGraph(Graph graph, GraphElement startNode) {
        visitedNum = 0;
        System.out.print("\n dfs:");
        DFS(graph, startNode);
        LOGGER.info("visitedNum={}||graphNodeNum={}", visitedNum, graph.getNodeNum());
        if (visitedNum == graph.getNodeNum()) {
            return true;
        }
        return false;
    }

    // 添加边
    private static void addEdge(Graph graph) throws ModelException {

        Map<String, GraphElement> elementMap = graph.getElementMap();

        for (String key : elementMap.keySet()) {
            GraphElement graphElement = elementMap.get(key);

            if (graphElement.getType() != FlowElementType.SEQUENCE_FLOW) {
                graph.setNodeNum(graph.getNodeNum() + 1);
            } else {
                // 边的incoming和outgoing均唯一
                List<String> incoming = graphElement.getIncoming();
                List<String> outgoing = graphElement.getOutgoing();
                if (incoming.size() != 1 || outgoing.size() != 1) {
                    String exceptionMsg = MessageFormat.format(Constants.MODEL_DEFINITION_ERROR_MSG_FORMAT,
                            ErrorEnum.EDGE_BELONG_TO_MULTI_PAIR_NODE.getErrMsg(), graphElement.getName(), graphElement.getKey());
                    LOGGER.error(exceptionMsg);
                    throw new ModelException(ErrorEnum.EDGE_BELONG_TO_MULTI_PAIR_NODE.getErrNo(), exceptionMsg);
                }

                GraphElement incomingNode = graph.getElementMap().get(incoming.get(0));
                GraphElement outgoingNode = graph.getElementMap().get(outgoing.get(0));

                Map<String, List<GraphElement>> adjacencyMap = graph.getAdjacencyMap();
                List<GraphElement> nodeList = adjacencyMap.get(incomingNode.getKey());

                if (nodeList == null) {
                    nodeList = new ArrayList<>();
                    adjacencyMap.put(incomingNode.getKey(), nodeList);
                }

                nodeList.add(outgoingNode);
            }
        }
    }

    private static void ringCheck(Graph graph, GraphElement node) throws ModelException {
        Integer index = graph.getTraceMap().get(node.getKey());
        if (index != null && index >= 0) {
            int userTaskNum = 0;
            // debug
            System.out.print("\n ring:");
            List<String> traceList = graph.getTraceList();
            for (int i = index; i < traceList.size(); i++) {
                String key = graph.getTraceList().get(i);
                GraphElement graphElement = graph.getElementMap().get(key);
                String name = graphElement.getName();

                System.out.print(name + "->");

                if (graphElement.getType() == FlowElementType.USER_TASK) {
                    userTaskNum++;
                }
            }
            if (userTaskNum == 0) {
                String exceptionMsg = ErrorEnum.RING_WRONG.getErrMsg();
                LOGGER.error(exceptionMsg);
                throw new ModelException(ErrorEnum.RING_WRONG);
            }
        } else {
            List<String> traceList = graph.getTraceList();
            traceList.add(node.getKey());
            graph.getTraceMap().put(node.getKey(), traceList.size() - 1);

            List<GraphElement> nodeList = graph.getAdjacencyMap().get(node.getKey());
            if (CollectionUtils.isNotEmpty(nodeList)) {
                for (GraphElement n : nodeList) {
                    ringCheck(graph, n);
                }
            }

            int lastIndex = graph.getTraceList().size() - 1;
            String key = graph.getTraceList().get(lastIndex);
            graph.getTraceMap().remove(key);
            graph.getTraceList().remove(lastIndex);
        }
    }

    private static void DFS(Graph graph, GraphElement node) {
        if (!node.isVisited()) {
            node.setVisited(true);
            visitedNum++;
            System.out.print(node.getName() + "->");

            List<GraphElement> nodeList = graph.getAdjacencyMap().get(node.getKey());
            if (CollectionUtils.isNotEmpty(nodeList)) {
                for (GraphElement n : nodeList) {
                    DFS(graph, n);
                }
            }
        }
    }

    private static class Graph {
        private Map<String, GraphElement> elementMap = new HashMap<>();
        private Map<String, List<GraphElement>> adjacencyMap = new HashMap<>();
        private List<String> traceList = new ArrayList<>();
        // 存放key和key在traceList里的位置
        private Map<String, Integer> traceMap = new LinkedHashMap<>();

        private int nodeNum = 0;

        public Map<String, GraphElement> getElementMap() {
            return elementMap;
        }

        public Map<String, List<GraphElement>> getAdjacencyMap() {
            return adjacencyMap;
        }

        public List<String> getTraceList() {
            return traceList;
        }

        public int getNodeNum() {
            return nodeNum;
        }

        public void setElementMap(Map<String, GraphElement> elementMap) {
            this.elementMap = elementMap;
        }

        public void setAdjacencyMap(Map<String, List<GraphElement>> adjacencyMap) {
            this.adjacencyMap = adjacencyMap;
        }

        public void setTraceList(List<String> traceList) {
            this.traceList = traceList;
        }

        public void setNodeNum(int nodeNum) {
            this.nodeNum = nodeNum;
        }

        public Map<String, Integer> getTraceMap() {
            return traceMap;
        }

        public void setTraceMap(Map<String, Integer> traceMap) {
            this.traceMap = traceMap;
        }
    }

    private static class GraphElement extends FlowElement {
        private boolean visited;
        private String name;

        public boolean isVisited() {
            return visited;
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
