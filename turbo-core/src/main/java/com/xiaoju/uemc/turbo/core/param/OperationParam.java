package com.xiaoju.uemc.turbo.core.param;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Stefanie on 2019/12/2.
 */
@Data
@ToString(callSuper = true)
public class OperationParam extends CommonParam{
    private String operator;

    public OperationParam(String tenant, String caller) {
        super(tenant, caller);
    }
}
