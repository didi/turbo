package com.xiaoju.uemc.turbo.engine.runner;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Stefanie on 2019/12/1.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestEngineApplication.class)
public class BaseTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);
}
