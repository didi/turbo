package com.xiaoju.uemc.turbo.engine.bo;

import lombok.Data;

/**
 * Created by Stefanie on 2020/1/30.
 */
@Data
public class FlowInstanceBO {
    private String flowInstanceId;
    private String flowDeployId;
    private Integer status;
}
