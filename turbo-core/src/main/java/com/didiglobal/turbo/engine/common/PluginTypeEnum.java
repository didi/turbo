package com.didiglobal.turbo.engine.common;

import com.didiglobal.turbo.engine.plugin.ElementPlugin;
import com.didiglobal.turbo.engine.plugin.ExpressionCalculatorPlugin;
import com.didiglobal.turbo.engine.plugin.IdGeneratorPlugin;
import com.didiglobal.turbo.engine.plugin.ListenerPlugin;
import com.didiglobal.turbo.engine.plugin.Plugin;

public enum PluginTypeEnum {
    EXPRESSION_CALCULATOR_PLUGIN("expressionCalculatorPlugin", ExpressionCalculatorPlugin.class),
    ELEMENT_PLUGIN("elementPlugin", ElementPlugin.class),
    ID_GENERATOR_PLUGIN("idGeneratorPlugin", IdGeneratorPlugin.class),
    LISTENER_PLUGIN("listenerPlugin", ListenerPlugin.class);

    private String pluginType;
    private Class<? extends Plugin> pluginClass;

    PluginTypeEnum(String pluginType, Class<? extends Plugin> pluginClass) {
        this.pluginType = pluginType;
        this.pluginClass = pluginClass;
    }

    public String getPluginType() {
        return pluginType;
    }

    public void setPluginType(String pluginType) {
        this.pluginType = pluginType;
    }

    public Class<? extends Plugin> getPluginClass() {
        return pluginClass;
    }

    public void setPluginClass(Class<? extends Plugin> pluginClass) {
        this.pluginClass = pluginClass;
    }
}
