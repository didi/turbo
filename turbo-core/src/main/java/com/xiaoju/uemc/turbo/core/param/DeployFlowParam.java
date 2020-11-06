package com.xiaoju.uemc.turbo.core.param;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Stefanie on 2019/12/2.
 */
@Data
@ToString(callSuper = true)
public class DeployFlowParam extends OperationParam {
    String flowModuleId;

    public DeployFlowParam(String tenant, String caller) {
        super(tenant, caller);
    }
}
