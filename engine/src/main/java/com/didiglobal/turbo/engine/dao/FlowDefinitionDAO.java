package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.FlowDefinitionPO;

public interface FlowDefinitionDAO extends BaseDAO{

    /**
     * Insert: insert flowDefinitionPO, return -1 while insert failed.
     *
     * @param flowDefinitionPO
     * @return int
     */
    int insert(FlowDefinitionPO flowDefinitionPO);

    /**
     * UpdateByModuleId: update flowDefinitionPO by flowModuleId, return -1 while updateByModuleId failed.
     *
     * @param flowDefinitionPO
     * @return int
     */
    int updateByModuleId(FlowDefinitionPO flowDefinitionPO);

    /**
     * SelectByModuleId: query flowDefinitionPO by flowModuleId, return null while flowDefinitionPO can't be found.
     *
     * @param flowModuleId
     * @return flowDefinitionPO
     */
    FlowDefinitionPO selectByModuleId(String flowModuleId);

}
