package com.didiglobal.turbo.engine.util;

import com.didiglobal.turbo.engine.runner.BaseTest;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class GroovyUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    /**
     * Exception:
     * int i = i;
     * String String == '111';
     * String.equals111('123')
     * int a = '123';
     * curl http://www.a.com
     * abc
     * 1/0
     *
     */

    @Test
    public void fun1(){
        try {
            String expression = "";
            Map<String, Object> dataMap = Maps.newHashMap();
            Object result = GroovyUtil.execute(expression, dataMap);
            LOGGER.warn("result:{}:{}", result.getClass().getSimpleName(), result);
        } catch (Exception e) {
            LOGGER.warn("catch exception", e);
        }
    }
}
