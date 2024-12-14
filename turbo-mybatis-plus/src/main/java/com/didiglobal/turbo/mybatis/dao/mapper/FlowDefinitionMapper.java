package com.didiglobal.turbo.mybatis.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.turbo.mybatis.entity.FlowDefinitionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FlowDefinitionMapper extends BaseMapper<FlowDefinitionEntity> {

    @Select("SELECT * FROM em_flow_definition WHERE flow_module_id=#{flowModuleId}")
    FlowDefinitionEntity selectByFlowModuleId(@Param("flowModuleId") String flowModuleId);
}
