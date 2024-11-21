package com.didiglobal.turbo.plugin.executor;

import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.util.FlowModelUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InclusiveGatewayExecutor extends AbstractGatewayExecutor {

    @Override
    protected boolean calculateCondition(FlowElement outgoingSequenceFlow, Map<String, InstanceData> instanceDataMap) {
        // case1 condition is true, hit the outgoing
        String condition = FlowModelUtil.getConditionFromSequenceFlow(outgoingSequenceFlow);
        return StringUtils.isNotBlank(condition) && processCondition(condition, instanceDataMap);
    }

    @Override
    protected int calculateOutgoingSize(FlowElement currentNodeModel, Map<String, FlowElement> flowElementMap, Map<String, InstanceData> instanceDataMap) {
        return calculateOutgoings(currentNodeModel, flowElementMap, instanceDataMap).size();
    }
}
