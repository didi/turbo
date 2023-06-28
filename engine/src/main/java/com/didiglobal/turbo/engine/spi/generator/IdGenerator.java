package com.didiglobal.turbo.engine.spi.generator;

/**
 * turbo id generator
 */
public interface IdGenerator {

    /**
     * get the next ID
     *
     * @return ID
     */
    String getNextId();

}
