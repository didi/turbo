package com.xiaoju.uemc.turbo.engine.engineV2;

import com.didiglobal.reportlogger.LoggerFactory;
import com.didiglobal.reportlogger.ReportLogger;
import com.xiaoju.uemc.turbo.engine.processor.DefinitionProcessor;
import com.xiaoju.uemc.turbo.engine.processor.RuntimeProcessor;
import com.xiaoju.uemc.turbo.engine.util.IdGenerator;

/**
 * Created by Stefanie on 2020/11/11.
 */
public class ProcessEngineV2 extends TurboEngine implements ITurboEngine {

    protected static final ReportLogger LOGGER = LoggerFactory.getLogger(ProcessEngineV2.class);

    private static final ProcessEngineV2 processEngineV2 = new ProcessEngineV2();

    private ProcessEngineV2() {
    }

    private void init(IdGenerator idGenerator) {
        if (idGenerator != null) {
            this.definitionProcessor = new DefinitionProcessor(idGenerator);
            this.runtimeProcessor = new RuntimeProcessor(idGenerator);
        } else {
            LOGGER.error("ProcessEngineV2 init failed: idGenerator is null.||idGenerator={}", idGenerator);
        }
    }

    public static Builder custom() {
        return new Builder();
    }

    public static Builder custorm(IdGenerator idGenerator) {
        return new Builder(idGenerator);
    }

    public static class Builder {

        private IdGenerator idGenerator;

        public Builder(IdGenerator idGenerator) {
            this.idGenerator = idGenerator;
        }

        public Builder() {
        }

        public Builder setIdGenerator(IdGenerator idGenerator) {
            this.idGenerator = idGenerator;
            return this;
        }

        public ProcessEngineV2 build() {
            processEngineV2.init(idGenerator);
            return processEngineV2;
        }
    }
}
