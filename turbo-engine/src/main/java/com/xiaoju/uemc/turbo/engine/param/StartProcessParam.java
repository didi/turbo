package com.xiaoju.uemc.turbo.engine.param;

import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import lombok.Data;

import java.util.List;

/**
 * Created by Stefanie on 2019/12/2.
 */

@Data
public class StartProcessParam {
    private String flowModuleId;
    private String flowDeployId;
    private List<InstanceData> variables;
}
