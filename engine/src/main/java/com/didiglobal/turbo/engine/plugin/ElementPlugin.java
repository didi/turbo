package com.didiglobal.turbo.engine.plugin;

import com.didiglobal.turbo.engine.executor.ElementExecutor;
import com.didiglobal.turbo.engine.validator.ElementValidator;

public interface ElementPlugin extends Plugin{
    String ELEMENT_TYPE_PREFIX = "turbo.plugin.element_type.";
    ElementExecutor getElementExecutor();

    ElementValidator getElementValidator();

    Integer getFlowElementType();

    /**
     * 返回自定义的 SourceNodeResolver，用于在 getHistoryElementList 中
     * 自定义节点的 source sequence flow 解析逻辑。
     * <p>
     * 默认返回 null，表示使用 engine 的默认行为。
     * 插件可以重写此方法以支持自定义节点类型（如并行网关 join 节点）的多源展示。
     *
     * @return SourceNodeResolver 实例，或 null 使用默认行为
     */
    default SourceNodeResolver getSourceNodeResolver() {
        return null;
    }
}
