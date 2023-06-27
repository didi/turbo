package com.didiglobal.turbo.engine.spi;

import com.google.common.base.Preconditions;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * turbo service loader
 *
 * @param <T> the type of service
 * @author lijinghao
 */
public class TurboServiceLoader<T> {

    private static final Map<Class<?>, TurboServiceLoader<?>> LOADERS = new ConcurrentHashMap<>();

    private final Class<T> serviceInterface;

    @Getter
    private final Collection<T> services;

    private TurboServiceLoader(final Class<T> serviceInterface) {
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
        Preconditions.checkArgument(serviceInterface.isInterface(),
            "SPI interface `%s` is not interface.", serviceInterface);
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> getServiceInterfaces(final Class<T> serviceInterface) {
        TurboServiceLoader<?> result = LOADERS.get(serviceInterface);
        return (Collection<T>) (result != null ? result.getServiceInterfaces()
            : LOADERS.computeIfAbsent(serviceInterface, TurboServiceLoader::new).getServiceInterfaces());
    }

    private Collection<T> getServiceInterfaces() {
        return services;
    }

    public static <T> T getDefaultService(Class<T> tClass) {
        Collection<T> serviceInterfaces = getServiceInterfaces(tClass);
        if (serviceInterfaces.size() == 0) {
            throw new RuntimeException("spi load exception: not found Implementation class of interface " + tClass.getName());
        } else if (serviceInterfaces.size() == 1) {
            return serviceInterfaces.stream().findFirst().get();
        } else {
            // Find the implementation to be used
            Optional<T> optionalIdGenerator = serviceInterfaces.stream()
                .filter(s -> null != s.getClass().getAnnotation(SPIOrder.class))
                .min(Comparator.comparingInt(o -> o.getClass().getAnnotation(SPIOrder.class).value()));
            return optionalIdGenerator.orElseGet(() -> serviceInterfaces.stream().findFirst().get());
        }
    }
}

