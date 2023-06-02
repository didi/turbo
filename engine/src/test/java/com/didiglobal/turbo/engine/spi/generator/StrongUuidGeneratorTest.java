package com.didiglobal.turbo.engine.spi.generator;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StrongUuidGeneratorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StrongUuidGeneratorTest.class);

    private final IdGenerator idGenerator = IdGenerateFactory.getIdGenerator();

    @Test
    public void testDoExecute() {
        int count = 1000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            String nextId = idGenerator.getNextId();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(nextId);
            }
        }

        LOGGER.info("generate {} ids, cost {}ms", count, System.currentTimeMillis() - start);
    }
}
