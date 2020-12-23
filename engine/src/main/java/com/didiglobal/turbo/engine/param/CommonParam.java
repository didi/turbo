package com.didiglobal.turbo.engine.param;

import com.google.common.base.MoreObjects;

public class CommonParam {
    private String tenant;
    private String caller;

    public CommonParam(String tenant, String caller) {
        this.tenant = tenant;
        this.caller = caller;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("tenant", tenant)
                .add("caller", caller)
                .toString();
    }
}
