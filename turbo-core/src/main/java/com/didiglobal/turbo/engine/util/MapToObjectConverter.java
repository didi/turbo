package com.didiglobal.turbo.engine.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MapToObjectConverter {

    public static <T> T convertMapToObject(Map<String, Object> map, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        T obj = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (map.containsKey(field.getName())) {
                Object value = map.get(field.getName());
                if (value!= null && field.getType().isAssignableFrom(value.getClass())) {
                    field.set(obj, value);
                }
            }
        }
        return obj;
    }

    public static Map<String, Object> convertObjectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }
        return map;
    }
}
