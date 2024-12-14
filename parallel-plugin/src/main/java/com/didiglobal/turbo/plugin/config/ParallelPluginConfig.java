package com.didiglobal.turbo.plugin.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.didiglobal.turbo.engine.common.EntityPOEnum;
import com.didiglobal.turbo.plugin.dao.ParallelNodeInstanceHandler;
import com.didiglobal.turbo.plugin.dao.ParallelNodeInstanceLogHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ComponentScan("com.didiglobal.turbo.plugin")
@MapperScan("com.didiglobal.turbo.plugin.dao")
@EnableAutoConfiguration(exclude = {DruidDataSourceAutoConfigure.class})
public class ParallelPluginConfig {

    @PostConstruct
    public void init() {
        com.didiglobal.turbo.mybatis.CustomOperationHandlerRegistry.registerHandler(EntityPOEnum.NODE_INSTANCE, new ParallelNodeInstanceHandler());
        com.didiglobal.turbo.mybatis.CustomOperationHandlerRegistry.registerHandler(EntityPOEnum.NODE_INSTANCE_LOG, new ParallelNodeInstanceLogHandler());
    }
}
