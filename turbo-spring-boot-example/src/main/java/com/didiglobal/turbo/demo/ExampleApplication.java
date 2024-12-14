package com.didiglobal.turbo.demo;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.didiglobal.turbo.spring.annotation.EnableTurboEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableTurboEngine
@SpringBootApplication(scanBasePackages = {"com.didiglobal.turbo.demo"})
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor page = new PaginationInterceptor();
        page.setDialectType("mysql");
        return page;
    }
}
