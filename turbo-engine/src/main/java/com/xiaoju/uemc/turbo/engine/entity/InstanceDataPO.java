package com.xiaoju.uemc.turbo.engine.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

/**
 * Created by Stefanie on 2019/11/27.
 */
@TableName("ei_instance_data")
@Data
@ToString(callSuper = true)
public class InstanceDataPO extends CommonPO {
    private String flowInstanceId;
    private String instanceDataId;
    private String nodeInstanceId;
    private String flowDeployId;
    private String flowModuleId;
    private String nodeKey;
    private String instanceData;
    private Integer type;
}
