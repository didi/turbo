package com.xiaoju.uemc.turbo.core.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by Stefanie on 2019/12/2.
 */
@Data
public class FlowModuleDTO {
    private String flowModuleId;
    private String flowName;
    private String flowKey;
    private String flowModel;
    private Integer status;
    private String remark;
    private String tenant;
    private String caller;
    private String operator;
    private Date modifyTime;
}
