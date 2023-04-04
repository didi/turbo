package com.didiglobal.turbo.engine.spi.calulator;

import com.didiglobal.turbo.engine.spi.TurboServiceLoader;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * expression calculator manager
 */
@Service
public class ExpressionCalculatorManager {

    /**
     * expression calculator
     */
    private ExpressionCalculator expressionCalculator;

    public ExpressionCalculator getExpressionCalculator(){
        if(expressionCalculator != null){
            return expressionCalculator;
        }

        synchronized (ExpressionCalculatorManager.class){
            Collection<ExpressionCalculator> serviceInterfaces = TurboServiceLoader.getServiceInterfaces(ExpressionCalculator.class);
            // In the order in which the services are loaded, take the first implementation
            Optional<ExpressionCalculator> optional = serviceInterfaces.stream().findFirst();
            if(optional.isPresent()){
                expressionCalculator = optional.get();
                return expressionCalculator;
            }
            return null;
        }
    }
}
