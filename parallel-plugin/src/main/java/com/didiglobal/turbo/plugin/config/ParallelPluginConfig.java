package com.didiglobal.turbo.plugin.config;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.didiglobal.turbo.engine.common.EntityPOEnum;
import com.didiglobal.turbo.engine.plugin.CustomOperationHandlerRegistry;
import com.didiglobal.turbo.plugin.dao.ParallelNodeInstanceHandler;
import com.didiglobal.turbo.plugin.dao.ParallelNodeInstanceLogHandler;
import com.didiglobal.turbo.plugin.executor.BranchMergeAnyOne;
import com.didiglobal.turbo.plugin.executor.BranchMergeJoinAll;
import com.didiglobal.turbo.plugin.executor.DataMergeAll;
import com.didiglobal.turbo.plugin.executor.DataMergeNone;
import com.didiglobal.turbo.plugin.executor.InclusiveGatewayExecutor;
import com.didiglobal.turbo.plugin.executor.MergeStrategyFactory;
import com.didiglobal.turbo.plugin.executor.ParallelGatewayExecutor;
import com.didiglobal.turbo.plugin.service.ParallelNodeInstanceService;
import com.didiglobal.turbo.plugin.validator.InclusiveGatewayValidator;
import com.didiglobal.turbo.plugin.validator.ParallelGatewayValidator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ComponentScan("com.didiglobal.turbo.plugin.config")
@MapperScan("com.didiglobal.turbo.plugin.dao")
@EnableAutoConfiguration(exclude = {DruidDataSourceAutoConfigure.class})
public class ParallelPluginConfig {

    @PostConstruct
    public void init() {
        CustomOperationHandlerRegistry.registerHandler(EntityPOEnum.NODE_INSTANCE, new ParallelNodeInstanceHandler());
        CustomOperationHandlerRegistry.registerHandler(EntityPOEnum.NODE_INSTANCE_LOG, new ParallelNodeInstanceLogHandler());
    }

    // ==================== Executor beans ====================

    @Bean
    public InclusiveGatewayExecutor inclusiveGatewayExecutor() {
        return new InclusiveGatewayExecutor();
    }

    @Bean
    public ParallelGatewayExecutor parallelGatewayExecutor() {
        return new ParallelGatewayExecutor();
    }

    @Bean
    public MergeStrategyFactory mergeStrategyFactory() {
        return new MergeStrategyFactory();
    }

    @Bean
    public BranchMergeJoinAll branchMergeJoinAll() {
        return new BranchMergeJoinAll();
    }

    @Bean
    public BranchMergeAnyOne branchMergeAnyOne() {
        return new BranchMergeAnyOne();
    }

    @Bean
    public DataMergeAll dataMergeAll() {
        return new DataMergeAll();
    }

    @Bean
    public DataMergeNone dataMergeNone() {
        return new DataMergeNone();
    }

    // ==================== Validator beans ====================

    @Bean
    public InclusiveGatewayValidator inclusiveGatewayValidator() {
        return new InclusiveGatewayValidator();
    }

    @Bean
    public ParallelGatewayValidator parallelGatewayValidator() {
        return new ParallelGatewayValidator();
    }

    // ==================== Service beans ====================

    @Bean
    public ParallelNodeInstanceService parallelNodeInstanceService() {
        return new ParallelNodeInstanceService();
    }
}

