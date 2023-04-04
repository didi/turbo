package com.didiglobal.turbo.engine.spi.generator;

import com.didiglobal.turbo.engine.spi.SingletonSPI;

/**
 * turbo id generator
 */
@SingletonSPI
public interface IdGenerator {

    /**
     * Get the next ID
     *
     * @return ID
     */
    String getNextId();

}
