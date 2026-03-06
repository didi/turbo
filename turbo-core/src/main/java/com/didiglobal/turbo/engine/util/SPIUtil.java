package com.didiglobal.turbo.engine.util;

import com.didiglobal.turbo.engine.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class SPIUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SPIUtil.class);

    public static <T> T loadService(Class<T> serviceInterface) {
        ServiceLoader<T> loader = ServiceLoader.load(serviceInterface);
        Iterator<T> iterator = loader.iterator();
        if (iterator.hasNext()) {
            LOGGER.info("load service:{}", serviceInterface.getName());
            return iterator.next();
        }
        return null;
    }

    public static <T extends Plugin> List<Plugin> loadAllServices(Class<T> serviceInterface) {
        List<Plugin> list = new ArrayList<>();
        Iterator<T> iterator = ServiceLoader.load(serviceInterface).iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        LOGGER.info("load all {} services:{}", serviceInterface.getSimpleName(), list);
        return list;
    }
}
