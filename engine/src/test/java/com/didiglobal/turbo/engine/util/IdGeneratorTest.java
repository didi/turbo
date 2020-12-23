package com.didiglobal.turbo.engine.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdGeneratorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdGeneratorTest.class);

    private IdGenerator idGenerator = new StrongUuidGenerator();

    @Test
    public void testDoExecute() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            LOGGER.info("{}", idGenerator.getNextId());
        }

        LOGGER.info("{}", System.currentTimeMillis() - start);
    }
}
