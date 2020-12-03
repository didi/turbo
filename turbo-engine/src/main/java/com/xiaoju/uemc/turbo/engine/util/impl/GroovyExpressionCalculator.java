package com.xiaoju.uemc.turbo.engine.util.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import com.xiaoju.uemc.turbo.engine.util.ExpressionCalculator;
import com.xiaoju.uemc.turbo.engine.util.GroovyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Map;

/**
 * 项目名称：turbo
 * 类 名 称：GroovyExpressionCalculator
 * 类 描 述：
 * 创建时间：2020/11/11 5:14 PM
 * 创 建 人：didiwangxing
 */
@Service
public class GroovyExpressionCalculator implements ExpressionCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyExpressionCalculator.class);

    @Override
    public Boolean calculate(String expression, Map<String, Object> dataMap) throws ProcessException {
        if (expression.startsWith("${") && expression.endsWith("}")) {
            expression = expression.substring(2, expression.length() - 1);
        }
        Object result = null;
        try {
            result = GroovyUtil.execute(expression, dataMap);
            if (result instanceof Boolean) {
                return (Boolean) result;
            } else {
                LOGGER.warn("the result of expression is not boolean.||expression={}||result={}||dataMap={}", expression, result, JSON.toJSONString(dataMap));
                throw new ProcessException(ErrorEnum.GROOVY_CALCULATE_FAILED.getErrNo(), "expression is not instanceof bool.");
            }
        } catch (Throwable t) {
            LOGGER.error("calculate expression failed.||message={}||expression={}||dataMap={}, ", t.getMessage(), expression, dataMap, t);
            String groovyExFormat = "{0}: expression={1}";
            throw new ProcessException(ErrorEnum.GROOVY_CALCULATE_FAILED, MessageFormat.format(groovyExFormat, t.getMessage(), expression));
        } finally {
            LOGGER.info("calculate expression.||expression={}||dataMap={}||result={}", expression, JSONObject.toJSONString(dataMap), result);
        }
    }
}
