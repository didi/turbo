package com.didiglobal.turbo.engine.common;

import com.didiglobal.turbo.engine.entity.CommonPO;
import com.didiglobal.turbo.engine.entity.FlowDefinitionPO;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import com.didiglobal.turbo.engine.entity.FlowInstanceMappingPO;
import com.didiglobal.turbo.engine.entity.FlowInstancePO;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;

public enum EntityPOEnum {
    FLOW_DEFINITION("em_flow_definition", FlowDefinitionPO.class),
    FLOW_DEPLOYMENT("em_flow_deployment", FlowDeploymentPO.class),
    FLOW_INSTANCE("ei_flow_instance", FlowInstancePO.class),
    NODE_INSTANCE("ei_node_instance", NodeInstancePO.class),
    INSTANCE_DATA("ei_instance_data", InstanceDataPO.class),
    FLOW_INSTANCE_MAPPING("ei_flow_instance_mapping", FlowInstanceMappingPO.class),
    NODE_INSTANCE_LOG("ei_node_instance_log", NodeInstanceLogPO.class);

    private String tableName;
    private Class<? extends CommonPO> entityClass;

    EntityPOEnum(String flowDefinition, Class<? extends CommonPO> poClass) {
        this.tableName = flowDefinition;
        this.entityClass = poClass;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<? extends CommonPO> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public String toString() {
        return "EntityEnum{" +
            "entityName='" + tableName + '\'' +
            ", entityType=" + entityClass.getName() +
            '}';
    }
}
