package com.didiglobal.turbo.spring.plugin;

import com.didiglobal.turbo.engine.common.PluginTypeEnum;
import com.didiglobal.turbo.engine.plugin.ElementPlugin;
import com.didiglobal.turbo.engine.plugin.ExpressionCalculatorPlugin;
import com.didiglobal.turbo.engine.plugin.IdGeneratorPlugin;
import com.didiglobal.turbo.engine.plugin.ListenerPlugin;
import com.didiglobal.turbo.engine.plugin.Plugin;
import com.didiglobal.turbo.engine.plugin.manager.AbstractPluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultPluginManager extends AbstractPluginManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPluginManager.class);

    private final DefaultListableBeanFactory beanFactory;

    public DefaultPluginManager(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        loadPlugins();
        initialize();
    }

    /**
     * 初始化插件管理器，将不同类型的插件进行初始化操作。
     */
    @Override
    protected void initialize() {
        initializePlugins(pluginMap.getOrDefault(PluginTypeEnum.ELEMENT_PLUGIN.getPluginType(), new ArrayList<>()), ElementPlugin.class);
        initializePlugins(pluginMap.getOrDefault(PluginTypeEnum.ID_GENERATOR_PLUGIN.getPluginType(), new ArrayList<>()), IdGeneratorPlugin.class);
        initializePlugins(pluginMap.getOrDefault(PluginTypeEnum.EXPRESSION_CALCULATOR_PLUGIN.getPluginType(), new ArrayList<>()), ExpressionCalculatorPlugin.class);
        initializePlugins(pluginMap.getOrDefault(PluginTypeEnum.LISTENER_PLUGIN.getPluginType(), new ArrayList<>()), ListenerPlugin.class);
    }

    public DefaultListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    /**
     * 初始化插件列表，并检查和配置每个插件。
     *
     * @param pluginList      要初始化的插件列表
     * @param pluginInterface 插件接口类型，用于日志和信息输出
     * @param <T>             插件的具体类型
     */
    private <T extends Plugin> void initializePlugins(List<? extends Plugin> pluginList, Class<T> pluginInterface) {
        LOGGER.info("start checking and initializing {} plugins", pluginInterface.getSimpleName());
        checkSupport(pluginList);
        pluginList.forEach(plugin -> {
            try {
                plugin.init();
                if (plugin instanceof BasePlugin) {
                    ((BasePlugin) plugin).setBeanFactory(beanFactory);
                }
            } catch (Exception e) {
                LOGGER.warn("An error occurred while initializing plugin: {}. Error: {}", plugin.getClass().getName(), e.getMessage());
                throw new RuntimeException(e);
            }
        });
        PluginSqlExecutorUtil.closeDataSource();
        LOGGER.info("load {} plugin end. count:{}", pluginInterface.getSimpleName(), pluginList.size());
    }

    /**
     * 校验插件是否支持
     * 1. 插件名称是否重复
     * 2. 插件是否开启
     * 3. 插件类型是否重复
     *
     * @param plugins
     * @param <T>
     */
    private <T extends Plugin> void checkSupport(List<T> plugins) {
        Set<String> pluginNames = new HashSet<>();
        Map<Integer, Object> pluginTypeMap = new HashMap<>();
        plugins.removeIf(plugin -> {
            // 检查插件名称是否重复
            if (!pluginNames.add(plugin.getName())) {
                throw new RuntimeException("plugin name duplicate: " + plugin.getName());
            }

            // 检查插件是否开启
            if (!plugin.support()) {
                LOGGER.info("plugin not support: " + plugin.getName());
                return true;
            }

            // 检查插件类型是否重复
            if (plugin instanceof ElementPlugin) {
                int flowElementType = ((ElementPlugin) plugin).getFlowElementType();
                if (pluginTypeMap.containsKey(flowElementType)) {
                    throw new RuntimeException("plugin type duplicate: " + plugin.getName());
                }
                pluginTypeMap.put(flowElementType, plugin);
            }
            return false;
        });
    }
}
