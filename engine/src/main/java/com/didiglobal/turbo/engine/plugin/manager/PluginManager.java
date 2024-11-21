package com.didiglobal.turbo.engine.plugin.manager;

import com.didiglobal.turbo.engine.common.EntityPOEnum;
import com.didiglobal.turbo.engine.dao.BaseDAO;
import com.didiglobal.turbo.engine.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PluginManager {
    List<Plugin> getPlugins();

    <T extends Plugin> List<T> getPluginsFor(Class<T> pluginInterface);

    Integer countPlugins();

    <T extends Plugin> Boolean containsPlugin(Class<T> pluginInterface, String pluginName);
}
