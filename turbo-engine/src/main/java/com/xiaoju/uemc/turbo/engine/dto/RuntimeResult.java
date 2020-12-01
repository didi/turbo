package com.xiaoju.uemc.turbo.engine.dto;

import com.xiaoju.uemc.turbo.engine.bo.NodeInstance;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by Stefanie on 2019/12/5.
 */
@Data
@ToString(callSuper = true)
public class RuntimeResult extends CommonResult {
    private String flowInstanceId;
    private int status;
    private NodeInstance activeTaskInstance;
    private List<InstanceData> variables;

    public RuntimeResult() {
        super();
    }

    public RuntimeResult(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
