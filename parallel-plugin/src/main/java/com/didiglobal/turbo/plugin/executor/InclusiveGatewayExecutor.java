package com.didiglobal.turbo.plugin.executor;

import com.didiglobal.turbo.engine.dao.FlowInstanceMappingDAO;
import com.didiglobal.turbo.engine.dao.InstanceDataDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceLogDAO;
import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.engine.executor.ExecutorFactory;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.plugin.manager.PluginManager;
import com.didiglobal.turbo.engine.util.ExpressionCalculator;
import com.didiglobal.turbo.engine.util.FlowModelUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class InclusiveGatewayExecutor extends AbstractGatewayExecutor {

    public InclusiveGatewayExecutor(ExecutorFactory executorFactory, InstanceDataDAO instanceDataDAO, NodeInstanceDAO nodeInstanceDAO, ProcessInstanceDAO processInstanceDAO, NodeInstanceLogDAO nodeInstanceLogDAO, FlowInstanceMappingDAO flowInstanceMappingDAO, PluginManager pluginManager, ExpressionCalculator expressionCalculator) {
        super(executorFactory, instanceDataDAO, nodeInstanceDAO, processInstanceDAO, nodeInstanceLogDAO, flowInstanceMappingDAO, pluginManager, expressionCalculator);
    }

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
