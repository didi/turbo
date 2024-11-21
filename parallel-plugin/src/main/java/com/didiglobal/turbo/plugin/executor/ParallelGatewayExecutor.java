package com.didiglobal.turbo.plugin.executor;

import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.model.InstanceData;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ParallelGatewayExecutor extends AbstractGatewayExecutor {

    @Override
    protected int calculateOutgoingSize(FlowElement currentNodeModel, Map<String, FlowElement> flowElementMap, Map<String, InstanceData> instanceDataMap) {
        return currentNodeModel.getOutgoing().size();
    }
}