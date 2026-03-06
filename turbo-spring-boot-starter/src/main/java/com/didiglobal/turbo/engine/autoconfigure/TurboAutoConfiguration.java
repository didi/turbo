package com.didiglobal.turbo.engine.autoconfigure;

import com.didiglobal.turbo.engine.annotation.EnableTurboEngine;
import com.didiglobal.turbo.engine.config.TurboEngineConfig;
import com.didiglobal.turbo.engine.engine.ProcessEngine;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * Spring Boot Auto-configuration for Turbo engine.
 *
 * <p>This class auto-configures the Turbo engine when:
 * <ul>
 *   <li>{@link ProcessEngine} is on the classpath</li>
 *   <li>The property {@code turbo.enabled} is not set to {@code false}</li>
 *   <li>No user-defined {@link ProcessEngine} bean exists</li>
 * </ul>
 *
 * <p>To disable auto-configuration, set {@code turbo.enabled=false} in your properties.
 *
 * <p>Configuration keys:
 * <ul>
 *   <li>{@code turbo.enabled} (default: true)</li>
 *   <li>{@code turbo.plugin-manager-custom-class} — fully-qualified class name of a custom PluginManager</li>
 *   <li>{@code turbo.call-activity-nested-level} — JSON string controlling CallActivity nesting limits</li>
 * </ul>
 *
 * @see TurboProperties
 * @see TurboEngineConfig
 */
@AutoConfiguration
@ConditionalOnClass(ProcessEngine.class)
@ConditionalOnProperty(prefix = "turbo", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(TurboProperties.class)
@ConditionalOnMissingBean(ProcessEngine.class)
@Import(TurboEngineConfig.class)
public class TurboAutoConfiguration {
    // TurboEngineConfig is imported and defines all Turbo beans.
    // Additional conditional beans or customizations can be added here.
}
