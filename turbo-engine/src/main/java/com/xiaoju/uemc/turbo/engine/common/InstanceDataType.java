package com.xiaoju.uemc.turbo.engine.common;

/**
 * Created by Stefanie on 2019/12/2.
 */
public class InstanceDataType {
    public static final int DEFAULT = 0; //数据库默认值
    public static final int INIT = 1; //实例初始化
    public static final int EXECUTE = 2; //系统执行
    public static final int HOOK = 3; //系统主动获取
    public static final int UPDATE = 4; //上游更新
    public static final int COMMIT = 5; //任务提交
    public static final int ROLLBACK = 6; //任务回滚(暂时无用, 回滚时不产生新数据, 只修改数据版本号(dbId))
}
