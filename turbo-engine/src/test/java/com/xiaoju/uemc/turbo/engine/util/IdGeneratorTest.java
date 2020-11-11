package com.xiaoju.uemc.turbo.engine.util;

import org.junit.Test;

/**
 * Created by Stefanie on 2020/11/11.
 */
public class IdGeneratorTest {

    private IdGenerator idGenerator = StrongUuidGenerator.getInstance();

    @Test
    public void testDoExecute() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            System.out.println(idGenerator.getNextId());
        }

        System.out.println(System.currentTimeMillis() - start);
    }
}
