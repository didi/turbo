package com.didiglobal.turbo.engine.param;

import com.google.common.base.MoreObjects;

public class OperationParam extends CommonParam{
    private String operator;

    public OperationParam(String tenant, String caller) {
        super(tenant, caller);
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("tenant", getTenant())
                .add("caller", getCaller())
                .add("operator", operator)
                .toString();
    }
}
