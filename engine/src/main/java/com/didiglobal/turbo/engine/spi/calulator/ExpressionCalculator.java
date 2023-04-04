package com.didiglobal.turbo.engine.spi.calulator;

import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.spi.SingletonSPI;

import java.util.Map;

/**
 * turbo outgoing condition calculator
 */
@SingletonSPI
public interface ExpressionCalculator {

    /**
     * Execute the conditional expression and get the result of the evaluation
     *
     * @param expression conditional expression
     * @param dataMap    data for calculate
     * @return true or false
     * @throws ProcessException the exception thrown during execution
     */
    Boolean calculate(String expression, Map<String, Object> dataMap) throws ProcessException;
}
