package com.didiglobal.turbo.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.didiglobal.turbo.engine.entity.FlowDefinitionPO;
import com.didiglobal.turbo.engine.util.TurboBeanUtils;

import java.util.HashMap;
import java.util.Map;

@TableName("em_flow_definition")
public class FlowDefinitionEntity extends FlowDefinitionPO {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(exist = false)
    private Map<String, Object> properties = new HashMap<>();

    public static FlowDefinitionEntity of(FlowDefinitionPO po) {
        FlowDefinitionEntity entity = new FlowDefinitionEntity();
        TurboBeanUtils.copyProperties(po, entity);
        return entity;
    }

}
