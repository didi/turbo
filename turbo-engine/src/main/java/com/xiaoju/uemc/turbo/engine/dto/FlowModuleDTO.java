package com.xiaoju.uemc.turbo.engine.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Created by Stefanie on 2019/12/2.
 */
@Data
@ToString(callSuper = true)
public class FlowModuleDTO extends CommonDTO {
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
