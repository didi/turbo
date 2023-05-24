package com.didiglobal.turbo.engine.spi.generator;

import com.didiglobal.turbo.engine.spi.TurboServiceLoader;

import java.util.Collection;
import java.util.Optional;

/**
 * id generate factory
 *
 * @author lijinghao
 */
public class IdGenerateFactory {

    /**
     * id generator
     */
    private static final IdGenerator ID_GENERATOR;

    static {
        Collection<IdGenerator> serviceInterfaces = TurboServiceLoader.getServiceInterfaces(IdGenerator.class);
        // In the order in which the services are loaded, take the first implementation
        Optional<IdGenerator> optional = serviceInterfaces.stream().findFirst();
        if (optional.isPresent()) {
            ID_GENERATOR = optional.get();
        } else {
            throw new RuntimeException("spi load exception: not found Implementation class of interface IdGenerator");
        }
    }

    public static IdGenerator getIdGenerator() {
        return ID_GENERATOR;
    }
}
