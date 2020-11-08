package com.xiaoju.uemc.turbo.engine.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

/**
 * Created by Stefanie on 2019/11/24.
 */
@TableName("em_flow_definition")
@Data
@ToString(callSuper = true)
public class FlowDefinitionPO extends OperationPO {
    private String flowModuleId;
    private String flowName;
    private String flowKey;
    private String flowModel;
    private Integer status;
}
