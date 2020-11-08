package com.xiaoju.uemc.turbo.engine.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoju.uemc.turbo.engine.entity.FlowDeploymentPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by Stefanie on 2019/11/29.
 */
public interface FlowDeploymentMapper extends BaseMapper<FlowDeploymentPO> {

    @Select("SELECT * FROM em_flow_deployment WHERE flow_deploy_id=#{flowDeployId}")
    FlowDeploymentPO selectByDeployId(@Param("flowDeployId") String flowDeployId);

    @Select("SELECT * FROM em_flow_deployment WHERE flow_module_id=#{flowModuleId} ORDER BY id DESC LIMIT 1")
    FlowDeploymentPO selectByModuleId(@Param("flowModuleId") String flowModuleId);
}
