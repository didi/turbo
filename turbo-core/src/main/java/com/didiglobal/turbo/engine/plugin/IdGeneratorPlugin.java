package com.didiglobal.turbo.engine.plugin;

import com.didiglobal.turbo.engine.util.IdGenerator;

public interface IdGeneratorPlugin extends Plugin{
    IdGenerator getIdGenerator();
}
