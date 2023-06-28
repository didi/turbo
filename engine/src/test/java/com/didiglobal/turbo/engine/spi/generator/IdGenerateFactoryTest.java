package com.didiglobal.turbo.engine.spi.generator;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class IdGenerateFactoryTest {

    @Test
    public void assertExists() {
        IdGenerator idGenerator = IdGenerateFactory.getIdGenerator();
        Assert.assertNotNull(idGenerator);
        Assert.assertTrue(StringUtils.isNotBlank(idGenerator.getNextId()));
    }

}
