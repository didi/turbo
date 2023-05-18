package com.didiglobal.turbo.engine.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.didiglobal.turbo.engine")
@MapperScan("com.didiglobal.turbo.engine.dao")
@EnableAutoConfiguration(exclude = {DruidDataSourceAutoConfigure.class})
public class TurboEngineConfig {

}
