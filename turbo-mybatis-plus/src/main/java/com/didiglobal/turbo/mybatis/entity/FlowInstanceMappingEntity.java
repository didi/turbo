package com.didiglobal.turbo.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.didiglobal.turbo.engine.entity.FlowInstanceMappingPO;
import com.didiglobal.turbo.engine.util.TurboBeanUtils;

import java.util.HashMap;
import java.util.Map;

@TableName("ei_flow_instance_mapping")
public class FlowInstanceMappingEntity extends FlowInstanceMappingPO {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(exist = false)
    private Map<String, Object> properties = new HashMap<>();

    public static FlowInstanceMappingEntity of(FlowInstanceMappingPO flowInstanceMappingPO) {
        FlowInstanceMappingEntity flowInstanceMappingEntity = new FlowInstanceMappingEntity();
        TurboBeanUtils.copyProperties(flowInstanceMappingPO, flowInstanceMappingEntity);
        return flowInstanceMappingEntity;
    }
}
