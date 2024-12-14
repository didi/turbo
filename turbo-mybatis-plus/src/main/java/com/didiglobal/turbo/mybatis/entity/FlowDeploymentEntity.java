package com.didiglobal.turbo.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import com.didiglobal.turbo.engine.util.TurboBeanUtils;

import java.util.HashMap;
import java.util.Map;

@TableName("em_flow_deployment")
public class FlowDeploymentEntity extends FlowDeploymentPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(exist = false)
    private Map<String, Object> properties = new HashMap<>();

    public static FlowDeploymentEntity of(FlowDeploymentPO flowDeploymentPO) {
        FlowDeploymentEntity flowDeploymentEntity = new FlowDeploymentEntity();
        TurboBeanUtils.copyProperties(flowDeploymentPO, flowDeploymentEntity);
        return flowDeploymentEntity;
    }
}
