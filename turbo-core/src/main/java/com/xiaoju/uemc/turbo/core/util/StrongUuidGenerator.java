package com.xiaoju.uemc.turbo.core.util;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

/**
 * Created by Stefanie on 2019/11/26.
 */
public class StrongUuidGenerator {

    private static volatile StrongUuidGenerator singleGenerator;

    // different ProcessEngines on the same classloader share one generator.
    protected static TimeBasedGenerator timeBasedGenerator;

    public StrongUuidGenerator() {
        initGenerator();
    }

    public static StrongUuidGenerator getInstance() {
        if (singleGenerator == null) {
            synchronized (StrongUuidGenerator.class) {
                if (singleGenerator == null) {
                    return new StrongUuidGenerator();
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

    public static String getNextId() {
        return timeBasedGenerator.generate().toString();
    }

//    public static void main(String[] args) {
//        StrongUuidGenerator snowFlake = new StrongUuidGenerator();
//
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 1000000; i++) {
//            System.out.println(snowFlake.getNextId());
//        }
//
//        System.out.println(System.currentTimeMillis() - start);
//    }

}
