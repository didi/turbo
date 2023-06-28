package com.didiglobal.turbo.engine.spi.calculator;

import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.exception.TurboException;
import com.didiglobal.turbo.engine.spi.TurboServiceLoader;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * expression calculator factory
 */
public class ExpressionCalculatorFactory {

    /**
     * default expression calculator
     */
    private volatile static ExpressionCalculator DEFAULT_EXPRESSION_CALCULATOR;

    /**
     * all calculators
     */
    private static final Map<String, ExpressionCalculator> CALCULATORS = new LinkedHashMap<>();

    public static void load() {
        Collection<ExpressionCalculator> serviceInterfaces = TurboServiceLoader.getServiceInterfaces(ExpressionCalculator.class);
        serviceInterfaces.forEach(service -> CALCULATORS.put(service.getType(), service));
    }

    public static void loadDefault() {
        if (DEFAULT_EXPRESSION_CALCULATOR == null) {
            synchronized (ExpressionCalculatorFactory.class) {
                if (DEFAULT_EXPRESSION_CALCULATOR == null) {
                    DEFAULT_EXPRESSION_CALCULATOR = TurboServiceLoader.getDefaultService(ExpressionCalculator.class);
                }
            }
        }
    }

    public static boolean contains(String type) {
        return CALCULATORS.containsKey(type);
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
            loadDefault();
            return DEFAULT_EXPRESSION_CALCULATOR;
        }

        if (CALCULATORS.isEmpty()) {
            load();
        }

        if (!contains(type)) {
            throw new TurboException(ErrorEnum.NOT_FOUND_EXPRESSION_CALCULATOR,
                String.format("Not found expression calculator for %s", type));
        }
        return CALCULATORS.get(type);
    }

}
