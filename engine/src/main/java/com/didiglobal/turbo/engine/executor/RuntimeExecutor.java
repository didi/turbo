package com.didiglobal.turbo.engine.executor;


import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.dao.*;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.spi.generator.IdGenerateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * runtime executor
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
    protected ProcessInstanceDAO processInstanceDAO;

    @Resource
    protected NodeInstanceLogDAO nodeInstanceLogDAO;

    @Resource
    protected FlowInstanceMappingDAO flowInstanceMappingDAO;

    protected String genId() {
        return IdGenerateFactory.getIdGenerator().getNextId();
    }

    /**
     * Execute the process
     *
     * @param runtimeContext runtime context information
     * @throws ProcessException the exception thrown during execution
     */
    public abstract void execute(RuntimeContext runtimeContext) throws ProcessException;

    /**
     * Commit and continue the process
     *
     * @param runtimeContext runtime context information
     * @throws ProcessException the exception thrown during execution
     */
    public abstract void commit(RuntimeContext runtimeContext) throws ProcessException;

    /**
     * Roll back to the previous user node
     *
     * @param runtimeContext runtime context information
     * @throws ProcessException the exception thrown during execution
     */
    public abstract void rollback(RuntimeContext runtimeContext) throws ProcessException;

    /**
     * Determine whether the process has been completed
     *
     * @param runtimeContext runtime context information
     * @return true or false
     * @throws ProcessException the exception thrown during execution
     */
    protected abstract boolean isCompleted(RuntimeContext runtimeContext) throws ProcessException;

    /**
     * Determine whether the current process is a sub process
     *
     * @param runtimeContext runtime context information
     * @return true or false
     * @throws ProcessException the exception thrown during execution
     */
    protected boolean isSubFlowInstance(RuntimeContext runtimeContext) throws ProcessException {
        return runtimeContext.getParentRuntimeContext() != null;
    }

    /**
     * Get the current execute executor
     *
     * @param runtimeContext runtime context information
     * @return execute executor
     * @throws ProcessException the exception thrown during execution
     */
    protected abstract RuntimeExecutor getExecuteExecutor(RuntimeContext runtimeContext) throws ProcessException;

    /**
     * Get the rollback executor
     *
     * @param runtimeContext runtime context information
     * @return rollback executor
     * @throws ProcessException the exception thrown during execution
     */
    protected abstract RuntimeExecutor getRollbackExecutor(RuntimeContext runtimeContext) throws ProcessException;
}
