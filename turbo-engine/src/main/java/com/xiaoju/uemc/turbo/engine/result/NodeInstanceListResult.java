package com.xiaoju.uemc.turbo.engine.result;

import com.xiaoju.uemc.turbo.engine.bo.NodeInstance;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by Stefanie on 2020/1/6.
 */
@Data
@ToString(callSuper = true)
public class NodeInstanceListResult extends CommonResult {
    private List<NodeInstance> nodeInstanceList;

    public NodeInstanceListResult(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
