package com.didiglobal.turbo.engine.common;

/**
 * Type of instance data recording
 */
public class InstanceDataType {
    /**
     * Default
     */
    public static final int DEFAULT = 0;

    /**
     * Instance initialization
     */
    public static final int INIT = 1;

    /**
     * Flow execute
     */
    public static final int EXECUTE = 2;

    /**
     * Active acquisition by the system
     */
    public static final int HOOK = 3;

    /**
     * Invoker actively updates
     */
    public static final int UPDATE = 4;

    /**
     * Submit Task
     */
    public static final int COMMIT = 5;

    /**
     * Rollback Task
     * Not used. No new data is generated during rollback,
     * only the data version number (dbId) is modified
     */
    public static final int ROLLBACK = 6;
}
