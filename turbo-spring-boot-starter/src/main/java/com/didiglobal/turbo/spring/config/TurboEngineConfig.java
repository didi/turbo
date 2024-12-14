package com.didiglobal.turbo.spring.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/**
 * auto scan mybatis mapper path
 */
@MapperScan("com.didiglobal.turbo.mybatis.dao")
@EnableAutoConfiguration(exclude = {DruidDataSourceAutoConfigure.class})
public class TurboEngineConfig {

}
