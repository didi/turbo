package com.xiaoju.uemc.turbo.engine.bo;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Stefanie on 2019/12/19.
 */
@Data
@ToString(callSuper = true)
public class FlowInfo extends FlowBasicInfo {
    private String flowModel;
}
