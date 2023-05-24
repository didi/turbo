package com.didiglobal.turbo.engine.executor;


import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.dao.InstanceDataDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceLogDAO;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.spi.generator.IdGenerateFactory;

import javax.annotation.Resource;

/**
 * runtime executor
 */
public abstract class RuntimeExecutor {

    @Resource
    protected ExecutorFactory executorFactory;

    @Resource
    protected InstanceDataDAO instanceDataDAO;

    @Resource
    protected NodeInstanceDAO nodeInstanceDAO;

    @Resource
    protected NodeInstanceLogDAO nodeInstanceLogDAO;

    protected String genId() {
        return IdGenerateFactory.getIdGenerator().getNextId();
    }

    /**
     * execute the process
     *
     * @param runtimeContext runtime context information
     * @throws ProcessException the exception thrown during execution
     */
    public abstract void execute(RuntimeContext runtimeContext) throws ProcessException;

    /**
     * commit and continue the process
     *
     * @param runtimeContext runtime context information
     * @throws ProcessException the exception thrown during execution
     */
    public abstract void commit(RuntimeContext runtimeContext) throws ProcessException;

    /**
     * roll back to the previous user node
     *
     * @param runtimeContext runtime context information
     * @throws ProcessException the exception thrown during execution
     */
    public abstract void rollback(RuntimeContext runtimeContext) throws ProcessException;

    /**
     * determine whether the process has been completed
     *
     * @param runtimeContext runtime context information
     * @return true or false
     * @throws ProcessException the exception thrown during execution
     */
    protected abstract boolean isCompleted(RuntimeContext runtimeContext) throws ProcessException;

    /**
     * get the current execute executor
     *
     * @param runtimeContext runtime context information
     * @return execute executor
     * @throws ProcessException the exception thrown during execution
     */
    protected abstract RuntimeExecutor getExecuteExecutor(RuntimeContext runtimeContext) throws ProcessException;

    /**
     * get the rollback executor
     *
     * @param runtimeContext runtime context information
     * @return rollback executor
     * @throws ProcessException the exception thrown during execution
     */
    protected abstract RuntimeExecutor getRollbackExecutor(RuntimeContext runtimeContext) throws ProcessException;
}
