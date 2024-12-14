package com.didiglobal.turbo.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.util.TurboBeanUtils;

import java.util.HashMap;
import java.util.Map;

@TableName("ei_instance_data")
public class InstanceDataEntity extends InstanceDataPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(exist = false)
    private Map<String, Object> properties = new HashMap<>();

    public static InstanceDataEntity of(InstanceDataPO instanceDataPO) {
        InstanceDataEntity instanceDataEntity = new InstanceDataEntity();
        TurboBeanUtils.copyProperties(instanceDataPO, instanceDataEntity);
        return instanceDataEntity;
    }
}
