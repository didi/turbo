package com.xiaoju.uemc.turbo.core.param;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Stefanie on 2019/12/2.
 */
@Data
@ToString(callSuper = true)
public class CreateFlowParam extends OperationParam {
    private String flowKey;
    private String flowName;
    private String remark;

    public CreateFlowParam(String tenant, String caller) {
        super(tenant, caller);
    }
}
