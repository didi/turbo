package com.didiglobal.turbo.demo;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.didiglobal.turbo.engine.annotation.EnableTurboEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableTurboEngine
@SpringBootApplication(scanBasePackages = {"com.didiglobal.turbo.demo"})
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor page = new PaginationInterceptor();
        page.setDialectType("mysql");
        return page;
    }
}
