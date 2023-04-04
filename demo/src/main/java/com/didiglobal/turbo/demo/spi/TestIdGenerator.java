package com.didiglobal.turbo.demo.spi;

import com.didiglobal.turbo.engine.spi.generator.IdGenerator;
import org.apache.commons.lang3.RandomStringUtils;

public class TestIdGenerator implements IdGenerator {

    @Override
    public String getNextId() {
        // 通过这种方式扩展自己的ID生成器
        return RandomStringUtils.randomAlphabetic(20);
    }
}
