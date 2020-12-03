package com.xiaoju.uemc.turbo.engine.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

/**
 * Created by Stefanie on 2019/11/27.
 */
@TableName("em_flow_deployment")
@Data
@ToString(callSuper = true)
public class FlowDeploymentPO extends OperationPO {
    private String flowDeployId;
    private String flowModuleId;
    private String flowName;
    private String flowKey;
    private String flowModel;
    private Integer status;
}
