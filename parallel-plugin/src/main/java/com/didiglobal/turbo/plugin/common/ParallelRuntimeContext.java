package com.didiglobal.turbo.plugin.common;

import com.didiglobal.turbo.engine.common.ExtendRuntimeContext;
import com.google.common.base.MoreObjects;

public class ParallelRuntimeContext extends ExtendRuntimeContext {

    /**
     * 分支执行ID
     */
    private String executeId;
    public String getExecuteId() {
        return executeId;
    }

    public void setExecuteId(String executeId) {
        this.executeId = executeId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("executeId", executeId)
                .add("branchExecuteDataMap", getBranchExecuteDataMap())
                .add("branchSuspendNodeInstance", getBranchSuspendNodeInstance())
                .add("currentNodeModel", getCurrentNodeModel())
                .add("exception", getException())
                .toString();
    }
}
