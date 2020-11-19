package com.xiaoju.uemc.turbo.engine.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Stefanie on 2019/12/16.
 */
@Configuration
@Data
public class EngineConfig {

    @Value("${hook.url}")
    private String hookUrl;

    @Value("${hook.timeout}")
    private Integer hookTimeout;
}
