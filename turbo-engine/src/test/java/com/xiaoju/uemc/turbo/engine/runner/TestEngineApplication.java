package com.xiaoju.uemc.turbo.engine.runner;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Stefanie on 2019/11/27.
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {DruidDataSourceAutoConfigure.class})
@ComponentScan("com.xiaoju.uemc.turbo.engine")
@MapperScan(basePackages = {"com.xiaoju.uemc.turbo.engine.dao"})
public class TestEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestEngineApplication.class, args);
    }
}
