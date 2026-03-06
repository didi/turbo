package com.didiglobal.turbo.engine.config;

import com.didiglobal.turbo.engine.interceptor.MyBatisInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
public class TurboMybatisConfig {
    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @PostConstruct
    public void addCustomInterceptor() {
        // 添加自定义拦截器
        sqlSessionFactory.getConfiguration().addInterceptor(new MyBatisInterceptor(sqlSessionFactory));
    }
}
