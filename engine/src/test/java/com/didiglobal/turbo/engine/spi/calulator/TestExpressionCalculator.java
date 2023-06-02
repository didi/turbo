package com.didiglobal.turbo.engine.spi.calulator;

import com.didiglobal.turbo.engine.exception.ProcessException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * test calculator
 */
public class TestExpressionCalculator implements ExpressionCalculator {

    @Override
    public String getType() {
        return "test";
    }

    @Override
    public Boolean calculate(String expression, Map<String, Object> dataMap) throws ProcessException {
        Pattern compile = Pattern.compile(expression);
        Matcher data = compile.matcher((String) dataMap.get("data"));
        return data.matches();
    }
}
