package com.didiglobal.turbo.engine.core;

import com.didiglobal.turbo.engine.executor.FlowExecutor;
import com.didiglobal.turbo.engine.processor.DefinitionProcessor;
import com.didiglobal.turbo.engine.processor.RuntimeProcessor;

/**
 * @author fxz
 */
public class TurboContext {

    private RuntimeProcessor runtimeProcessor;
    private DefinitionProcessor definitionProcessor;
    private FlowExecutor flowExecutor;

    public TurboContext build(RuntimeProcessor runtimeProcessor) {
        runtimeProcessor.configure(this);
        return this;
    }

    public RuntimeProcessor getRuntimeProcessor() {
        return runtimeProcessor;
    }

    public void setRuntimeProcessor(RuntimeProcessor runtimeProcessor) {
        this.runtimeProcessor = runtimeProcessor;
    }

    public DefinitionProcessor getDefinitionProcessor() {
        return definitionProcessor;
    }

    public void setDefinitionProcessor(DefinitionProcessor definitionProcessor) {
        this.definitionProcessor = definitionProcessor;
    }

    public FlowExecutor getFlowExecutor() {
        return flowExecutor;
    }

    public void setFlowExecutor(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor;
    }
}