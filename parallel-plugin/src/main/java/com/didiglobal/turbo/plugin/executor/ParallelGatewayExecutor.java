package com.didiglobal.turbo.plugin.executor;

import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.model.InstanceData;

import java.util.Map;

public class ParallelGatewayExecutor extends AbstractGatewayExecutor {

    @Override
    protected int calculateOutgoingSize(FlowElement currentNodeModel, Map<String, FlowElement> flowElementMap, Map<String, InstanceData> instanceDataMap) {
        return currentNodeModel.getOutgoing().size();
    }
}