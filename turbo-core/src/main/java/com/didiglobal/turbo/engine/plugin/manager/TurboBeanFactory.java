package com.didiglobal.turbo.engine.plugin.manager;

/**
 * Simple bean factory interface, decoupled from Spring's {@code DefaultListableBeanFactory}.
 * Implementations can delegate to a Spring container or any other DI mechanism.
 */
public interface TurboBeanFactory {

    /**
     * Return the bean instance that uniquely matches the given object type.
     *
     * @param requiredType type the bean must match
     * @param <T>          the type of the bean
     * @return the matching bean instance
     */
    <T> T getBean(Class<T> requiredType);
}
