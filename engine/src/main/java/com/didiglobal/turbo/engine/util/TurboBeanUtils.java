package com.didiglobal.turbo.engine.util;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author fxz
 */
public class TurboBeanUtils {

    public static void copyProperties(Object source, Object target) {
        try {
            PropertyUtils.copyProperties(target, source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
