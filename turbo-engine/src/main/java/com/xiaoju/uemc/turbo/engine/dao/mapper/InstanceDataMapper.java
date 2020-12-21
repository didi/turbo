package com.xiaoju.uemc.turbo.engine.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoju.uemc.turbo.engine.entity.InstanceDataPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


/**
 * Created by Stefanie on 2019/12/1.
 */
public interface InstanceDataMapper extends BaseMapper<InstanceDataPO> {

    @Select("SELECT * FROM ei_instance_data WHERE instance_data_id=#{instanceDataId}")
    InstanceDataPO select(@Param("flowInstanceId") String flowInstanceId,
                          @Param("instanceDataId") String instanceDataId);

    @Select("SELECT * FROM ei_instance_data WHERE flow_instance_id=#{flowInstanceId} ORDER BY id DESC LIMIT 1")
    InstanceDataPO selectRecentOne(@Param("flowInstanceId") String flowInstanceId);
}
