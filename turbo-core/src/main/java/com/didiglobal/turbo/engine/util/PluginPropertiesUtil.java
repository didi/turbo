package com.didiglobal.turbo.engine.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

public class PluginPropertiesUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginPropertiesUtil.class);

    // 用于缓存所有加载并合并后的配置属性
    private static Properties cachedProperties;

    // 标记是否已经完成了配置文件的加载和合并处理
    private static boolean isLoaded = false;

    private static void loadAllPluginProperties() {
        if (!isLoaded) {
            cachedProperties = new Properties();
            try {
                // 通过类加载器获取所有plugin.properties文件的资源URL
                Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources("plugin.properties");
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    Properties properties = new Properties();
                    try (InputStream input = url.openStream()) {
                        properties.load(input);
                        cachedProperties.putAll(properties);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("load plugin.properties error", e);
            }
            isLoaded = true;
        }
    }

    public static String getPropertyValue(String key, String defaultValue) {
        if (!isLoaded) {
            loadAllPluginProperties();
        }
        if (cachedProperties.get(key) == null) {
            return defaultValue;
        }
        return cachedProperties.getProperty(key);
    }

    public static String getPropertyValue(String key) {
        if (!isLoaded) {
            loadAllPluginProperties();
        }
        return cachedProperties.getProperty(key);
    }
}