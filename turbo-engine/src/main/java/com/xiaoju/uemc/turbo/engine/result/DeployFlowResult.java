package com.xiaoju.uemc.turbo.engine.result;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Stefanie on 2019/12/2.
 */
@Data
@ToString(callSuper = true)
public class DeployFlowResult extends CommonResult {
    private String flowModuleId;
    private String flowDeployId;
}
