package com.xiaoju.uemc.turbo.engine.common;

/**
 * 项目名称：optimus-prime
 * 类 名 称：FlowModuleStatus
 * 类 描 述：
 * 创建时间：2019/12/3 3:11 PM
 * 创 建 人：didiwangxing
 */
public class FlowModuleStatus {
    public static final int DEFAULT = 0; //数据库默认值
    public static final int EDITING = 1; //编辑中
    public static final int DEPLOYED = 2; //已部署
    public static final int DISABLED = 3; //已下线, 暂未使用
}
