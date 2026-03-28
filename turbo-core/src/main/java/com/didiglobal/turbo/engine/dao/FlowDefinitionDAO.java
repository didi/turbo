package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.FlowDefinitionPO;

/**
 * DAO interface for flow definition persistence.
 * Implementations may use MyBatis-Plus (in Spring context) or other mechanisms.
 */
public interface FlowDefinitionDAO {

    int insert(FlowDefinitionPO flowDefinitionPO);

    int updateByModuleId(FlowDefinitionPO flowDefinitionPO);

    FlowDefinitionPO selectByModuleId(String flowModuleId);
}
