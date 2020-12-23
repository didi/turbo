package com.didiglobal.turbo.engine.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.turbo.engine.entity.FlowDefinitionPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface FlowDefinitionMapper extends BaseMapper<FlowDefinitionPO> {

    @Select("SELECT * FROM em_flow_definition WHERE flow_module_id=#{flowModuleId}")
    FlowDefinitionPO selectByFlowModuleId(@Param("flowModuleId") String flowModuleId);
}
