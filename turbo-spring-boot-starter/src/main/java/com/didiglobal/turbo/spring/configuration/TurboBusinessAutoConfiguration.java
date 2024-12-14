package com.didiglobal.turbo.spring.configuration;

import com.didiglobal.turbo.engine.config.BusinessConfig;
import com.didiglobal.turbo.spring.support.BusinessConfigImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * turbo business auto configuration
 * @author fxz
 */
@Configuration
public class TurboBusinessAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public BusinessConfig businessConfig() {
        return new BusinessConfigImpl();
    }

}