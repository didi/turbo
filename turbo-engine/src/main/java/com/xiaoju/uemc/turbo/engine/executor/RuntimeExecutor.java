package com.xiaoju.uemc.turbo.engine.executor;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.xiaoju.uemc.modules.support.jedis.RedisUtils;
import com.xiaoju.uemc.turbo.engine.common.RuntimeContext;
import com.xiaoju.uemc.turbo.engine.dao.InstanceDataDAO;
import com.xiaoju.uemc.turbo.engine.dao.NodeInstanceDAO;
import com.xiaoju.uemc.turbo.engine.dao.NodeInstanceLogDAO;
import com.xiaoju.uemc.turbo.engine.util.IdGenerator;
import com.xiaoju.uemc.turbo.engine.util.StrongUuidGenerator;

import javax.annotation.Resource;

/**
 * The RuntimeExecutor provides the execution operations of process execution, submission and rollback,
 * the flow operation of executing the next node executor or rolling back the next node executor,
 * and the judgment operation of checking whether it is completed
 *
 * The process execution starts from the start node until the user node is suspended.
 * The process submission starts from the user node and then accompanies the execution.
 * The process rollback starts from the user node and rolls back to the nearest user node.
 * The operation is accompanied by a check whether the operation is completed
 *
 * Getting and executing the next node actuator or rolling back the next node actuator is mainly
 * used to find the actuator of the next node, calculate the next node model from the current
 * node model, and then get the node executor from the factory
 *
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

    protected final RedisUtils redisClient = RedisUtils.getInstance();

    // TODO: 2020/11/11
    private static final IdGenerator idGenerator = StrongUuidGenerator.getInstance();

    /**
     * Returns a random number sequence
     *
     * @return random number sequence
     */
    protected String genId() {
        return idGenerator.getNextId();
    }

    /**
     * It is used for the node executor to complete its own functions during execution.
     *
     * According to the runtime context, the node instance object is generated and has its own state.
     * For example, from the active state to the completion state, the node execution is completed once,
     * and then added to the end of the completed node queue to store the execution status.
     *
     * If it is a user node, it should be suspended at execution time to notify the external caller.
     *
     * @param runtimeContext include flow info and runtime info
     * @throws Exception
     */
    public abstract void execute(RuntimeContext runtimeContext) throws Exception;

    /**
     * User node submission starts from the current specified user node and
     * is suspended at the next user node or completed at the end node.
     *
     * Among them, the executor runs a commit and executes multiple times
     * until it is suspended or completed.
     *
     * @param runtimeContext include flow info and runtime info
     * @throws Exception
     */
    public abstract void commit(RuntimeContext runtimeContext) throws Exception;

    /**
     * User node rollback starts from the current specified user node,
     * rolls back to the nearest user node, suspends or throws an exception at the start node.
     *
     * Among them, we will set the current node status from active to disabled,
     * intermediate node status from completed to disabled,
     * and the most recent user node from completed to active, and finally update to the database
     *
     * @param runtimeContext include flow info and runtime info
     * @throws Exception
     */
    public abstract void rollback(RuntimeContext runtimeContext) throws Exception;

    /**
     * Judge whether the current process instance is completed in the execution process
     * from the runtime context
     *
     * @param runtimeContext include flow info and runtime info
     * @return true if flow instance is completed
     * @throws Exception
     */
    protected abstract boolean isCompleted(RuntimeContext runtimeContext) throws Exception;

    /**
     * It is used to find the executor of the next node during execution,
     * calculate the next node model from the current node model
     * according to the preset conditional expression,
     * and then get the node executor from the factory
     *
     * @param runtimeContext include flow info and runtime info
     * @return next node model executor
     * @throws Exception
     */
    protected abstract RuntimeExecutor getExecuteExecutor(RuntimeContext runtimeContext) throws Exception;

    /**
     * It is used to find the executor of the next node when rolling back.
     * From the current node model, according to the execution records of historical nodes,
     * the model of the next node to be rolled back is obtained, and then the executor of
     * this node is obtained from the factory
     *
     * @param runtimeContext include flow info and runtime info
     * @return
     * @throws Exception
     */
    protected abstract RuntimeExecutor getRollbackExecutor(RuntimeContext runtimeContext) throws Exception;
}
