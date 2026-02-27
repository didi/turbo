package com.didiglobal.turbo.engine.plugin;

import com.didiglobal.turbo.engine.bo.ElementInstance;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.model.FlowElement;

import java.util.List;
import java.util.Map;

/**
 * 节点来源解析器接口。
 * <p>
 * 用于自定义历史元素展示中 source sequence flow 的解析逻辑。
 * 插件可实现此接口来处理特殊的节点类型（如并行网关的 join 节点有多个来源）。
 * <p>
 * 如果没有任何插件处理某个节点，engine 将使用默认的单一来源解析逻辑。
 */
public interface SourceNodeResolver {

    /**
     * 判断该解析器是否支持处理给定的节点实例
     *
     * @param nodeInstancePO 节点实例记录
     * @param flowElementMap 流程元素映射
     * @return true 表示该解析器可以处理此节点
     */
    boolean supports(NodeInstancePO nodeInstancePO, Map<String, FlowElement> flowElementMap);

    /**
     * 为历史展示解析 source sequence flow 列表。
     * <p>
     * 返回值为该节点对应的所有 source sequence flow 的 ElementInstance 列表。
     * 返回 null 则回退到 engine 默认行为。
     *
     * @param nodeInstancePO 节点实例记录
     * @param flowElementMap 流程元素映射
     * @return source sequence flow 的 ElementInstance 列表，或 null 表示使用默认逻辑
     */
    List<ElementInstance> resolveSourceSequenceFlows(
            NodeInstancePO nodeInstancePO,
            Map<String, FlowElement> flowElementMap);
}
