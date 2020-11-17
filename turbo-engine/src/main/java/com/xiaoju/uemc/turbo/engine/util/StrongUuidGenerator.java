package com.xiaoju.uemc.turbo.engine.util;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

/**
 * Created by Stefanie on 2019/11/26.
 */
public class StrongUuidGenerator implements IdGenerator {

    private static volatile StrongUuidGenerator singleGenerator;

    // different ProcessEngines on the same classloader share one generator.
    private static TimeBasedGenerator timeBasedGenerator;

    private StrongUuidGenerator() {
        initGenerator();
    }

    public static StrongUuidGenerator getInstance() {
        if (singleGenerator == null) {
            synchronized (StrongUuidGenerator.class) {
                if (singleGenerator == null) {
                    singleGenerator = new StrongUuidGenerator();
                }
            }
        }
        return singleGenerator;
    }

    private void initGenerator() {
        if (timeBasedGenerator == null) {
            synchronized (StrongUuidGenerator.class) {
                if (timeBasedGenerator == null) {
                    timeBasedGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
                }
            }
        }
    }

    public String getNextId() {
        return timeBasedGenerator.generate().toString();
    }

}
