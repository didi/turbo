package com.didiglobal.turbo.plugin.runner;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.didiglobal.turbo.spring.annotation.EnableTurboEngine;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.didiglobal.turbo.plugin")
@MapperScan("com.didiglobal.turbo.plugin.dao.mapper")
@EnableTurboEngine
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class})
public class TestEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestEngineApplication.class, args);
    }
}
