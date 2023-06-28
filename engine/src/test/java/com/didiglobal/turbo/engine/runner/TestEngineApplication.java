package com.didiglobal.turbo.engine.runner;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.didiglobal.turbo.engine.annotation.EnableTurboEngine;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableTurboEngine
@SpringBootApplication(scanBasePackages = "com.didiglobal.turbo.engine", exclude = {DruidDataSourceAutoConfigure.class})
public class TestEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestEngineApplication.class, args);
    }
}
