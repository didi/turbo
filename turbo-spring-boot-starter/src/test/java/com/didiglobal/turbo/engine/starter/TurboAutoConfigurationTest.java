package com.didiglobal.turbo.engine.starter;

import com.didiglobal.turbo.engine.autoconfigure.TurboAutoConfiguration;
import com.didiglobal.turbo.engine.autoconfigure.TurboProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Integration test for {@link TurboAutoConfiguration}.
 * <p>
 * Uses {@link ApplicationContextRunner} to verify auto-configuration behaviour
 * without requiring a live database.
 */
class TurboAutoConfigurationTest {

    /**
     * Minimal context runner that only processes {@link TurboProperties}
     * without attempting to start the full engine (which requires a database).
     */
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(TurboAutoConfiguration.class));

    @Test
    void turboPropertiesAreLoadedWithDefaults() {
        // Use a properties-only runner (we cannot start the full engine without a DB)
        new ApplicationContextRunner()
                .withPropertyValues()
                .withUserConfiguration(TurboPropertiesTestConfig.class)
                .run(ctx -> {
                    TurboProperties props = ctx.getBean(TurboProperties.class);
                    Assertions.assertNotNull(props, "TurboProperties bean should exist");
                    Assertions.assertTrue(props.isEnabled(), "Default enabled should be true");
                });
    }

    @Test
    void autoConfigurationIsDisabledWhenPropertyFalse() {
        // When turbo.enabled=false, TurboAutoConfiguration should not be activated
        contextRunner
                .withPropertyValues("turbo.enabled=false")
                .run(ctx -> {
                    // The context fails to start (no DB) but TurboAutoConfiguration is skipped
                    // so processEngine bean should not be registered from our auto-config
                    Assertions.assertFalse(ctx.containsBean("processEngine"),
                            "ProcessEngine should not be created when turbo.enabled=false");
                });
    }

    @Test
    void turboPropertiesHonorCustomValues() {
        new ApplicationContextRunner()
                .withPropertyValues("turbo.call-activity-nested-level={\"default\":5}",
                        "turbo.enabled=true")
                .withUserConfiguration(TurboPropertiesTestConfig.class)
                .run(ctx -> {
                    TurboProperties props = ctx.getBean(TurboProperties.class);
                    Assertions.assertNotNull(props.getCallActivityNestedLevel(),
                            "callActivityNestedLevel should be set");
                    Assertions.assertEquals("{\"default\":5}", props.getCallActivityNestedLevel());
                });
    }

    /**
     * Minimal Spring configuration that only registers {@link TurboProperties}
     * without the full engine infrastructure.
     */
    @org.springframework.context.annotation.Configuration
    @org.springframework.boot.context.properties.EnableConfigurationProperties(TurboProperties.class)
    static class TurboPropertiesTestConfig {
    }
}
