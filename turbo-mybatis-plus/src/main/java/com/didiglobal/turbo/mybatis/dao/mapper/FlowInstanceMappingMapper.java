package com.didiglobal.turbo.mybatis.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.turbo.mybatis.entity.FlowInstanceMappingEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FlowInstanceMappingMapper extends BaseMapper<FlowInstanceMappingEntity> {

    @Select("SELECT * FROM ei_flow_instance_mapping WHERE flow_instance_id= #{flowInstanceId} and node_instance_id = #{nodeInstanceId}")
    List<FlowInstanceMappingEntity> selectFlowInstanceMappingPOList(@Param("flowInstanceId") String flowInstanceId, @Param("nodeInstanceId") String nodeInstanceId);

    @Select("SELECT * FROM ei_flow_instance_mapping WHERE flow_instance_id= #{flowInstanceId} and node_instance_id = #{nodeInstanceId}")
    FlowInstanceMappingEntity selectFlowInstanceMappingPO(@Param("flowInstanceId") String flowInstanceId, @Param("nodeInstanceId") String nodeInstanceId);

    @Update("UPDATE ei_flow_instance_mapping SET type= #{type}, modify_time= #{modifyTime} WHERE flow_instance_id= #{flowInstanceId} and node_instance_id = #{nodeInstanceId}")
    void updateType(FlowInstanceMappingEntity entity);
}
