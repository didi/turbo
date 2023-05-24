package com.didiglobal.turbo.engine.spi.generator;

import com.didiglobal.turbo.engine.spi.SingletonSPI;

/**
 * turbo id generator
 */
@SingletonSPI
public interface IdGenerator {

    /**
     * get the next ID
     *
     * @return ID
     */
    String getNextId();

}
