package com.didiglobal.turbo.engine.common;

public class FlowInstanceStatus {

    /**
     * Process initial state
     */
    public static final int DEFAULT = 0;

    /**
     * The process is completed normally, final state
     */
    public static final int COMPLETED = 1;

    /**
     * Process execution, non final state
     */
    public static final int RUNNING = 2;

    /**
     * Process forced end, final state
     */
    public static final int TERMINATED = 3;

    /**
     * The final state of the sub process, supporting rollback
     */
    public static final int END = 4;

}
