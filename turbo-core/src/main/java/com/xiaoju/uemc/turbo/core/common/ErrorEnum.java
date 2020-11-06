package com.xiaoju.uemc.turbo.core.common;

/**
 * Created by Stefanie on 2019/12/5.
 */
public enum ErrorEnum {

    //1000~1999 非阻断性错误码
    SUCCESS(1000, "处理成功"),
    REENTRANT_WARNING(1001, "重复处理"),
    COMMIT_SUSPEND(1002, "任务待提交"),
    ROLLBACK_SUSPEND(1003, "任务待撤销"),

    //2000~2999 通用业务错误
    PARAM_INVALID(2001, "参数错误"),

    //3000~3999 流程定义错误
    DEFINITION_INSERT_INVALID(3001, "数据库插入失败"),
    DEFINITION_UPDATE_INVALID(3002, "数据库更新失败"),

    MODEL_EMPTY(3201, "模型为空"),
    MODEL_MUST_ONE_START_NODE(3202, "流程必须有且仅有一个开始节点"),
    ELEMENT_KEY_NOT_UNIQUE(3203, "流程元素key必须唯一"),
    MODEL_NO_END_NODE(3204, "流程至少需要有一个结束节点"),
    MODEL_NOT_UNICOM(3205, "该流程从开始节点不能到底每一个节点"),
    EDGE_BELONG_TO_MULTI_PAIR_NODE(3206, "边应该属于一个入口节点和一个出口节点"),
    RING_WRONG(3207, "流程中环结构中必须至少包含一个用户节点"),
    GATEWAY_NO_OUTGOING(3208, "网关节点应该至少有一条出口"),
    GATEWAY_EMPTY_EDGE_OUTGOING(3209, "网关节点条件分支除默认分支外均需要配置条件表达式"),
    GATEWAY_TOO_MANY_DEFAULT_EDGE(3210, "网关节点最多只能有一条默认分支"),
    MODEL_UNKNOWN_ELEMENT_KEY(3211, "不支持该类型"),
    NORMAL_NODE_MUST_ONE_OUTGOING(3212, "非条件判断节点以及起始节点外，其他节点有且仅有一个出口"),

    //4000~4999 流程执行错误
    COMMIT_FAILED(4001, "提交失败"),
    ROLLBACK_FAILED(4002, "撤销失败"),
    TERMINATE_CANNOT_COMMIT(4003, "流程实例已终止, 无法继续提交"),
    FLOW_INSTANCE_CANNOT_ROLLBACK(4004, "非执行中流程实例, 无法撤销"),
    NO_NODE_TO_ROLLBACK(4005, "该实例没有可撤销的节点"),
    NO_USER_TASK_TO_ROLLBACK(4006, "已完成第一个用户节点的撤销, 无法继续撤销"),
    GET_FLOW_DEPLOYMENT_FAILED(4007, "获取不到流程部署信息"),
    GET_FLOW_INSTANCE_FAILED(4008, "获取不到流程实例信息"),
    GET_NODE_FAILED(4009, "获取不到待处理的节点"),
    GET_NODE_INSTANCE_FAILED(4010, "获取不到节点实例信息"),
    GET_INSTANCE_DATA_FAILED(4011, "获取不到实例数据信息"),
    GET_HOOK_CONFIG_FAILED(4012, "获取不到hook配置"),
    GET_OUTGOING_FAILED(4013, "找不到下一个待执行节点"),
    UNSUPPORTED_ELEMENT_TYPE(4014, "不支持的节点操作"),
    MISSING_DATA(4015, "表达式运算缺少数据"),
    SAVE_FLOW_INSTANCE_FAILED(4016, "保存流程实例失败"),
    SAVE_INSTANCE_DATA_FAILED(4017, "保存实例数据失败"),
    GROOVY_CALCULATE_FAILED(4018, "表达式执行失败"),

    //5000~5999 系统错误
    //保留错误码
    SYSTEM_ERROR(5000, "系统异常"),
    FAILED(5001, "处理失败"),
    NOT_AUTHORIZED(5002, "没有权限"),

    //6000~6999 数据库错误
    CONVERT_SHARD_CONFIG_EXCEPTION(6001, "解析分表配置失败"),
    INVALID_SHARD_CONFIG(6002, "分表配置不合法"),
    EMPTY_SHARD_PARAM(6003, "分表参数为空"),
    GET_MAPPER_FAILED(6004, "解析Mapper失败"),
    GET_SHARD_VALUE_FAILED(6005, "获取分表Number失败");

    ErrorEnum(int errNo, String errMsg) {
        this.errNo = errNo;
        this.errMsg = errMsg;
    }

    private int errNo;
    private String errMsg;

    public int getErrNo() {
        return errNo;
    }

    public void setErrNo(int errNo) {
        this.errNo = errNo;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public static boolean isSuccess(int errNo) {
        return errNo >= 1000 && errNo < 2000;
    }
}
