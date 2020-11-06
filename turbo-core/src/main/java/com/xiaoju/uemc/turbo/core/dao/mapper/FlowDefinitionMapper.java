package com.xiaoju.uemc.turbo.core.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoju.uemc.turbo.core.entity.FlowDefinitionPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by Stefanie on 2019/11/25.
 */
public interface FlowDefinitionMapper extends BaseMapper<FlowDefinitionPO> {

    @Select("SELECT * FROM em_flow_definition WHERE flow_module_id=#{flowModuleId}")
    FlowDefinitionPO selectByFlowModuleId(@Param("flowModuleId") String flowModuleId);
}
