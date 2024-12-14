package com.didiglobal.turbo.mybatis.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.turbo.mybatis.entity.FlowInstanceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProcessInstanceMapper extends BaseMapper<FlowInstanceEntity> {

    @Select("SELECT * FROM ei_flow_instance WHERE flow_instance_id=#{flowInstanceId}")
    FlowInstanceEntity selectByFlowInstanceId(@Param("flowInstanceId") String flowInstanceId);

    @Update("UPDATE ei_flow_instance SET status=#{status}, modify_time=#{modifyTime} " +
            "WHERE flow_instance_id=#{flowInstanceId}")
    void updateStatus(FlowInstanceEntity entity);
}
