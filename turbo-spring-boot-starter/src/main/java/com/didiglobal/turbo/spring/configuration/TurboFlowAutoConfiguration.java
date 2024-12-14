package com.didiglobal.turbo.spring.configuration;

import com.didiglobal.turbo.engine.config.BusinessConfig;
import com.didiglobal.turbo.engine.core.TurboContext;
import com.didiglobal.turbo.engine.dao.FlowDefinitionDAO;
import com.didiglobal.turbo.engine.dao.FlowDeploymentDAO;
import com.didiglobal.turbo.engine.dao.FlowInstanceMappingDAO;
import com.didiglobal.turbo.engine.dao.InstanceDataDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceLogDAO;
import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.engine.engine.ProcessEngine;
import com.didiglobal.turbo.engine.engine.impl.ProcessEngineImpl;
import com.didiglobal.turbo.engine.executor.EndEventExecutor;
import com.didiglobal.turbo.engine.executor.ExclusiveGatewayExecutor;
import com.didiglobal.turbo.engine.executor.ExecutorFactory;
import com.didiglobal.turbo.engine.executor.FlowExecutor;
import com.didiglobal.turbo.engine.executor.SequenceFlowExecutor;
import com.didiglobal.turbo.engine.executor.StartEventExecutor;
import com.didiglobal.turbo.engine.executor.UserTaskExecutor;
import com.didiglobal.turbo.engine.executor.callactivity.SyncSingleCallActivityExecutor;
import com.didiglobal.turbo.engine.plugin.manager.PluginManager;
import com.didiglobal.turbo.engine.processor.DefinitionProcessor;
import com.didiglobal.turbo.engine.processor.RuntimeProcessor;
import com.didiglobal.turbo.engine.service.FlowInstanceService;
import com.didiglobal.turbo.engine.service.InstanceDataService;
import com.didiglobal.turbo.engine.service.NodeInstanceService;
import com.didiglobal.turbo.engine.spi.HookService;
import com.didiglobal.turbo.engine.util.ExpressionCalculator;
import com.didiglobal.turbo.engine.validator.ModelValidator;
import com.didiglobal.turbo.spring.support.ExecutorFactoryImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * turbo flow auto configuration
 *
 * @author fxz
 */
@Configuration
public class TurboFlowAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public TurboContext turboContext(RuntimeProcessor runtimeProcessor, DefinitionProcessor definitionProcessor
            , FlowExecutor flowExecutor) {
        TurboContext turboContext = new TurboContext();
        turboContext.setRuntimeProcessor(runtimeProcessor);
        turboContext.setDefinitionProcessor(definitionProcessor);
        turboContext.setFlowExecutor(flowExecutor);
        runtimeProcessor.configure(turboContext);
        return turboContext;
    }

    @ConditionalOnMissingBean
    @Bean
    public DefinitionProcessor definitionProcessor(PluginManager pluginManager, ModelValidator modelValidator, FlowDefinitionDAO flowDefinitionDAO, FlowDeploymentDAO flowDeploymentDAO) {
        return new DefinitionProcessor(pluginManager, modelValidator, flowDefinitionDAO, flowDeploymentDAO);
    }

    @ConditionalOnMissingBean
    @Bean
    public RuntimeProcessor runtimeProcessor(FlowDeploymentDAO flowDeploymentDAO, ProcessInstanceDAO processInstanceDAO,
                                             NodeInstanceDAO nodeInstanceDAO, FlowInstanceMappingDAO flowInstanceMappingDAO,
                                             FlowInstanceService flowInstanceService, InstanceDataService instanceDataService, NodeInstanceService nodeInstanceService) {
        return new RuntimeProcessor(flowDeploymentDAO, processInstanceDAO, nodeInstanceDAO, flowInstanceMappingDAO, flowInstanceService, instanceDataService, nodeInstanceService);
    }

    @ConditionalOnMissingBean
    @Bean
    public ProcessEngine processEngine(TurboContext turboContext) {
        ProcessEngineImpl processEngine = new ProcessEngineImpl();
        processEngine.configure(turboContext);
        return processEngine;
    }

    @ConditionalOnMissingBean
    @Bean
    public FlowInstanceService flowInstanceService(NodeInstanceDAO nodeInstanceDAO, FlowInstanceMappingDAO flowInstanceMappingDAO, ProcessInstanceDAO processInstanceDAO, FlowDeploymentDAO flowDeploymentDAO) {
        return new FlowInstanceService(nodeInstanceDAO, flowInstanceMappingDAO, processInstanceDAO, flowDeploymentDAO);
    }

    @ConditionalOnMissingBean
    @Bean
    public InstanceDataService instanceDataService(InstanceDataDAO instanceDataDAO, ProcessInstanceDAO processInstanceDAO, FlowDeploymentDAO flowDeploymentDAO, NodeInstanceDAO nodeInstanceDAO, FlowInstanceMappingDAO flowInstanceMappingDAO,
                                                   FlowInstanceService flowInstanceService) {
        return new InstanceDataService(instanceDataDAO, processInstanceDAO, flowDeploymentDAO, nodeInstanceDAO, flowInstanceMappingDAO, flowInstanceService);
    }

    @ConditionalOnMissingBean
    @Bean
    public NodeInstanceService nodeInstanceService(NodeInstanceDAO nodeInstanceDAO, ProcessInstanceDAO processInstanceDAO, FlowDeploymentDAO flowDeploymentDAO, FlowInstanceService flowInstanceService) {
        return new NodeInstanceService(nodeInstanceDAO, processInstanceDAO, flowDeploymentDAO, flowInstanceService);
    }

    @ConditionalOnMissingBean
    @Bean
    public FlowExecutor flowExecutor(ExecutorFactory executorFactory,
                                     InstanceDataDAO instanceDataDAO, NodeInstanceDAO nodeInstanceDAO, ProcessInstanceDAO processInstanceDAO, NodeInstanceLogDAO nodeInstanceLogDAO, FlowInstanceMappingDAO flowInstanceMappingDAO, PluginManager pluginManager) {
        return new FlowExecutor(executorFactory, instanceDataDAO, nodeInstanceDAO, processInstanceDAO, nodeInstanceLogDAO, flowInstanceMappingDAO, pluginManager);
    }

    @ConditionalOnMissingBean
    @Bean
    public ExecutorFactory executorFactory() {
        return new ExecutorFactoryImpl();
    }

    @ConditionalOnMissingBean
    @Bean
    public StartEventExecutor startEventExecutor(ExecutorFactory executorFactory,
                                                 InstanceDataDAO instanceDataDAO, NodeInstanceDAO nodeInstanceDAO, ProcessInstanceDAO processInstanceDAO, NodeInstanceLogDAO nodeInstanceLogDAO, FlowInstanceMappingDAO flowInstanceMappingDAO,
                                                 PluginManager pluginManager, ExpressionCalculator expressionCalculator) {
        return new StartEventExecutor(executorFactory, instanceDataDAO, nodeInstanceDAO, processInstanceDAO, nodeInstanceLogDAO, flowInstanceMappingDAO, pluginManager, expressionCalculator);
    }

    @ConditionalOnMissingBean
    @Bean
    public EndEventExecutor endEventExecutor(ExecutorFactory executorFactory,
                                             InstanceDataDAO instanceDataDAO, NodeInstanceDAO nodeInstanceDAO, ProcessInstanceDAO processInstanceDAO, NodeInstanceLogDAO nodeInstanceLogDAO, FlowInstanceMappingDAO flowInstanceMappingDAO,
                                             PluginManager pluginManager, ExpressionCalculator expressionCalculator) {
        return new EndEventExecutor(executorFactory, instanceDataDAO, nodeInstanceDAO, processInstanceDAO, nodeInstanceLogDAO, flowInstanceMappingDAO, pluginManager, expressionCalculator);
    }

    @Bean
    @ConditionalOnMissingBean
    public SequenceFlowExecutor sequenceFlowExecutor(ExecutorFactory executorFactory,
                                                     InstanceDataDAO instanceDataDAO, NodeInstanceDAO nodeInstanceDAO, ProcessInstanceDAO processInstanceDAO, NodeInstanceLogDAO nodeInstanceLogDAO, FlowInstanceMappingDAO flowInstanceMappingDAO,
                                                     PluginManager pluginManager, ExpressionCalculator expressionCalculator) {
        return new SequenceFlowExecutor(executorFactory, instanceDataDAO, nodeInstanceDAO, processInstanceDAO, nodeInstanceLogDAO, flowInstanceMappingDAO, pluginManager, expressionCalculator);
    }

    @ConditionalOnMissingBean
    @Bean
    public UserTaskExecutor userTaskExecutor(ExecutorFactory executorFactory,
                                             InstanceDataDAO instanceDataDAO, NodeInstanceDAO nodeInstanceDAO, ProcessInstanceDAO processInstanceDAO, NodeInstanceLogDAO nodeInstanceLogDAO, FlowInstanceMappingDAO flowInstanceMappingDAO,
                                             PluginManager pluginManager, ExpressionCalculator expressionCalculator) {
        return new UserTaskExecutor(executorFactory, instanceDataDAO, nodeInstanceDAO, processInstanceDAO, nodeInstanceLogDAO, flowInstanceMappingDAO, pluginManager, expressionCalculator);
    }

    @ConditionalOnMissingBean
    @Bean
    public ExclusiveGatewayExecutor exclusiveGatewayExecutor(ExecutorFactory executorFactory,
                                                             InstanceDataDAO instanceDataDAO, NodeInstanceDAO nodeInstanceDAO, ProcessInstanceDAO processInstanceDAO, NodeInstanceLogDAO nodeInstanceLogDAO, FlowInstanceMappingDAO flowInstanceMappingDAO,
                                                             PluginManager pluginManager, ExpressionCalculator expressionCalculator,
                                                             ObjectProvider<List<HookService>> hookServiceProvider) {
        return new ExclusiveGatewayExecutor(executorFactory, instanceDataDAO, nodeInstanceDAO, processInstanceDAO, nodeInstanceLogDAO, flowInstanceMappingDAO, pluginManager, hookServiceProvider.getIfAvailable(), expressionCalculator);
    }

    @ConditionalOnMissingBean
    @Bean
    public SyncSingleCallActivityExecutor syncSingleCallActivityExecutor(ExecutorFactory executorFactory,
                                                                         InstanceDataDAO instanceDataDAO, NodeInstanceDAO nodeInstanceDAO, ProcessInstanceDAO processInstanceDAO, NodeInstanceLogDAO nodeInstanceLogDAO, FlowInstanceMappingDAO flowInstanceMappingDAO,
                                                                         PluginManager pluginManager, RuntimeProcessor runtimeProcessor,
                                                                         FlowDeploymentDAO flowDeploymentDAO,
                                                                         NodeInstanceService nodeInstanceService, BusinessConfig businessConfig,
                                                                         ExpressionCalculator expressionCalculator) {
        return new SyncSingleCallActivityExecutor(executorFactory, instanceDataDAO, nodeInstanceDAO, processInstanceDAO, nodeInstanceLogDAO, flowInstanceMappingDAO, pluginManager, runtimeProcessor, flowDeploymentDAO, nodeInstanceService, businessConfig, expressionCalculator);
    }

}