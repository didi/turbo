package com.didiglobal.turbo.plugin.config;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.didiglobal.turbo.engine.common.EntityPOEnum;
import com.didiglobal.turbo.engine.plugin.CustomOperationHandlerRegistry;
import com.didiglobal.turbo.plugin.dao.ParallelNodeInstanceHandler;
import com.didiglobal.turbo.plugin.dao.ParallelNodeInstanceLogHandler;
import com.didiglobal.turbo.plugin.dao.mapper.JoinSourceMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
@ComponentScan("com.didiglobal.turbo.plugin")
@MapperScan("com.didiglobal.turbo.plugin.dao")
@EnableAutoConfiguration(exclude = {DruidDataSourceAutoConfigure.class})
public class ParallelPluginConfig {

    @Resource
    private JoinSourceMapper joinSourceMapper;

    @PostConstruct
    public void init() {
        CustomOperationHandlerRegistry.registerHandler(EntityPOEnum.NODE_INSTANCE, new ParallelNodeInstanceHandler(joinSourceMapper));
        CustomOperationHandlerRegistry.registerHandler(EntityPOEnum.NODE_INSTANCE_LOG, new ParallelNodeInstanceLogHandler());
    }
}
