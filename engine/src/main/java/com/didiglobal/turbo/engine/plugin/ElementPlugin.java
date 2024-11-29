package com.didiglobal.turbo.engine.plugin;

import com.didiglobal.turbo.engine.executor.ElementExecutor;
import com.didiglobal.turbo.engine.validator.ElementValidator;

public interface ElementPlugin extends Plugin{
    String ELEMENT_TYPE_PREFIX = "turbo.plugin.element_type.";
    ElementExecutor getElementExecutor();

    ElementValidator getElementValidator();

    Integer getFlowElementType();
}
