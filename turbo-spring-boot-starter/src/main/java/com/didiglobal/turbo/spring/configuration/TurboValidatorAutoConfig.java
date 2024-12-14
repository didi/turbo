package com.didiglobal.turbo.spring.configuration;

import com.didiglobal.turbo.engine.config.BusinessConfig;
import com.didiglobal.turbo.engine.dao.FlowDefinitionDAO;
import com.didiglobal.turbo.engine.plugin.manager.PluginManager;
import com.didiglobal.turbo.engine.validator.CallActivityValidator;
import com.didiglobal.turbo.engine.validator.ElementValidatorFactory;
import com.didiglobal.turbo.engine.validator.EndEventValidator;
import com.didiglobal.turbo.engine.validator.ExclusiveGatewayValidator;
import com.didiglobal.turbo.engine.validator.FlowModelValidator;
import com.didiglobal.turbo.engine.validator.ModelValidator;
import com.didiglobal.turbo.engine.validator.SequenceFlowValidator;
import com.didiglobal.turbo.engine.validator.StartEventValidator;
import com.didiglobal.turbo.engine.validator.UserTaskValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * turbo validator auto configuration
 *
 * @author fxz
 */
@Configuration
public class TurboValidatorAutoConfig {

    @ConditionalOnMissingBean
    @Bean
    public CallActivityValidator callActivityValidator(BusinessConfig businessConfig, FlowDefinitionDAO flowDefinitionDAO) {
        return new CallActivityValidator(businessConfig, flowDefinitionDAO);
    }

    @ConditionalOnMissingBean
    @Bean
    public EndEventValidator endEventValidator() {
        return new EndEventValidator();
    }

    @ConditionalOnMissingBean
    @Bean
    public ExclusiveGatewayValidator exclusiveGatewayValidator() {
        return new ExclusiveGatewayValidator();
    }

    @ConditionalOnMissingBean
    @Bean
    public SequenceFlowValidator sequenceFlowValidator() {
        return new SequenceFlowValidator();
    }

    @ConditionalOnMissingBean
    @Bean
    public StartEventValidator startEventValidator() {
        return new StartEventValidator();
    }

    @ConditionalOnMissingBean
    @Bean
    public UserTaskValidator userTaskValidator() {
        return new UserTaskValidator();
    }

    @ConditionalOnMissingBean
    @Bean
    public ElementValidatorFactory elementValidatorFactory(StartEventValidator startEventValidator, EndEventValidator endEventValidator,
                                                           SequenceFlowValidator sequenceFlowValidator, UserTaskValidator userTaskValidator,
                                                           ExclusiveGatewayValidator exclusiveGatewayValidator, CallActivityValidator callActivityValidator,
                                                           PluginManager pluginManager) {
        return new ElementValidatorFactory(startEventValidator, endEventValidator, sequenceFlowValidator, userTaskValidator, exclusiveGatewayValidator, callActivityValidator, pluginManager);
    }

    @ConditionalOnMissingBean
    @Bean
    public FlowModelValidator flowModelValidator(ElementValidatorFactory elementValidatorFactory) {
        return new FlowModelValidator(elementValidatorFactory);
    }

    @ConditionalOnMissingBean
    @Bean
    public ModelValidator modelValidator(FlowModelValidator flowModelValidator) {
        return new ModelValidator(flowModelValidator);
    }

}