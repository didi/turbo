package com.didiglobal.turbo.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.util.TurboBeanUtils;

import java.util.HashMap;
import java.util.Map;

@TableName("ei_node_instance")
public class NodeInstanceEntity extends NodeInstancePO {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(exist = false)
    private Map<String, Object> properties = new HashMap<>();

    public static NodeInstanceEntity of(NodeInstancePO nodeInstancePO) {
        NodeInstanceEntity nodeInstanceEntity = new NodeInstanceEntity();
        TurboBeanUtils.copyProperties(nodeInstancePO, nodeInstanceEntity);
        return nodeInstanceEntity;
    }
}
