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
    // redis cache
    protected final RedisUtils redisClient = RedisUtils.getInstance();

    // TODO: 2020/11/11
    private static final IdGenerator idGenerator = StrongUuidGenerator.getInstance();

    /**
     * generate random id
     * @return
     */
    protected String genId() {
        return idGenerator.getNextId();
    }

    /**
     * execute flow module with runtimeContext
     *
     * @param runtimeContext
     * @throws Exception
     */
    public abstract void execute(RuntimeContext runtimeContext) throws Exception;

    /**
     * commit flow module with runtimeContext
     *
     * @param runtimeContext
     * @throws Exception
     */
    public abstract void commit(RuntimeContext runtimeContext) throws Exception;

    /**
     * rollback flow module with runtimeContext
     *
     * @param runtimeContext
     * @throws Exception
     */
    public abstract void rollback(RuntimeContext runtimeContext) throws Exception;

    /**
     * judge current status is complete from runtimeContext
     *
     * @param runtimeContext
     * @return
     * @throws Exception
     */
    protected abstract boolean isCompleted(RuntimeContext runtimeContext) throws Exception;

    /**
     * get executor on execute
     *
     * @param runtimeContext
     * @return
     * @throws Exception
     */
    protected abstract RuntimeExecutor getExecuteExecutor(RuntimeContext runtimeContext) throws Exception;

    /**
     * get executor on rollback
     *
     * @param runtimeContext
     * @return
     * @throws Exception
     */
    protected abstract RuntimeExecutor getRollbackExecutor(RuntimeContext runtimeContext) throws Exception;
}
