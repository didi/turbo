package com.didiglobal.turbo.engine.spi;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * turbo service loader
 *
 * @author lijinghao
 * @param <T> the type of service
 */
public class TurboServiceLoader<T> {

    private static final Map<Class<?>, TurboServiceLoader<?>> LOADERS = new ConcurrentHashMap<>();

    private final Class<T> serviceInterface;

    @Getter
    private final Collection<T> services;

    private TurboServiceLoader(final Class<T> serviceInterface){
        this.serviceInterface = serviceInterface;
        validate();
        services = load();
    }

    private Collection<T> load() {
        Collection<T> result = new LinkedList<>();
        for (T each : ServiceLoader.load(serviceInterface)) {
            result.add(each);
        }
        return result;
    }

    private void validate() {
        Preconditions.checkNotNull(serviceInterface, "SPI interface is null.");
        Preconditions.checkArgument(serviceInterface.isInterface(), "SPI interface `%s` is not interface.", serviceInterface);
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> getServiceInterfaces(final Class<T> serviceInterface){
        TurboServiceLoader<?> result = LOADERS.get(serviceInterface);
        return (Collection<T>) (result != null ? result.getServiceInterfaces(): LOADERS.computeIfAbsent(serviceInterface, TurboServiceLoader::new).getServiceInterfaces());

    }

    private Collection<T> getServiceInterfaces() {
        return serviceInterface.getAnnotation(SingletonSPI.class) == null ? createNewServiceInstances() : getSingletonServiceInstances();

    }

    private Collection<T> getSingletonServiceInstances() {
        return services;

    }

    @SneakyThrows(ReflectiveOperationException.class)
    @SuppressWarnings("unchecked")
    private Collection<T> createNewServiceInstances() {
        Collection<T> result = new LinkedList<>();
        for (Object each : services) {
            result.add((T) each.getClass().getDeclaredConstructor().newInstance());
        }
        return result;
    }
}

