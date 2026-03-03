package com.didiglobal.turbo.engine.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for copying properties between beans, replacing the Spring-specific
 * {@code org.springframework.beans.BeanUtils.copyProperties()} dependency.
 */
public class BeanUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtil.class);

    private BeanUtil() {
    }

    /**
     * Copy the property values from the source bean into the target bean.
     * Only fields with matching names and compatible types are copied.
     * Static and final fields are ignored.
     *
     * @param source the source bean
     * @param target the target bean
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        Map<String, Field> targetFields = getAllFields(target.getClass());
        Map<String, Field> sourceFields = getAllFields(source.getClass());

        for (Map.Entry<String, Field> entry : targetFields.entrySet()) {
            String name = entry.getKey();
            Field targetField = entry.getValue();
            Field sourceField = sourceFields.get(name);
            if (sourceField == null) {
                continue;
            }
            if (!targetField.getType().isAssignableFrom(sourceField.getType())) {
                continue;
            }
            try {
                sourceField.setAccessible(true);
                targetField.setAccessible(true);
                targetField.set(target, sourceField.get(source));
            } catch (IllegalAccessException e) {
                LOGGER.warn("copyProperties failed for field: {}", name, e);
            }
        }
    }

    private static Map<String, Field> getAllFields(Class<?> clazz) {
        Map<String, Field> fieldMap = new HashMap<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                    continue;
                }
                fieldMap.putIfAbsent(field.getName(), field);
            }
            current = current.getSuperclass();
        }
        return fieldMap;
    }
}
