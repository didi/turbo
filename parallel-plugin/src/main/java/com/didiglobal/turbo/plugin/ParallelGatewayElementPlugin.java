package com.didiglobal.turbo.plugin;

import com.didiglobal.turbo.engine.executor.ElementExecutor;
import com.didiglobal.turbo.engine.plugin.ElementPlugin;
import com.didiglobal.turbo.engine.plugin.manager.BasePlugin;
import com.didiglobal.turbo.engine.util.PluginSqlExecutorUtil;
import com.didiglobal.turbo.engine.validator.ElementValidator;
import com.didiglobal.turbo.plugin.common.ExtendFlowElementType;
import com.didiglobal.turbo.plugin.executor.ParallelGatewayExecutor;
import com.didiglobal.turbo.engine.util.PluginPropertiesUtil;
import com.didiglobal.turbo.plugin.validator.ParallelGatewayValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ParallelGatewayElementPlugin extends BasePlugin implements ElementPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelGatewayElementPlugin.class);
    private static final String PLUGIN_NAME = "ParallelGatewayElementPlugin";
    public static Integer elementType = ExtendFlowElementType.PARALLEL_GATEWAY;
    public ParallelGatewayElementPlugin() {
        String elementType = PluginPropertiesUtil.getPropertyValue(ELEMENT_TYPE_PREFIX + PLUGIN_NAME);
        if (elementType != null) {
            ParallelGatewayElementPlugin.elementType = Integer.valueOf(elementType);
        }
    }
    @Override
    public ElementExecutor getElementExecutor() {
        return beanFactory.getBean(ParallelGatewayExecutor.class);
    }

    @Override
    public ElementValidator getElementValidator() {
        return beanFactory.getBean(ParallelGatewayValidator.class);
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
            PluginSqlExecutorUtil.executeSqlFile(Objects.requireNonNull(getClass().getClassLoader().getResource(sqlFilePath)).getPath(), true, null);
        } catch (Exception e) {
            LOGGER.warn("init plugin failed", e);
            return false;
        }
        return true;
    }
}
