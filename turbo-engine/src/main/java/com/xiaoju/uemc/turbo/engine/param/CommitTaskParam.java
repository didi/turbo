package com.xiaoju.uemc.turbo.engine.param;

import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by Stefanie on 2019/12/2.
 */
@Data
@ToString(callSuper = true)
public class CommitTaskParam extends RuntimeTaskParam {
    private List<InstanceData> variables;
}
