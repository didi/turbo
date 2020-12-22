package com.xiaoju.uemc.turbo.engine.bo;

import lombok.Data;

/**
 * Created by Stefanie on 2019/12/19.
 */
@Data
public class FlowBasicInfo {
    private String flowDeployId;
    private String flowModuleId;
    private String tenant;
    private String caller;
}
