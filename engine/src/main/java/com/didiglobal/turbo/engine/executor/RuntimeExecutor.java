package com.didiglobal.turbo.engine.executor;


import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.dao.FlowInstanceMappingDAO;
import com.didiglobal.turbo.engine.dao.InstanceDataDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceLogDAO;
import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.plugin.IdGeneratorPlugin;
import com.didiglobal.turbo.engine.plugin.manager.PluginManager;
import com.didiglobal.turbo.engine.util.IdGenerator;
import com.didiglobal.turbo.engine.util.StrongUuidGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class RuntimeExecutor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RuntimeExecutor.class);

    protected final ExecutorFactory executorFactory;

    protected final InstanceDataDAO instanceDataDAO;

    protected final NodeInstanceDAO nodeInstanceDAO;

    protected final ProcessInstanceDAO processInstanceDAO;

    protected final NodeInstanceLogDAO nodeInstanceLogDAO;

    protected final FlowInstanceMappingDAO flowInstanceMappingDAO;

    protected final PluginManager pluginManager;

    private IdGenerator ID_GENERATOR;

    protected RuntimeExecutor(ExecutorFactory executorFactory, InstanceDataDAO instanceDataDAO, NodeInstanceDAO nodeInstanceDAO, ProcessInstanceDAO processInstanceDAO, NodeInstanceLogDAO nodeInstanceLogDAO, FlowInstanceMappingDAO flowInstanceMappingDAO, PluginManager pluginManager) {
        this.executorFactory = executorFactory;
        this.instanceDataDAO = instanceDataDAO;
        this.nodeInstanceDAO = nodeInstanceDAO;
        this.processInstanceDAO = processInstanceDAO;
        this.nodeInstanceLogDAO = nodeInstanceLogDAO;
        this.flowInstanceMappingDAO = flowInstanceMappingDAO;
        this.pluginManager = pluginManager;
    }

    protected String genId() {
        if (null == ID_GENERATOR) {
            List<IdGeneratorPlugin> idGeneratorPlugins = pluginManager.getPluginsFor(IdGeneratorPlugin.class);
            if (!idGeneratorPlugins.isEmpty()) {
                ID_GENERATOR = idGeneratorPlugins.get(0).getIdGenerator();
            } else {
                ID_GENERATOR = new StrongUuidGenerator();
            }
        }
        return ID_GENERATOR.getNextId();
    }

    public abstract void execute(RuntimeContext runtimeContext) throws ProcessException;

    public abstract void commit(RuntimeContext runtimeContext) throws ProcessException;

    public abstract void rollback(RuntimeContext runtimeContext) throws ProcessException;

    protected abstract boolean isCompleted(RuntimeContext runtimeContext) throws ProcessException;

    protected boolean isSubFlowInstance(RuntimeContext runtimeContext) throws ProcessException {
        return runtimeContext.getParentRuntimeContext() != null;
    }

    protected abstract RuntimeExecutor getExecuteExecutor(RuntimeContext runtimeContext) throws ProcessException;

    protected abstract RuntimeExecutor getRollbackExecutor(RuntimeContext runtimeContext) throws ProcessException;
}
