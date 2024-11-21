package com.didiglobal.turbo.plugin.runner;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DruidDataSourceAutoConfigure.class})
@ComponentScan(basePackages = {"com.didiglobal.turbo.plugin", "com.didiglobal.turbo.engine"})
@MapperScan(basePackages = {"com.didiglobal.turbo.engine.dao", "com.didiglobal.turbo.plugin.dao"})
public class TestEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestEngineApplication.class, args);
    }
}
