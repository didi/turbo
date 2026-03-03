package com.didiglobal.turbo.engine.config;

import com.didiglobal.turbo.engine.plugin.ExpressionCalculatorPlugin;
import com.didiglobal.turbo.engine.plugin.IdGeneratorPlugin;
import com.didiglobal.turbo.engine.plugin.manager.DefaultPluginManager;
import com.didiglobal.turbo.engine.plugin.manager.PluginManager;
import com.didiglobal.turbo.engine.plugin.manager.TurboBeanFactory;
import com.didiglobal.turbo.engine.util.ExpressionCalculator;
import com.didiglobal.turbo.engine.util.IdGenerator;
import com.didiglobal.turbo.engine.util.StrongUuidGenerator;
import com.didiglobal.turbo.engine.util.impl.GroovyExpressionCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

import java.util.List;

@Configuration
public class PluginConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginConfig.class);
    @Value("${turbo.plugin.manager.custom-class:#{null}}")
    private String customManagerClass;

    @Resource
    private DefaultListableBeanFactory defaultListableBeanFactory;

    /**
     * Spring adapter that wraps DefaultListableBeanFactory into TurboBeanFactory.
     */
    private TurboBeanFactory turboBeanFactory() {
        return new TurboBeanFactory() {
            @Override
            public <T> T getBean(Class<T> requiredType) {
                return defaultListableBeanFactory.getBean(requiredType);
            }
        };
    }

    /**
     * 若指定了自定义的PluginManager，则使用指定的，否则使用默认的DefaultPluginManager
     * @return
     */
    @Bean
    public PluginManager getPluginManager() {
        TurboBeanFactory beanFactory = turboBeanFactory();
        if (null == customManagerClass) {
            LOGGER.info("No custom PluginManager specified, using default PluginManager.");
            return new DefaultPluginManager(beanFactory);
        } else {
            try {
                Class<?> clazz = Class.forName(customManagerClass);
                return (PluginManager) clazz.getDeclaredConstructor(TurboBeanFactory.class).newInstance(beanFactory);
            } catch (Exception e) {
                throw new RuntimeException("Failed to instantiate custom PluginManager", e);
            }
        }
    }

    /**
     *  优先从插件中获取表达式计算器，如有多个仅使用第一个，若未找到则使用默认实现
     * @param pluginManager
     * @return
     */
    @Bean
    public ExpressionCalculator getExpressionCalculator(PluginManager pluginManager) {
        List<ExpressionCalculatorPlugin> expressionCalculatorPlugins = pluginManager.getPluginsFor(ExpressionCalculatorPlugin.class);
        if (!expressionCalculatorPlugins.isEmpty()) {
            LOGGER.info("Found expression calculator plugin: {}", expressionCalculatorPlugins.get(0).getName());
            return expressionCalculatorPlugins.get(0).getExpressionCalculator();
        }
        return new GroovyExpressionCalculator();
    }

    /**
     * 优先从插件中获取id生成器，如有多个仅使用第一个，若未找到则使用默认实现
     * @param pluginManager
     * @return
     */
    @Bean
    public IdGenerator getIdGenerator(PluginManager pluginManager) {
        List<IdGeneratorPlugin> expressionCalculatorPlugins = pluginManager.getPluginsFor(IdGeneratorPlugin.class);
        if (!expressionCalculatorPlugins.isEmpty()) {
            LOGGER.info("Found id generator plugin: {}", expressionCalculatorPlugins.get(0).getName());
            return expressionCalculatorPlugins.get(0).getIdGenerator();
        }
        return new StrongUuidGenerator();
    }
}
