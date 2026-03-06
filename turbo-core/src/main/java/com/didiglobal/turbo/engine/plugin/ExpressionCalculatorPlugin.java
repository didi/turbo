package com.didiglobal.turbo.engine.plugin;

import com.didiglobal.turbo.engine.util.ExpressionCalculator;

public interface ExpressionCalculatorPlugin extends Plugin{
    ExpressionCalculator getExpressionCalculator();
}
