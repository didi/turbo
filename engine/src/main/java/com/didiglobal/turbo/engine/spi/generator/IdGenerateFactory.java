package com.didiglobal.turbo.engine.spi.generator;

import com.didiglobal.turbo.engine.spi.TurboServiceLoader;

/**
 * id generate factory
 */
public class IdGenerateFactory {

    /**
     * id generator
     */
    private volatile static IdGenerator ID_GENERATOR;

    public static IdGenerator getIdGenerator() {
        loadDefault();
        return ID_GENERATOR;
    }

    private static void loadDefault() {
        if (ID_GENERATOR == null) {
            synchronized (IdGenerateFactory.class) {
                if (ID_GENERATOR == null) {
                    ID_GENERATOR = TurboServiceLoader.getDefaultService(IdGenerator.class);
                }
            }
        }
    }
}
