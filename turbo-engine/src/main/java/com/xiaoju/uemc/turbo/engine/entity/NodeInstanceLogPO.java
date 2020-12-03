package com.xiaoju.uemc.turbo.engine.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

/**
 * Created by Stefanie on 2019/11/27.
 */
@TableName("ei_node_instance_log")
@Data
@ToString(callSuper = true)
public class NodeInstanceLogPO extends CommonPO {
    private String nodeInstanceId;
    private String flowInstanceId;
    private String instanceDataId;
    private String nodeKey;
    private Integer type;
    private Integer status;
}
