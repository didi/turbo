package com.didiglobal.turbo.plugin.annotation;

import com.didiglobal.turbo.plugin.config.ParallelPluginConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * auto scan spring bean and mybatis mapper path for parallel/inclusive gateway plugin
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ParallelPluginConfig.class})
public @interface EnableParallelPlugin {

}
