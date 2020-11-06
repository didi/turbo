package com.xiaoju.uemc.turbo.core.common;

/**
 * Created by Stefanie on 2019/12/2.
 */
public class FlowDefinitionStatus {
    public static final int DEFAULT = 0; //数据库默认值
    public static final int INIT = 1; //流程创建, 初始化
    public static final int EDITING = 2; //编辑中
    public static final int DISABLED = 3; //已下线, 暂未使用
}
