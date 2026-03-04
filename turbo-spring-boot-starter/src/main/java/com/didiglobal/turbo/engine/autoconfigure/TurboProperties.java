package com.didiglobal.turbo.engine.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Turbo engine.
 * All properties are prefixed with {@code turbo}.
 */
@ConfigurationProperties(prefix = "turbo")
public class TurboProperties {

    /**
     * Whether Turbo engine auto-configuration is enabled.
     */
    private boolean enabled = true;

    /**
     * Fully-qualified class name of a custom {@link com.didiglobal.turbo.engine.plugin.manager.PluginManager}.
     * If not set, the default plugin manager is used.
     */
    private String pluginManagerCustomClass;

    /**
     * Optional: nested-level limit for CallActivity nodes, as a JSON string.
     * Example: {@code {"caller1": 3, "caller2": 5}}
     */
    private String callActivityNestedLevel;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPluginManagerCustomClass() {
        return pluginManagerCustomClass;
    }

    public void setPluginManagerCustomClass(String pluginManagerCustomClass) {
        this.pluginManagerCustomClass = pluginManagerCustomClass;
    }

    public String getCallActivityNestedLevel() {
        return callActivityNestedLevel;
    }

    public void setCallActivityNestedLevel(String callActivityNestedLevel) {
        this.callActivityNestedLevel = callActivityNestedLevel;
    }
}
