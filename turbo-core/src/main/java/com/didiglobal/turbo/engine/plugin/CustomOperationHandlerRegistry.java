package com.didiglobal.turbo.engine.plugin;

import com.didiglobal.turbo.engine.common.EntityPOEnum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomOperationHandlerRegistry {
    // 用于存储每个实体枚举和其对应的处理器列表的映射
    private static final Map<EntityPOEnum, List<CustomOperationHandler>> handlerMap = new HashMap<>();

    /**
     * 注册自定义操作处理器到特定的实体枚举。
     *
     * @param entity  与自定义处理器关联的实体枚举
     * @param handler 要注册的自定义操作处理器
     */
    public static void registerHandler(EntityPOEnum entity, CustomOperationHandler handler) {
        handlerMap.computeIfAbsent(entity, k -> new ArrayList<>()).add(handler);
    }

    /**
     * 获取与指定实体枚举关联的自定义操作处理器列表。
     *
     * @param entity 需要获取处理器的实体枚举
     * @return 与该实体枚举关联的处理器列表，如果没有找到则返回空列表
     */
    public static List<CustomOperationHandler> getHandlers(EntityPOEnum entity) {
        return handlerMap.getOrDefault(entity, new ArrayList<>());
    }
}
