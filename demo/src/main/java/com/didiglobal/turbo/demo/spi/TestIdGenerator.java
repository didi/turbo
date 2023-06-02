package com.didiglobal.turbo.demo.spi;

import com.didiglobal.turbo.engine.spi.generator.IdGenerator;
import org.apache.commons.lang3.RandomStringUtils;

public class TestIdGenerator implements IdGenerator {

    @Override
    public String getNextId() {
        // Expand your ID generator in this way
        return RandomStringUtils.randomAlphabetic(20);
    }
}
