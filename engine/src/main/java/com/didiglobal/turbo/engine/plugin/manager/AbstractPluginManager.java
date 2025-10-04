package com.didiglobal.turbo.engine.plugin.manager;

import com.didiglobal.turbo.engine.common.PluginTypeEnum;
import com.didiglobal.turbo.engine.plugin.ElementPlugin;
import com.didiglobal.turbo.engine.plugin.ExpressionCalculatorPlugin;
import com.didiglobal.turbo.engine.plugin.IdGeneratorPlugin;
import com.didiglobal.turbo.engine.plugin.ListenerPlugin;
import com.didiglobal.turbo.engine.plugin.Plugin;
import com.didiglobal.turbo.engine.util.SPIUtil;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractPluginManager implements PluginManager {
    protected Map<String, List<? extends Plugin>> pluginMap = new HashMap<>();
    protected DefaultListableBeanFactory beanFactory;

    public AbstractPluginManager() {
    }

    /**
     * 通过SPI方式加载插件，目前支持ElementPlugin、IdGeneratorPlugin、ExpressionCalculatorPlugin、ListenerPlugin四类插件
     */
    protected void loadPlugins() {
        pluginMap.put(PluginTypeEnum.ELEMENT_PLUGIN.getPluginType(), SPIUtil.loadAllServices(ElementPlugin.class));
        pluginMap.put(PluginTypeEnum.ID_GENERATOR_PLUGIN.getPluginType(), SPIUtil.loadAllServices(IdGeneratorPlugin.class));
        pluginMap.put(PluginTypeEnum.EXPRESSION_CALCULATOR_PLUGIN.getPluginType(), SPIUtil.loadAllServices(ExpressionCalculatorPlugin.class));
        pluginMap.put(PluginTypeEnum.LISTENER_PLUGIN.getPluginType(), SPIUtil.loadAllServices(ListenerPlugin.class));
    }

    /**
     * 初始化插件，可通过重写 initialize() 方法自定义插件初始化逻辑
     */
    protected void initialize() {
    }

    @Override
    public List<Plugin> getPlugins() {
        List<Plugin> allPlugins = new ArrayList<>();
        pluginMap.values().forEach(t -> allPlugins.add((Plugin) t));
        return allPlugins;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Plugin> List<T> getPluginsFor(Class<T> pluginInterface) {
        return switch (pluginInterface.getSimpleName()) {
            case "ElementPlugin" -> (List<T>) pluginMap.get(PluginTypeEnum.ELEMENT_PLUGIN.getPluginType());
            case "IdGeneratorPlugin" -> (List<T>) pluginMap.get(PluginTypeEnum.ID_GENERATOR_PLUGIN.getPluginType());
            case "ExpressionCalculatorPlugin" ->
                    (List<T>) pluginMap.get(PluginTypeEnum.EXPRESSION_CALCULATOR_PLUGIN.getPluginType());
            case "ListenerPlugin" -> (List<T>) pluginMap.get(PluginTypeEnum.LISTENER_PLUGIN.getPluginType());
            default -> new ArrayList<>();
        };
    }

    @Override
    public Integer countPlugins() {
        return pluginMap.values().stream().mapToInt(List::size).sum();
    }

    @Override
    public <T extends Plugin> Boolean containsPlugin(Class<T> pluginInterface, String pluginName) {
        List<T> plugins = getPluginsFor(pluginInterface);
        return plugins.stream().anyMatch(plugin -> plugin.getName().equals(pluginName));
    }
}