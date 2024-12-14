package com.didiglobal.turbo.engine.runner;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.didiglobal.turbo.spring.annotation.EnableTurboEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableTurboEngine
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class})
public class TestEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestEngineApplication.class, args);
    }
}
