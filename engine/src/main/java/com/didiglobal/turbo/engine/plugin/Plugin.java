package com.didiglobal.turbo.engine.plugin;

public interface Plugin {
    // turbo插件开关配置格式建议统一为turbo.plugin.support.${pluginName}
    String PLUGIN_SUPPORT_PREFIX = "turbo.plugin.support.";

    // turbo插件初始化文件配置格式建议统一为turbo.plugin.init_sql.${pluginName}
    String PLUGIN_INIT_SQL_FILE_PREFIX = "turbo.plugin.init_sql.";

    /**
     * 插件名称，唯一标识
     */
    String getName();

    /**
     * 插件开关
     */
    Boolean support();

    /**
     * 插件初始化
     */
    Boolean init();
}
