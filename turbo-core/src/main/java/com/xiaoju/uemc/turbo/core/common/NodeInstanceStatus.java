package com.xiaoju.uemc.turbo.core.common;

/**
 * Created by Stefanie on 2019/12/2.
 */
public class NodeInstanceStatus {
    public static final int DEFAULT = 0; //数据库默认值
    public static final int COMPLETED = 1; //处理成功
    public static final int ACTIVE = 2; //处理中
    public static final int FAILED = 3; //处理失败
    public static final int DISABLED = 4; //处理已撤销
}
