package com.didiglobal.turbo.engine.engine;

import com.didiglobal.turbo.engine.config.BusinessConfig;
import com.didiglobal.turbo.engine.dao.FlowDefinitionDAO;
import com.didiglobal.turbo.engine.dao.FlowDeploymentDAO;
import com.didiglobal.turbo.engine.dao.FlowInstanceMappingDAO;
import com.didiglobal.turbo.engine.dao.InstanceDataDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceLogDAO;
import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.engine.executor.EndEventExecutor;
import com.didiglobal.turbo.engine.executor.ExclusiveGatewayExecutor;
import com.didiglobal.turbo.engine.executor.ExecutorFactory;
import com.didiglobal.turbo.engine.executor.FlowExecutor;
import com.didiglobal.turbo.engine.executor.SequenceFlowExecutor;
import com.didiglobal.turbo.engine.executor.StartEventExecutor;
import com.didiglobal.turbo.engine.executor.UserTaskExecutor;
import com.didiglobal.turbo.engine.executor.callactivity.SyncSingleCallActivityExecutor;
import com.didiglobal.turbo.engine.plugin.manager.DefaultPluginManager;
import com.didiglobal.turbo.engine.plugin.manager.PluginManager;
import com.didiglobal.turbo.engine.plugin.manager.TurboBeanFactory;
import com.didiglobal.turbo.engine.processor.DefinitionProcessor;
import com.didiglobal.turbo.engine.processor.RuntimeProcessor;
import com.didiglobal.turbo.engine.service.FlowInstanceService;
import com.didiglobal.turbo.engine.service.InstanceDataService;
import com.didiglobal.turbo.engine.service.NodeInstanceService;
import com.didiglobal.turbo.engine.util.ExpressionCalculator;
import com.didiglobal.turbo.engine.util.IdGenerator;
import com.didiglobal.turbo.engine.util.StrongUuidGenerator;
import com.didiglobal.turbo.engine.util.impl.GroovyExpressionCalculator;
import com.didiglobal.turbo.engine.validator.CallActivityValidator;
import com.didiglobal.turbo.engine.validator.ElementValidatorFactory;
import com.didiglobal.turbo.engine.validator.EndEventValidator;
import com.didiglobal.turbo.engine.validator.ExclusiveGatewayValidator;
import com.didiglobal.turbo.engine.validator.FlowModelValidator;
import com.didiglobal.turbo.engine.validator.ModelValidator;
import com.didiglobal.turbo.engine.validator.SequenceFlowValidator;
import com.didiglobal.turbo.engine.validator.StartEventValidator;
import com.didiglobal.turbo.engine.validator.UserTaskValidator;
import com.didiglobal.turbo.engine.engine.impl.ProcessEngineImpl;

import java.lang.reflect.Field;
import java.util.Collections;

/**
 * Builder for creating a fully-wired {@link ProcessEngine} without any IoC container.
 * <p>
 * Usage:
 * <pre>
 *   ProcessEngine engine = TurboEngineBuilder.create()
 *       .flowDefinitionDAO(myFlowDefinitionDAO)
 *       .flowDeploymentDAO(myFlowDeploymentDAO)
 *       // ... other DAOs
 *       .build();
 * </pre>
 * All DAO dependencies must be provided. Other services/executors are wired automatically.
 */
public class TurboEngineBuilder {

    // Required DAOs
    private FlowDefinitionDAO flowDefinitionDAO;
    private FlowDeploymentDAO flowDeploymentDAO;
    private ProcessInstanceDAO processInstanceDAO;
    private NodeInstanceDAO nodeInstanceDAO;
    private InstanceDataDAO instanceDataDAO;
    private FlowInstanceMappingDAO flowInstanceMappingDAO;
    private NodeInstanceLogDAO nodeInstanceLogDAO;

    // Optional overrides
    private PluginManager pluginManager;
    private ExpressionCalculator expressionCalculator;
    private IdGenerator idGenerator;
    private BusinessConfig businessConfig;

    private TurboEngineBuilder() {
    }

    public static TurboEngineBuilder create() {
        return new TurboEngineBuilder();
    }

    public TurboEngineBuilder flowDefinitionDAO(FlowDefinitionDAO dao) {
        this.flowDefinitionDAO = dao;
        return this;
    }

    public TurboEngineBuilder flowDeploymentDAO(FlowDeploymentDAO dao) {
        this.flowDeploymentDAO = dao;
        return this;
    }

    public TurboEngineBuilder processInstanceDAO(ProcessInstanceDAO dao) {
        this.processInstanceDAO = dao;
        return this;
    }

    public TurboEngineBuilder nodeInstanceDAO(NodeInstanceDAO dao) {
        this.nodeInstanceDAO = dao;
        return this;
    }

    public TurboEngineBuilder instanceDataDAO(InstanceDataDAO dao) {
        this.instanceDataDAO = dao;
        return this;
    }

    public TurboEngineBuilder flowInstanceMappingDAO(FlowInstanceMappingDAO dao) {
        this.flowInstanceMappingDAO = dao;
        return this;
    }

    public TurboEngineBuilder nodeInstanceLogDAO(NodeInstanceLogDAO dao) {
        this.nodeInstanceLogDAO = dao;
        return this;
    }

    public TurboEngineBuilder pluginManager(PluginManager pm) {
        this.pluginManager = pm;
        return this;
    }

    public TurboEngineBuilder expressionCalculator(ExpressionCalculator ec) {
        this.expressionCalculator = ec;
        return this;
    }

    public TurboEngineBuilder idGenerator(IdGenerator ig) {
        this.idGenerator = ig;
        return this;
    }

    public TurboEngineBuilder businessConfig(BusinessConfig bc) {
        this.businessConfig = bc;
        return this;
    }

    /**
     * Build a fully-wired {@link ProcessEngine} using pure Java field injection (reflection).
     * No Spring context required.
     */
    public ProcessEngine build() {
        // Defaults
        if (pluginManager == null) {
            pluginManager = new DefaultPluginManager(new TurboBeanFactory() {
                @Override
                public <T> T getBean(Class<T> requiredType) {
                    throw new UnsupportedOperationException(
                            "No bean factory configured. Register a custom PluginManager via TurboEngineBuilder.pluginManager().");
                }
            });
        }
        if (expressionCalculator == null) {
            expressionCalculator = new GroovyExpressionCalculator();
        }
        if (idGenerator == null) {
            idGenerator = new StrongUuidGenerator();
        }
        if (businessConfig == null) {
            businessConfig = new BusinessConfig();
        }

        // --- Services ---
        FlowInstanceService flowInstanceService = new FlowInstanceService();
        inject(flowInstanceService, "processInstanceDAO", processInstanceDAO);
        inject(flowInstanceService, "nodeInstanceDAO", nodeInstanceDAO);
        inject(flowInstanceService, "flowDeploymentDAO", flowDeploymentDAO);
        inject(flowInstanceService, "flowInstanceMappingDAO", flowInstanceMappingDAO);

        InstanceDataService instanceDataService = new InstanceDataService();
        inject(instanceDataService, "instanceDataDAO", instanceDataDAO);
        inject(instanceDataService, "processInstanceDAO", processInstanceDAO);
        inject(instanceDataService, "flowDeploymentDAO", flowDeploymentDAO);
        inject(instanceDataService, "nodeInstanceDAO", nodeInstanceDAO);
        inject(instanceDataService, "flowInstanceMappingDAO", flowInstanceMappingDAO);
        inject(instanceDataService, "flowInstanceService", flowInstanceService);

        NodeInstanceService nodeInstanceService = new NodeInstanceService();
        inject(nodeInstanceService, "nodeInstanceDAO", nodeInstanceDAO);
        inject(nodeInstanceService, "processInstanceDAO", processInstanceDAO);
        inject(nodeInstanceService, "flowDeploymentDAO", flowDeploymentDAO);
        inject(nodeInstanceService, "flowInstanceService", flowInstanceService);

        // --- Validators ---
        StartEventValidator startEventValidator = new StartEventValidator();
        EndEventValidator endEventValidator = new EndEventValidator();
        SequenceFlowValidator sequenceFlowValidator = new SequenceFlowValidator();
        UserTaskValidator userTaskValidator = new UserTaskValidator();
        ExclusiveGatewayValidator exclusiveGatewayValidator = new ExclusiveGatewayValidator();
        CallActivityValidator callActivityValidator = new CallActivityValidator();
        inject(callActivityValidator, "businessConfig", businessConfig);
        inject(callActivityValidator, "flowDefinitionDAO", flowDefinitionDAO);

        ElementValidatorFactory elementValidatorFactory = new ElementValidatorFactory();
        inject(elementValidatorFactory, "startEventValidator", startEventValidator);
        inject(elementValidatorFactory, "endEventValidator", endEventValidator);
        inject(elementValidatorFactory, "sequenceFlowValidator", sequenceFlowValidator);
        inject(elementValidatorFactory, "userTaskValidator", userTaskValidator);
        inject(elementValidatorFactory, "exclusiveGatewayValidator", exclusiveGatewayValidator);
        inject(elementValidatorFactory, "callActivityValidator", callActivityValidator);
        inject(elementValidatorFactory, "pluginManager", pluginManager);
        elementValidatorFactory.init();

        FlowModelValidator flowModelValidator = new FlowModelValidator();
        inject(flowModelValidator, "elementValidatorFactory", elementValidatorFactory);

        ModelValidator modelValidator = new ModelValidator();
        inject(modelValidator, "flowModelValidator", flowModelValidator);

        // --- ExecutorFactory placeholder (needed before executors) ---
        ExecutorFactory executorFactory = new ExecutorFactory();

        // --- Executors (all wired with common deps) ---
        StartEventExecutor startEventExecutor = new StartEventExecutor();
        EndEventExecutor endEventExecutor = new EndEventExecutor();
        SequenceFlowExecutor sequenceFlowExecutor = new SequenceFlowExecutor();
        UserTaskExecutor userTaskExecutor = new UserTaskExecutor();
        ExclusiveGatewayExecutor exclusiveGatewayExecutor = new ExclusiveGatewayExecutor();
        inject(exclusiveGatewayExecutor, "hookServices", Collections.emptyList());
        SyncSingleCallActivityExecutor syncSingleCallActivityExecutor = new SyncSingleCallActivityExecutor();

        FlowExecutor flowExecutor = new FlowExecutor();

        // Wire executors with executorFactory + DAOs
        for (Object exec : new Object[]{startEventExecutor, endEventExecutor, sequenceFlowExecutor,
                userTaskExecutor, exclusiveGatewayExecutor, syncSingleCallActivityExecutor, flowExecutor}) {
            wireRuntimeExecutor(exec, executorFactory, pluginManager,
                    instanceDataDAO, nodeInstanceDAO, processInstanceDAO, nodeInstanceLogDAO, flowInstanceMappingDAO);
        }
        // Wire ElementExecutor-level expressionCalculator
        for (Object exec : new Object[]{startEventExecutor, endEventExecutor, sequenceFlowExecutor,
                userTaskExecutor, exclusiveGatewayExecutor}) {
            inject(exec, "expressionCalculator", expressionCalculator);
        }
        // Wire callActivity-specific fields
        inject(syncSingleCallActivityExecutor, "flowDeploymentDAO", flowDeploymentDAO);
        inject(syncSingleCallActivityExecutor, "nodeInstanceService", nodeInstanceService);
        inject(syncSingleCallActivityExecutor, "businessConfig", businessConfig);

        // Now wire executorFactory
        inject(executorFactory, "startEventExecutor", startEventExecutor);
        inject(executorFactory, "endEventExecutor", endEventExecutor);
        inject(executorFactory, "sequenceFlowExecutor", sequenceFlowExecutor);
        inject(executorFactory, "userTaskExecutor", userTaskExecutor);
        inject(executorFactory, "exclusiveGatewayExecutor", exclusiveGatewayExecutor);
        inject(executorFactory, "syncSingleCallActivityExecutor", syncSingleCallActivityExecutor);
        inject(executorFactory, "pluginManager", pluginManager);
        executorFactory.init();

        // Wire flowExecutor-specific field
        inject(flowExecutor, "processInstanceDAO", processInstanceDAO);

        // Wire SyncSingleCallActivityExecutor's runtimeProcessor (set after processor creation)
        // --- Processors ---
        DefinitionProcessor definitionProcessor = new DefinitionProcessor();
        inject(definitionProcessor, "pluginManager", pluginManager);
        inject(definitionProcessor, "modelValidator", modelValidator);
        inject(definitionProcessor, "flowDefinitionDAO", flowDefinitionDAO);
        inject(definitionProcessor, "flowDeploymentDAO", flowDeploymentDAO);
        definitionProcessor.init();

        RuntimeProcessor runtimeProcessor = new RuntimeProcessor();
        inject(runtimeProcessor, "flowDeploymentDAO", flowDeploymentDAO);
        inject(runtimeProcessor, "processInstanceDAO", processInstanceDAO);
        inject(runtimeProcessor, "nodeInstanceDAO", nodeInstanceDAO);
        inject(runtimeProcessor, "flowInstanceMappingDAO", flowInstanceMappingDAO);
        inject(runtimeProcessor, "flowInstanceService", flowInstanceService);
        inject(runtimeProcessor, "instanceDataService", instanceDataService);
        inject(runtimeProcessor, "nodeInstanceService", nodeInstanceService);
        inject(runtimeProcessor, "flowExecutor", flowExecutor);

        // Wire runtimeProcessor back into callActivity executor
        inject(syncSingleCallActivityExecutor, "runtimeProcessor", runtimeProcessor);

        // --- ProcessEngine ---
        ProcessEngineImpl processEngine = new ProcessEngineImpl();
        inject(processEngine, "definitionProcessor", definitionProcessor);
        inject(processEngine, "runtimeProcessor", runtimeProcessor);

        return processEngine;
    }

    private void wireRuntimeExecutor(Object executor, ExecutorFactory executorFactory,
                                     PluginManager pluginManager, InstanceDataDAO instanceDataDAO,
                                     NodeInstanceDAO nodeInstanceDAO, ProcessInstanceDAO processInstanceDAO,
                                     NodeInstanceLogDAO nodeInstanceLogDAO,
                                     FlowInstanceMappingDAO flowInstanceMappingDAO) {
        inject(executor, "executorFactory", executorFactory);
        inject(executor, "pluginManager", pluginManager);
        inject(executor, "instanceDataDAO", instanceDataDAO);
        inject(executor, "nodeInstanceDAO", nodeInstanceDAO);
        inject(executor, "processInstanceDAO", processInstanceDAO);
        inject(executor, "nodeInstanceLogDAO", nodeInstanceLogDAO);
        inject(executor, "flowInstanceMappingDAO", flowInstanceMappingDAO);
    }

    /**
     * Set a field value using reflection (walks up the class hierarchy).
     */
    private static void inject(Object target, String fieldName, Object value) {
        Class<?> clazz = target.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot inject field '" + fieldName + "' on " + target.getClass(), e);
            }
        }
        // Field not found: silently ignore (field may not exist on this class hierarchy)
    }
}
