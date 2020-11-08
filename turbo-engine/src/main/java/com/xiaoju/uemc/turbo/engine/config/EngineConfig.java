package com.xiaoju.uemc.turbo.engine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Stefanie on 2019/12/16.
 */
// TODO: 2019/12/19 rename
@Configuration
public class EngineConfig {

    // TODO: 2019/12/16 dynamic config for caller
    @Value("${optimus_prime.hook.url}")
    private String hookUrl;

    @Value("${optimus_prime.hook.timeout}")
    private Integer hookTimeout;

    public String getHookUrl() {
        return hookUrl;
    }

    public void setHookUrl(String hookUrl) {
        this.hookUrl = hookUrl;
    }

    public Integer getHookTimeout() {
        return hookTimeout;
    }

    public void setHookTimeout(Integer hookTimeout) {
        this.hookTimeout = hookTimeout;
    }
}
