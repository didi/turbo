package com.didiglobal.turbo.engine.common;

public class FlowDefinitionStatus {
    /**
     * Db default status
     */
    public static final int DEFAULT = 0;

    /**
     * Initialize state during process creation
     */
    public static final int INIT = 1;

    /**
     * Editing
     */
    public static final int EDITING = 2;

    /**
     * Offline, not currently in use
     */
    public static final int DISABLED = 3;
}
