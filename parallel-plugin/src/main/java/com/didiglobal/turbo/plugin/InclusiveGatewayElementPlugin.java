package com.didiglobal.turbo.plugin;

import com.didiglobal.turbo.engine.executor.ElementExecutor;
import com.didiglobal.turbo.engine.plugin.ElementPlugin;
import com.didiglobal.turbo.engine.util.PluginPropertiesUtil;
import com.didiglobal.turbo.engine.validator.ElementValidator;
import com.didiglobal.turbo.plugin.common.ExtendFlowElementType;
import com.didiglobal.turbo.plugin.executor.InclusiveGatewayExecutor;
import com.didiglobal.turbo.plugin.validator.InclusiveGatewayValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class InclusiveGatewayElementPlugin extends com.didiglobal.turbo.spring.plugin.BasePlugin implements ElementPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(InclusiveGatewayElementPlugin.class);
    private static final String PLUGIN_NAME = "InclusiveGatewayElementPlugin";
    public static Integer elementType = ExtendFlowElementType.INCLUSIVE_GATEWAY;

    public InclusiveGatewayElementPlugin() {
        String elementType = PluginPropertiesUtil.getPropertyValue(ELEMENT_TYPE_PREFIX + PLUGIN_NAME);
        if (elementType != null) {
            InclusiveGatewayElementPlugin.elementType = Integer.valueOf(elementType);
        }
    }

    @Override
    public ElementExecutor getElementExecutor() {
        return beanFactory.getBean(InclusiveGatewayExecutor.class);
    }

    @Override
    public ElementValidator getElementValidator() {
        return beanFactory.getBean(InclusiveGatewayValidator.class);
    }

    @Override
    public Integer getFlowElementType() {
        return elementType;
    }

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public Boolean support() {
        return PluginPropertiesUtil.getPropertyValue(PLUGIN_SUPPORT_PREFIX + PLUGIN_NAME, "true").equals("true");
    }

    @Override
    public Boolean init() {
        String sqlFilePath = PluginPropertiesUtil.getPropertyValue(PLUGIN_INIT_SQL_FILE_PREFIX + PLUGIN_NAME);
        try {
            com.didiglobal.turbo.spring.plugin.PluginSqlExecutorUtil.executeSqlFile(Objects.requireNonNull(getClass().getClassLoader().getResource(sqlFilePath)).getPath(), true, null);
        } catch (Exception e) {
            LOGGER.warn("init plugin failed", e);
            return false;
        }
        return true;
    }
}
