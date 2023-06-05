package com.didiglobal.turbo.engine.spi.calculator;

import com.didiglobal.turbo.engine.exception.ProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * test calculator
 */
public class TestExpressionCalculator implements ExpressionCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestExpressionCalculator.class);

    @Override
    public String getType() {
        return "test";
    }

    @Override
    public Boolean calculate(String expression, Map<String, Object> dataMap) throws ProcessException {
       LOGGER.info("enter TestExpressionCalculator, but use groovy for test");
       return ExpressionCalculatorFactory.getExpressionCalculator("groovy").calculate(expression, dataMap);
    }
}
