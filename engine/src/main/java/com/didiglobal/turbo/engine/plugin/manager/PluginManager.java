package com.didiglobal.turbo.engine.plugin.manager;

import com.didiglobal.turbo.engine.common.EntityPOEnum;
import com.didiglobal.turbo.engine.plugin.Plugin;

import java.util.List;

public interface PluginManager {
    List<Plugin> getPlugins();

    <T extends Plugin> List<T> getPluginsFor(Class<T> pluginInterface);

    Integer countPlugins();

    <T extends Plugin> Boolean containsPlugin(Class<T> pluginInterface, String pluginName);
}
