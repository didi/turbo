package com.didiglobal.turbo.engine.executor;

import com.didiglobal.turbo.engine.model.FlowElement;

public interface ExecutorFactory {

    ElementExecutor getElementExecutor(FlowElement flowElement);


    ElementExecutor getCallActivityExecutor(FlowElement flowElement);
}
