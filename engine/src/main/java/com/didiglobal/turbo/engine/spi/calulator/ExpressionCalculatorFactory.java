package com.didiglobal.turbo.engine.spi.calulator;

import com.didiglobal.turbo.engine.spi.TurboServiceLoader;

import java.util.Collection;
import java.util.Optional;

/**
 * expression calculator manager
 * @author lijinghao
 */
public class ExpressionCalculatorFactory {

    /**
     * expression calculator
     */
    private static final ExpressionCalculator EXPRESSION_CALCULATOR;

    static {
        Collection<ExpressionCalculator> serviceInterfaces = TurboServiceLoader.getServiceInterfaces(ExpressionCalculator.class);
        // In the order in which the services are loaded, take the first implementation
        Optional<ExpressionCalculator> optional = serviceInterfaces.stream().findFirst();
        if(optional.isPresent()){
            EXPRESSION_CALCULATOR = optional.get();
        } else {
            throw new RuntimeException("spi load exception: not found Implementation class of interface ExpressionCalculator");
        }
    }

    public static ExpressionCalculator getExpressionCalculator(){
        return EXPRESSION_CALCULATOR;
    }
}
