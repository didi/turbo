package com.xiaoju.uemc.turbo.engine.executor;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.xiaoju.uemc.turbo.engine.common.RuntimeContext;
import com.xiaoju.uemc.turbo.engine.dao.InstanceDataDAO;
import com.xiaoju.uemc.turbo.engine.dao.NodeInstanceDAO;
import com.xiaoju.uemc.turbo.engine.dao.NodeInstanceLogDAO;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import com.xiaoju.uemc.turbo.engine.util.IdGenerator;
import com.xiaoju.uemc.turbo.engine.util.StrongUuidGenerator;

import javax.annotation.Resource;

/**
 * Created by Stefanie on 2019/12/1.
 */
public abstract class RuntimeExecutor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RuntimeExecutor.class);

    @Resource
    protected ExecutorFactory executorFactory;

    @Resource
    protected InstanceDataDAO instanceDataDAO;

    @Resource
    protected NodeInstanceDAO nodeInstanceDAO;

    @Resource
    protected NodeInstanceLogDAO nodeInstanceLogDAO;

    private static final IdGenerator idGenerator = StrongUuidGenerator.getInstance();

    protected String genId() {
        return idGenerator.getNextId();
    }

    public abstract void execute(RuntimeContext runtimeContext) throws ProcessException;

    public abstract void commit(RuntimeContext runtimeContext) throws ProcessException;

    public abstract void rollback(RuntimeContext runtimeContext) throws ProcessException;

    protected abstract boolean isCompleted(RuntimeContext runtimeContext) throws ProcessException;

    protected abstract RuntimeExecutor getExecuteExecutor(RuntimeContext runtimeContext) throws ProcessException;

    protected abstract RuntimeExecutor getRollbackExecutor(RuntimeContext runtimeContext) throws ProcessException;
}
