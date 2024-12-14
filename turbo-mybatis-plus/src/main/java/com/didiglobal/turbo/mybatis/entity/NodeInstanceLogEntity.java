package com.didiglobal.turbo.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;
import com.didiglobal.turbo.engine.util.TurboBeanUtils;

import java.util.HashMap;
import java.util.Map;

@TableName("ei_node_instance_log")
public class NodeInstanceLogEntity extends NodeInstanceLogPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(exist = false)
    private Map<String, Object> properties = new HashMap<>();

    public static NodeInstanceLogEntity of(NodeInstanceLogPO nodeInstanceLogPO) {
        NodeInstanceLogEntity nodeInstanceLogEntity = new NodeInstanceLogEntity();
        TurboBeanUtils.copyProperties(nodeInstanceLogPO, nodeInstanceLogEntity);
        return nodeInstanceLogEntity;
    }
}
