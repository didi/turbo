package com.didiglobal.turbo.engine.plugin.manager;

/**
 * Base class for plugins that need access to a bean factory.
 */
public class BasePlugin {
    protected TurboBeanFactory beanFactory;

    public TurboBeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(TurboBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
