package com.didiglobal.turbo.engine.common;

public class RedisConstants {

    public static final String REDIS_PREFIX = "ior:"; //basic

    //k=flowDeployId; v=deployId+flowModuleId+tenantId+flowModel
    public static final String FLOW_INFO = REDIS_PREFIX + "flow_info_";

    //k=flowInstanceId; v=flowInstanceId+deployId+status
    public static final String FLOW_INSTANCE = REDIS_PREFIX + "flow_instance_";

    public static final int FLOW_EXPIRED_SECOND = 30 * 24 * 60 * 60; //30 days

    public static final int FLOW_INSTANCE_EXPIRED_SECOND = 24 * 60 * 60; //1 day
}
