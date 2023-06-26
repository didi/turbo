package com.didiglobal.turbo.engine.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.turbo.engine.entity.FlowInstanceMappingPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface FlowInstanceMappingMapper extends BaseMapper<FlowInstanceMappingPO> {

    @Select("SELECT * FROM ei_flow_instance_mapping WHERE flow_instance_id= #{flowInstanceId} and node_instance_id = #{nodeInstanceId}")
    List<FlowInstanceMappingPO> selectFlowInstanceMappingPOList(@Param("flowInstanceId") String flowInstanceId, @Param("nodeInstanceId") String nodeInstanceId);

    @Select("SELECT * FROM ei_flow_instance_mapping WHERE flow_instance_id= #{flowInstanceId} and node_instance_id = #{nodeInstanceId}")
    FlowInstanceMappingPO selectFlowInstanceMappingPO(@Param("flowInstanceId") String flowInstanceId, @Param("nodeInstanceId") String nodeInstanceId);

    @Update("UPDATE ei_flow_instance_mapping SET type= #{type}, modify_time= #{modifyTime} WHERE flow_instance_id= #{flowInstanceId} and node_instance_id = #{nodeInstanceId}")
    void updateType(FlowInstanceMappingPO entity);
}
