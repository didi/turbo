package com.didiglobal.turbo.engine.spi.calculator;

import org.junit.Assert;
import org.junit.Test;

public class ExpressionCalculatorFactoryTest {

    @Test
    public void assertGroovyExpressionCalculatorExists() {
        Assert.assertTrue(ExpressionCalculatorFactory.contains("groovy"));
        Assert.assertTrue(ExpressionCalculatorFactory.contains("pattern"));
        Assert.assertTrue(ExpressionCalculatorFactory.contains("test"));
    }


    @Test
    public void assertNotExists() {
        Assert.assertFalse(ExpressionCalculatorFactory.contains("Tn3UI7"));
    }

    @Test
    public void assertExpressionCalculatorExistsWithNoType(){
        ExpressionCalculator defaultExpressionCalculator = ExpressionCalculatorFactory.getExpressionCalculator("");
        Assert.assertNotNull(defaultExpressionCalculator);
    }

    @Test
    public void assertDefaultExpressionCalculatorNotGroovy(){
        ExpressionCalculator defaultExpressionCalculator = ExpressionCalculatorFactory.getExpressionCalculator("");
        Assert.assertFalse(defaultExpressionCalculator instanceof GroovyExpressionCalculator);
    }

}