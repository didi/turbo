package com.didiglobal.turbo.engine.spi;

import java.lang.annotation.*;

/**
 * Used to specify SPI order.
 * @author lijinghao
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SPIOrder {

    /**
     * The smallest order will be used as the default implementation,
     * and if the orders are the same,
     * the outermost loaded one will be used as the default implementation
     *
     * @return order value
     */
    int value() default Integer.MAX_VALUE;
}
