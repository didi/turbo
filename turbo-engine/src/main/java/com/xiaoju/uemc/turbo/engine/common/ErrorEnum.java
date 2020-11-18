package com.xiaoju.uemc.turbo.engine.common;

/**
 * Created by Stefanie on 2019/12/5.
 */
public enum ErrorEnum {

    //1000~1999 非阻断性错误码
    SUCCESS(1000, "处理成功"), //SUCCESS(1000, "Success"),
    REENTRANT_WARNING(1001, "重复处理"), //REENTRANT_WARNING(1001, "Reentrant warning"),
    COMMIT_SUSPEND(1002, "任务待提交"), //COMMIT_SUSPEND(1002, "Commit task suspend"),
    ROLLBACK_SUSPEND(1003, "任务待撤销"),//ROLLBACK_SUSPEND(1003, "Rollback task suspend"),

    //2000~2999 通用业务错误
    PARAM_INVALID(2001, "参数错误"),//PARAM_INVALID(2001, "Invalid param"),

    //3000~3999 流程定义错误
    DEFINITION_INSERT_INVALID(3001, "数据库插入失败"), //DEFINITION_INSERT_INVALID(3001, "Definition insert failed"),
    DEFINITION_UPDATE_INVALID(3002, "数据库更新失败"), //DEFINITION_UPDATE_INVALID(3002, "Definition update failed"),

    MODEL_EMPTY(3201, "模型为空"),//MODEL_EMPTY(3201, "Empty model"),
    MODEL_MUST_ONE_START_NODE(3202, "流程必须有且仅有一个开始节点"),//START_NODE_INVALID(3202, "Zero or more than one start node"),
    ELEMENT_KEY_NOT_UNIQUE(3203, "流程元素key必须唯一"),//ELEMENT_KEY_NOT_UNIQUE(3203, "Element key not unique"),
    MODEL_NO_END_NODE(3204, "流程至少需要有一个结束节点"),//END_NODE_INVALID(3204, "No end node"),
    MODEL_NOT_UNICOM(3205, "该流程从开始节点不能到底每一个节点"),//MODEL_NOT_UNICOM(3205, "Not unicom"),
    EDGE_BELONG_TO_MULTI_PAIR_NODE(3206, "边应该属于一个入口节点和一个出口节点"),//SEQUENCE_BELONG_TO_MULTI_PAIR_NODE(3206, "Sequence belong to multi pair node"),
    RING_WRONG(3207, "流程中环结构中必须至少包含一个用户节点"),//RING_WRONG(3207, "Ring wrong"),
    GATEWAY_NO_OUTGOING(3208, "网关节点应该至少有一条出口"),//GATEWAY_NO_OUTGOING(3208, "Gateway no outgoing"),
    GATEWAY_EMPTY_EDGE_OUTGOING(3209, "网关节点条件分支除默认分支外均需要配置条件表达式"),//GATEWAY_EMPTY_SEQUENCE_OUTGOING(3209, "Empty sequence outgoing"),
    GATEWAY_TOO_MANY_DEFAULT_EDGE(3210, "网关节点最多只能有一条默认分支"),//GATEWAY_TOO_MANY_DEFAULT_SEQUENCE(3210, "Too many default sequence"),
    MODEL_UNKNOWN_ELEMENT_KEY(3211, "不支持该类型"),//MODEL_UNKNOWN_ELEMENT_KEY(3211, "Unknown element key"),
    NORMAL_NODE_MUST_ONE_OUTGOING(3212, "非条件判断节点以及起始节点外，其他节点有且仅有一个出口"),//NORMAL_NODE_TOO_MANY_OUTGOING(3212, "Too many outgoing"),

    //4000~4999 流程执行错误
    COMMIT_FAILED(4001, "提交失败"),//COMMIT_FAILED(4001, "Commit task failed"),
    ROLLBACK_FAILED(4002, "撤销失败"),//ROLLBACK_FAILED(4002, "Rollback task failed"),
    TERMINATE_CANNOT_COMMIT(4003, "流程实例已终止, 无法继续提交"),//COMMIT_REJECTRD(4003, "Commit rejected, flow is terminate"),
    FLOW_INSTANCE_CANNOT_ROLLBACK(4004, "非执行中流程实例, 无法撤销"),//ROLLBACK_REJECTRD(4004, "Rollback rejected, non-running flowInstance rollback"),
    NO_NODE_TO_ROLLBACK(4005, "该实例没有可撤销的节点"),//NO_NODE_TO_ROLLBACK(4005, "No node to rollback"),
    NO_USER_TASK_TO_ROLLBACK(4006, "已完成第一个用户节点的撤销, 无法继续撤销"),//NO_USER_TASK_TO_ROLLBACK(4006, "No userTask to rollback"),
    GET_FLOW_DEPLOYMENT_FAILED(4007, "获取不到流程部署信息"),//GET_FLOW_DEPLOYMENT_FAILED(4007, "Get flowDeployment failed"),
    GET_FLOW_INSTANCE_FAILED(4008, "获取不到流程实例信息"),//GET_FLOW_INSTANCE_FAILED(4008, "Get flowInstance failed"),
    GET_NODE_FAILED(4009, "获取不到待处理的节点"),//GET_NODE_FAILED(4009, "Get current node failed"),
    GET_NODE_INSTANCE_FAILED(4010, "获取不到节点实例信息"),//GET_NODE_INSTANCE_FAILED(4010, "Get nodeInstance failed"),
    GET_INSTANCE_DATA_FAILED(4011, "获取不到实例数据信息"),//GET_INSTANCE_DATA_FAILED(4011, "Get instanceData failed"),
    GET_HOOK_CONFIG_FAILED(4012, "获取不到hook配置"),//GET_HOOK_CONFIG_FAILED(4012, "Get hook config failed"),
    GET_OUTGOING_FAILED(4013, "找不到下一个待执行节点"),//GET_OUTGOING_FAILED(4013, "Get outgoing failed"),
    UNSUPPORTED_ELEMENT_TYPE(4014, "不支持的节点操作"),//UNSUPPORTED_ELEMENT_TYPE(4014, "Unsupported element type"),
    MISSING_DATA(4015, "表达式运算缺少数据"),//MISSING_DATA(4015, "Miss data"),
    SAVE_FLOW_INSTANCE_FAILED(4016, "保存流程实例失败"),//SAVE_FLOW_INSTANCE_FAILED(4016, "Save flowInstance failed"),
    SAVE_INSTANCE_DATA_FAILED(4017, "保存实例数据失败"),//SAVE_INSTANCE_DATA_FAILED(4017, "Save instanceData failed"),
    GROOVY_CALCULATE_FAILED(4018, "表达式执行失败"),//GROOVY_CALCULATE_FAILED(4018, "Groovy calculate failed"),

    //5000~5999 系统错误
    //保留错误码
    SYSTEM_ERROR(5000, "系统异常"),//SYSTEM_ERROR(5000, "System error"),
    FAILED(5001, "处理失败");//FAILED(5001, "Failed");

    /*
    *
    *
    * SUCCESS(1000, "Success"),
REENTRANT_WARNING(1001, "Reentrant warning"),
COMMIT_SUSPEND(1002, "Commit task suspend"),
ROLLBACK_SUSPEND(1003, "Rollback task suspend"),

//2000~2999 通用业务错误
PARAM_INVALID(2001, "Invalid param"),

//3000~3999 流程定义错误
DEFINITION_INSERT_INVALID(3001, "Database insert failed"),
DEFINITION_UPDATE_INVALID(3002, "Database update failed"),

MODEL_EMPTY(3201, "Empty model"),
MODEL_NO_START_NODE(3202, "No start node"),
ELEMENT_KEY_NOT_UNIQUE(3203, "Element key not unique"),
MODEL_NO_END_NODE(3204, "No end node"),
MODEL_NOT_UNICOM(3205, "No unicom"),
SEQUENCE_BELONG_TO_MULTI_PAIR_NODE(3206, "Sequence belong to multi pair node"),
RING_WRONG(3207, "Ring wrong"),
GATEWAY_NO_OUTGOING(3208, "Gateway no outgoing"),
GATEWAY_EMPTY_SEQUENCE_OUTGOING(3209, "Empty sequence outgoing"),
GATEWAY_TOO_MANY_DEFAULT_SEQUENCE(3210, "Too many default sequence"),
MODEL_UNKNOWN_ELEMENT_KEY(3211, "Unknown element key"),
NORMAL_NODE_TOO_MANY_OUTGOING(3212, "Too many outgoing"),

//4000~4999 流程执行错误
COMMIT_FAILED(4001, "Commit task failed"),
ROLLBACK_FAILED(4002, "Rollback task failed"),
TERMINATE_FLOW_COMMIT(4003, "Terminate flow commit"),
FLOW_INSTANCE_ROLLBACK(4004, "Non-running flow instance rollback"),
NO_NODE_TO_ROLLBACK(4005, "No node to rollback"),
NO_USER_TASK_TO_ROLLBACK(4006, "No user task to rollback"),
GET_FLOW_DEPLOYMENT_FAILED(4007, "Get flow deployment failed"),
GET_FLOW_INSTANCE_FAILED(4008, "Get flow instance failed"),
GET_NODE_FAILED(4009, "Get current node failed"),
GET_NODE_INSTANCE_FAILED(4010, "Get node instance failed"),
GET_INSTANCE_DATA_FAILED(4011, "Get instance data failed"),
GET_HOOK_CONFIG_FAILED(4012, "Get hook config failed"),
GET_OUTGOING_FAILED(4013, "Get outgoing failed"),
UNSUPPORTED_ELEMENT_TYPE(4014, "Unsupported element type"),
MISSING_DATA(4015, "Miss data"),
SAVE_FLOW_INSTANCE_FAILED(4016, "Save flow instance failed"),
SAVE_INSTANCE_DATA_FAILED(4017, "Save instance data failed"),
GROOVY_CALCULATE_FAILED(4018, "Groovy calculate failed"),

//5000~5999 系统错误
//保留错误码
SYSTEM_ERROR(5000, "System error"),
FAILED(5001, "Failed");*/

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
