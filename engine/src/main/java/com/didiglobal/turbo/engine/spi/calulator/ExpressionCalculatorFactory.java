package com.didiglobal.turbo.engine.spi.calulator;

import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.exception.TurboException;
import com.didiglobal.turbo.engine.spi.TurboServiceLoader;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * expression calculator factory
 */
public class ExpressionCalculatorFactory {

    /**
     * default expression calculator
     */
    private static final ExpressionCalculator DEFAULT_EXPRESSION_CALCULATOR;

    /**
     * all calculators
     */
    private static final Map<String, ExpressionCalculator> CALCULATORS = new LinkedHashMap<>();

    static {
        Collection<ExpressionCalculator> serviceInterfaces = TurboServiceLoader.getServiceInterfaces(ExpressionCalculator.class);
        serviceInterfaces.forEach(service -> CALCULATORS.put(service.getType(), service));
        // In the order in which the services are loaded, take the first implementation as default
        Optional<ExpressionCalculator> optional = serviceInterfaces.stream().findFirst();
        if (optional.isPresent()) {
            DEFAULT_EXPRESSION_CALCULATOR = optional.get();
        } else {
            throw new RuntimeException("spi load exception: not found Implementation class of interface ExpressionCalculator");
        }
    }

    /**
     * Get the calculator by type
     * <p>
     * If no type is passed in, use the first calculator loaded by SPI
     *
     * @param type type of calculator
     * @return ExpressionCalculator
     */
    public static ExpressionCalculator getExpressionCalculator(String type) {
        if (StringUtils.isBlank(type)) {
            return DEFAULT_EXPRESSION_CALCULATOR;
        }
        if (!CALCULATORS.containsKey(type)) {
            throw new TurboException(ErrorEnum.NOT_FOUND_EXPRESSION_CALCULATOR,
                String.format("Not found expression calculator for %s", type));
        }
        return CALCULATORS.get(type);
    }

    public static boolean contains(String type) {
        return CALCULATORS.containsKey(type);
    }
}
