package com.xiaoju.uemc.turbo.engine.engineV2;

import com.xiaoju.uemc.turbo.engine.util.StrongUuidGenerator;

/**
 * Created by Stefanie on 2020/11/11.
 */
public class TurboEngineFactory {

    public static ITurboEngine getTurboEngine() {
        //default IdGenerator
        return ProcessEngineV2.custom().setIdGenerator(StrongUuidGenerator.getInstance()).build();
    }
}
