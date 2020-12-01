package com.xiaoju.uemc.turbo.engine.dto;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Stefanie on 2019/12/2.
 */
@Data
@ToString(callSuper = true)
public class StartProcessResult extends RuntimeResult {
    private String flowDeployId;
    private String flowModuleId;
}
