package com.xiaoju.uemc.turbo.engine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 项目名称：turbo
 * 类 名 称：RedisProperties
 * 类 描 述：
 * 创建时间：2020/11/12 10:24 PM
 * 创 建 人：didiwangxing
 */
@Data
@Component
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {
    private String url;
    private Integer timeout;
    private Integer jedisPoolMaxTotal;
    private Integer jedisPoolMaxIdle;
    private Integer jedisPoolMinIdle;
    private Long jedisPoolMaxWaitMillis;
    private Boolean jedisPoolTestOnBorrow;
}
