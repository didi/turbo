package com.didiglobal.turbo.spring.annotation;

import com.didiglobal.turbo.spring.config.TurboEngineConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * auto scan spring bean and mybatis mapper path
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({TurboEngineConfig.class})
public @interface EnableTurboEngine {

}
