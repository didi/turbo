package com.didiglobal.turbo.engine.spi.generator;

import com.didiglobal.turbo.engine.spi.TurboServiceLoader;

/**
 * id generate factory
 *
 * @author lijinghao
 */
public class IdGenerateFactory {

    /**
     * id generator
     */
    private static final IdGenerator ID_GENERATOR = TurboServiceLoader.getDefaultService(IdGenerator.class);

    public static IdGenerator getIdGenerator() {
        return ID_GENERATOR;
    }
}
