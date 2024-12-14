package com.didiglobal.turbo.mybatis.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.turbo.mybatis.entity.InstanceDataEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface InstanceDataMapper extends BaseMapper<InstanceDataEntity> {

    @Select("SELECT * FROM ei_instance_data WHERE instance_data_id=#{instanceDataId}")
    InstanceDataEntity select(@Param("flowInstanceId") String flowInstanceId,
                              @Param("instanceDataId") String instanceDataId);

    @Select("SELECT * FROM ei_instance_data WHERE flow_instance_id=#{flowInstanceId} ORDER BY id DESC LIMIT 1")
    InstanceDataEntity selectRecentOne(@Param("flowInstanceId") String flowInstanceId);

    @Update("UPDATE ei_instance_data SET instance_data=#{instanceData} WHERE id=#{id}")
    int updateData(InstanceDataEntity instanceDataPO);
}
