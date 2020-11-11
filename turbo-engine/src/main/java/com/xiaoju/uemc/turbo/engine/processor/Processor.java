package com.xiaoju.uemc.turbo.engine.processor;

import com.xiaoju.uemc.turbo.engine.util.IdGenerator;

/**
 * Created by Stefanie on 2020/11/11.
 */
public class Processor {

    protected IdGenerator idGenerator;

    protected Processor() {
    }

    protected Processor(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
}
