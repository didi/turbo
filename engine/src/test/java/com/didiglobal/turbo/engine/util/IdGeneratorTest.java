package com.didiglobal.turbo.engine.util;

import com.didiglobal.turbo.engine.spi.generator.IdGenerateFactory;
import com.didiglobal.turbo.engine.spi.generator.IdGenerator;
import com.didiglobal.turbo.engine.spi.generator.StrongUuidGenerator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdGeneratorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdGeneratorTest.class);

    private final IdGenerator idGenerator = IdGenerateFactory.getIdGenerator();

    @Test
    public void testDoExecute() {
        int count = 1000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            String nextId = idGenerator.getNextId();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info(nextId);
            }
        }

        LOGGER.info("generate {} ids, cost {}ms", count, System.currentTimeMillis() - start);
    }
}
