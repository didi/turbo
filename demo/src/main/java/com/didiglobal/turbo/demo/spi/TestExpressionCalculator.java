package com.didiglobal.turbo.demo.spi;

import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.spi.calculator.ExpressionCalculator;
import com.didiglobal.turbo.engine.spi.calculator.ExpressionCalculatorFactory;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Map;

public class TestExpressionCalculator implements ExpressionCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestExpressionCalculator.class);

    @Override
    public String getType() {
        return "test";
    }

    @Override
    public Boolean calculate(String expression, Map<String, Object> dataMap) throws ProcessException {
        // You can customize the implementation,
        // and the code here serves as an example of extending the calculation expression
        // Because all calculations in the demo use groovy, the groovy method will also be used here
        LOGGER.info("enter TestExpressionCalculator, but use groovy for test");
        return ExpressionCalculatorFactory.getExpressionCalculator("groovy").calculate(expression, dataMap);
    }

    /**
     * test
     *
     * @param args
     */
    public static void main(String[] args) {
        TestExpressionCalculator testExpressionCalculator = new TestExpressionCalculator();
        String expression = "[0-9]";
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("data", "1");
        try {
            Boolean result = testExpressionCalculator.calculate(expression, dataMap);
            Assert.isTrue(result, "test fail");
        } catch (ProcessException e) {
            e.printStackTrace();
        }
    }
}
