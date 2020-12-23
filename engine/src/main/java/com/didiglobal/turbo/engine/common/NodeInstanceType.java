package com.didiglobal.turbo.engine.common;

public class NodeInstanceType {
    public static final int DEFAULT = 0; //数据库默认值
    public static final int EXECUTE = 1; //系统执行
    public static final int COMMIT = 2; //任务提交
    public static final int ROLLBACK = 3; //任务撤销
}
