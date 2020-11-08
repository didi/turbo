package com.xiaoju.uemc.turbo.engine.param;

import com.google.common.base.MoreObjects;

/**
 * Created by Stefanie on 2019/12/2.
 */
public class RecallTaskParam extends RuntimeTaskParam {

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("flowInstanceId", getFlowInstanceId())
                .add("taskInstanceId", getTaskInstanceId())
                .toString();
    }
}
