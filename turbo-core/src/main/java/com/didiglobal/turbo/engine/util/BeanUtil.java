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
            if (!targetField.getType().isAssignableFrom(sourceField.getType())
                    && !isCompatibleTypes(targetField.getType(), sourceField.getType())) {
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

    /**
     * Returns true if the two types are compatible for assignment, including
     * primitive-to-wrapper and wrapper-to-primitive conversions.
     * <p>
     * This is needed because {@code Integer.class.isAssignableFrom(int.class)} returns
     * {@code false} in Java reflection, even though the types are compatible at the language level.
     */
    private static boolean isCompatibleTypes(Class<?> targetType, Class<?> sourceType) {
        if (targetType == sourceType) return true;
        // Handle primitive <-> wrapper pairs
        if (targetType == int.class) return sourceType == Integer.class;
        if (targetType == Integer.class) return sourceType == int.class;
        if (targetType == long.class) return sourceType == Long.class;
        if (targetType == Long.class) return sourceType == long.class;
        if (targetType == double.class) return sourceType == Double.class;
        if (targetType == Double.class) return sourceType == double.class;
        if (targetType == float.class) return sourceType == Float.class;
        if (targetType == Float.class) return sourceType == float.class;
        if (targetType == boolean.class) return sourceType == Boolean.class;
        if (targetType == Boolean.class) return sourceType == boolean.class;
        if (targetType == byte.class) return sourceType == Byte.class;
        if (targetType == Byte.class) return sourceType == byte.class;
        if (targetType == short.class) return sourceType == Short.class;
        if (targetType == Short.class) return sourceType == short.class;
        if (targetType == char.class) return sourceType == Character.class;
        if (targetType == Character.class) return sourceType == char.class;
        return false;
    }
}
