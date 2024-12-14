package com.didiglobal.turbo.plugin.config;

import com.didiglobal.turbo.engine.dao.FlowInstanceMappingDAO;
import com.didiglobal.turbo.engine.dao.InstanceDataDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceLogDAO;
import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.engine.executor.ExecutorFactory;
import com.didiglobal.turbo.engine.plugin.manager.PluginManager;
import com.didiglobal.turbo.engine.util.ExpressionCalculator;
import com.didiglobal.turbo.plugin.executor.InclusiveGatewayExecutor;
import com.didiglobal.turbo.plugin.executor.ParallelGatewayExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author fxz
 * @since 2024/12/14 17:59
 */
@Configuration
public class ParallelGatewayAutoConfig {

    @Lazy
    @Bean
    public ParallelGatewayExecutor parallelGatewayExecutor(ExecutorFactory executorFactory, InstanceDataDAO instanceDataDAO, NodeInstanceDAO nodeInstanceDAO, ProcessInstanceDAO processInstanceDAO, NodeInstanceLogDAO nodeInstanceLogDAO, FlowInstanceMappingDAO flowInstanceMappingDAO, PluginManager pluginManager, ExpressionCalculator expressionCalculator) {
        return new ParallelGatewayExecutor(executorFactory, instanceDataDAO, nodeInstanceDAO, processInstanceDAO, nodeInstanceLogDAO, flowInstanceMappingDAO, pluginManager, expressionCalculator);
    }

    @Lazy
    @Bean
    public InclusiveGatewayExecutor inclusiveGatewayExecutor(ExecutorFactory executorFactory, InstanceDataDAO instanceDataDAO, NodeInstanceDAO nodeInstanceDAO, ProcessInstanceDAO processInstanceDAO, NodeInstanceLogDAO nodeInstanceLogDAO, FlowInstanceMappingDAO flowInstanceMappingDAO, PluginManager pluginManager, ExpressionCalculator expressionCalculator) {
        return new InclusiveGatewayExecutor(executorFactory, instanceDataDAO, nodeInstanceDAO, processInstanceDAO, nodeInstanceLogDAO, flowInstanceMappingDAO, pluginManager, expressionCalculator);
    }

}
