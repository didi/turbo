package com.didiglobal.turbo.engine.util;

import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.spi.calulator.ExpressionCalculator;
import com.didiglobal.turbo.engine.spi.calulator.ExpressionCalculatorFactory;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author lijinghao
 * @version v1.0
 * @date 2023/5/30 2:55 PM
 */
public class ExpressionCalculatorTest {

    private final ExpressionCalculator expressionCalculator = ExpressionCalculatorFactory.getExpressionCalculator();

    @Test
    public void testExpressionCalculator() throws ProcessException {
        String expression = "str.length() > 2";
        Map<String, Object> data = Maps.newHashMap();
        data.put("str", "1234");
        Boolean result = expressionCalculator.calculate(expression, data);
        Assert.assertTrue(result);
    }
}
