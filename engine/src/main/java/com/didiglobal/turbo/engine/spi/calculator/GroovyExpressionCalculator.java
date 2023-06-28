package com.didiglobal.turbo.engine.spi.calculator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.util.GroovyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Map;

/**
 * Implementation of calculator that uses script based on `Groovy`
 * <p>
 * Expressions can be wrapped with ${} or written without anything
 * ep:
 * ${key_test > 0}  or key_test > 0
 */
public class GroovyExpressionCalculator implements ExpressionCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyExpressionCalculator.class);

    @Override
    public String getType() {
        return "groovy";
    }

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
                LOGGER.warn("the result of expression is not boolean.||expression={}||result={}||dataMap={}",
                    expression, result, JSON.toJSONString(dataMap));
                throw new ProcessException(ErrorEnum.GROOVY_CALCULATE_FAILED.getErrNo(), "expression is not instanceof bool.");
            }
        } catch (Exception e) {
            LOGGER.error("calculate expression failed.||message={}||expression={}||dataMap={}, ", e.getMessage(), expression, dataMap, e);
            String groovyExFormat = "{0}: expression={1}";
            throw new ProcessException(ErrorEnum.GROOVY_CALCULATE_FAILED, MessageFormat.format(groovyExFormat, e.getMessage(), expression));
        } finally {
            LOGGER.info("calculate expression.||expression={}||dataMap={}||result={}", expression, JSONObject.toJSONString(dataMap), result);
        }
    }
}
