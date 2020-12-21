package com.xiaoju.uemc.turbo.engine.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Created by Stefanie on 2019/11/27.
 */
@TableName("ei_flow_instance")
@Data
@ToString(callSuper = true)
public class FlowInstancePO extends CommonPO {
    private String flowInstanceId;
    private String flowDeployId;
    private String flowModuleId;
    private Integer status;
    private Date modifyTime;
}
