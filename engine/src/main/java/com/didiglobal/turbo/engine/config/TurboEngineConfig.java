package com.didiglobal.turbo.engine.config;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.didiglobal.turbo.engine.dao.FlowDefinitionDAO;
import com.didiglobal.turbo.engine.dao.FlowDeploymentDAO;
import com.didiglobal.turbo.engine.dao.FlowInstanceMappingDAO;
import com.didiglobal.turbo.engine.dao.InstanceDataDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceLogDAO;
import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.engine.engine.impl.ProcessEngineImpl;
import com.didiglobal.turbo.engine.executor.EndEventExecutor;
import com.didiglobal.turbo.engine.executor.ExclusiveGatewayExecutor;
import com.didiglobal.turbo.engine.executor.ExecutorFactory;
import com.didiglobal.turbo.engine.executor.FlowExecutor;
import com.didiglobal.turbo.engine.executor.SequenceFlowExecutor;
import com.didiglobal.turbo.engine.executor.StartEventExecutor;
import com.didiglobal.turbo.engine.executor.UserTaskExecutor;
import com.didiglobal.turbo.engine.executor.callactivity.SyncSingleCallActivityExecutor;
import com.didiglobal.turbo.engine.processor.DefinitionProcessor;
import com.didiglobal.turbo.engine.processor.RuntimeProcessor;
import com.didiglobal.turbo.engine.service.FlowInstanceService;
import com.didiglobal.turbo.engine.service.InstanceDataService;
import com.didiglobal.turbo.engine.service.NodeInstanceService;
import com.didiglobal.turbo.engine.validator.CallActivityValidator;
import com.didiglobal.turbo.engine.validator.ElementValidatorFactory;
import com.didiglobal.turbo.engine.validator.EndEventValidator;
import com.didiglobal.turbo.engine.validator.ExclusiveGatewayValidator;
import com.didiglobal.turbo.engine.validator.FlowModelValidator;
import com.didiglobal.turbo.engine.validator.ModelValidator;
import com.didiglobal.turbo.engine.validator.SequenceFlowValidator;
import com.didiglobal.turbo.engine.validator.StartEventValidator;
import com.didiglobal.turbo.engine.validator.UserTaskValidator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.didiglobal.turbo.engine.config")
@MapperScan("com.didiglobal.turbo.engine.dao")
@EnableAutoConfiguration(exclude = {DruidDataSourceAutoConfigure.class})
public class TurboEngineConfig {

    // ==================== DAO beans ====================

    @Bean
    public FlowDefinitionDAO flowDefinitionDAO() {
        return new FlowDefinitionDAO();
    }

    @Bean
    public FlowDeploymentDAO flowDeploymentDAO() {
        return new FlowDeploymentDAO();
    }

    @Bean
    public FlowInstanceMappingDAO flowInstanceMappingDAO() {
        return new FlowInstanceMappingDAO();
    }

    @Bean
    public InstanceDataDAO instanceDataDAO() {
        return new InstanceDataDAO();
    }

    @Bean
    public NodeInstanceDAO nodeInstanceDAO() {
        return new NodeInstanceDAO();
    }

    @Bean
    public NodeInstanceLogDAO nodeInstanceLogDAO() {
        return new NodeInstanceLogDAO();
    }

    @Bean
    public ProcessInstanceDAO processInstanceDAO() {
        return new ProcessInstanceDAO();
    }

    // ==================== Service beans ====================

    @Bean
    public FlowInstanceService flowInstanceService() {
        return new FlowInstanceService();
    }

    @Bean
    public InstanceDataService instanceDataService() {
        return new InstanceDataService();
    }

    @Bean
    public NodeInstanceService nodeInstanceService() {
        return new NodeInstanceService();
    }

    // ==================== Validator beans ====================

    @Bean
    public StartEventValidator startEventValidator() {
        return new StartEventValidator();
    }

    @Bean
    public EndEventValidator endEventValidator() {
        return new EndEventValidator();
    }

    @Bean
    public SequenceFlowValidator sequenceFlowValidator() {
        return new SequenceFlowValidator();
    }

    @Bean
    public UserTaskValidator userTaskValidator() {
        return new UserTaskValidator();
    }

    @Bean
    public ExclusiveGatewayValidator exclusiveGatewayValidator() {
        return new ExclusiveGatewayValidator();
    }

    @Bean
    public FlowModelValidator flowModelValidator() {
        return new FlowModelValidator();
    }

    @Bean
    public ModelValidator modelValidator() {
        return new ModelValidator();
    }

    @Bean
    public CallActivityValidator callActivityValidator() {
        return new CallActivityValidator();
    }

    @Bean
    public ElementValidatorFactory elementValidatorFactory() {
        return new ElementValidatorFactory();
    }

    // ==================== Executor beans ====================

    @Bean
    public StartEventExecutor startEventExecutor() {
        return new StartEventExecutor();
    }

    @Bean
    public EndEventExecutor endEventExecutor() {
        return new EndEventExecutor();
    }

    @Bean
    public SequenceFlowExecutor sequenceFlowExecutor() {
        return new SequenceFlowExecutor();
    }

    @Bean
    public UserTaskExecutor userTaskExecutor() {
        return new UserTaskExecutor();
    }

    @Bean
    public ExclusiveGatewayExecutor exclusiveGatewayExecutor() {
        return new ExclusiveGatewayExecutor();
    }

    @Bean
    public FlowExecutor flowExecutor() {
        return new FlowExecutor();
    }

    @Bean
    public SyncSingleCallActivityExecutor syncSingleCallActivityExecutor() {
        return new SyncSingleCallActivityExecutor();
    }

    @Bean
    public ExecutorFactory executorFactory() {
        return new ExecutorFactory();
    }

    // ==================== Processor beans ====================

    @Bean
    public DefinitionProcessor definitionProcessor() {
        return new DefinitionProcessor();
    }

    @Bean
    public RuntimeProcessor runtimeProcessor() {
        return new RuntimeProcessor();
    }

    // ==================== Engine bean ====================

    @Bean
    public ProcessEngineImpl processEngine() {
        return new ProcessEngineImpl();
    }
}
