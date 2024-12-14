package com.didiglobal.turbo.mybatis.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.turbo.mybatis.entity.FlowDeploymentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FlowDeploymentMapper extends BaseMapper<FlowDeploymentEntity> {

    @Select("SELECT * FROM em_flow_deployment WHERE flow_deploy_id=#{flowDeployId}")
    FlowDeploymentEntity selectByDeployId(@Param("flowDeployId") String flowDeployId);

    @Select("SELECT * FROM em_flow_deployment WHERE flow_module_id=#{flowModuleId} ORDER BY id DESC LIMIT 1")
    FlowDeploymentEntity selectByModuleId(@Param("flowModuleId") String flowModuleId);
}
