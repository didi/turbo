package com.didiglobal.turbo.engine.common;

public enum ErrorEnum {

    //1000~1999 非阻断性错误码
    SUCCESS(1000, "Success"),
    REENTRANT_WARNING(1001, "Reentrant warning"),
    COMMIT_SUSPEND(1002, "Commit task suspend"),
    ROLLBACK_SUSPEND(1003, "Rollback task suspend"),

    //2000~2999 通用业务错误
    PARAM_INVALID(2001, "Invalid param"),

    //3000~3999 流程定义错误
    DEFINITION_INSERT_INVALID(3001, "Definition insert failed"),
    DEFINITION_UPDATE_INVALID(3002, "Definition update failed"),

    FLOW_NOT_EXIST(3101, "Flow not exist"),
    FLOW_NOT_EDITING(3102, "Flow not editing status"),

    MODEL_EMPTY(3201, "Empty model"),
    START_NODE_INVALID(3202, "Zero or more than one start node"),
    ELEMENT_KEY_NOT_UNIQUE(3203, "Element key not unique"),
    END_NODE_INVALID(3204, "No end node"),
    MODEL_NOT_UNICOM(3205, "Not unicom"),
    SEQUENCE_BELONG_TO_MULTI_PAIR_NODE(3206, "Sequence belong to multi pair node"),
    RING_WRONG(3207, "Ring wrong"),
    GATEWAY_NO_OUTGOING(3208, "Gateway no outgoing"),
    EMPTY_SEQUENCE_OUTGOING(3209, "Empty sequence outgoing"),
    TOO_MANY_DEFAULT_SEQUENCE(3210, "Too many default sequence"),
    MODEL_UNKNOWN_ELEMENT_KEY(3211, "Unknown element key"),
    ELEMENT_TOO_MANY_INCOMING(3212, "Too many incoming"),
    ELEMENT_TOO_MANY_OUTGOING(3213, "Too many outgoing"),
    ELEMENT_LACK_INCOMING(3214, "Element lack incoming"),
    ELEMENT_LACK_OUTGOING(3215, "Element lack outgoing"),

    //4000~4999 流程执行错误
    COMMIT_FAILED(4001, "Commit task failed"),
    ROLLBACK_FAILED(4002, "Rollback task failed"),
    COMMIT_REJECTRD(4003, "Commit rejected, flow is terminate"),
    ROLLBACK_REJECTRD(4004, "Rollback rejected, non-running flowInstance to rollback"),
    NO_NODE_TO_ROLLBACK(4005, "No node to rollback"),
    NO_USER_TASK_TO_ROLLBACK(4006, "No userTask to rollback"),
    GET_FLOW_DEPLOYMENT_FAILED(4007, "Get flowDeployment failed"),
    GET_FLOW_INSTANCE_FAILED(4008, "Get flowInstance failed"),
    GET_NODE_FAILED(4009, "Get current node failed"),
    GET_NODE_INSTANCE_FAILED(4010, "Get nodeInstance failed"),
    GET_INSTANCE_DATA_FAILED(4011, "Get instanceData failed"),
    GET_HOOK_CONFIG_FAILED(4012, "Get hook config failed"),
    GET_OUTGOING_FAILED(4013, "Get outgoing failed"),
    UNSUPPORTED_ELEMENT_TYPE(4014, "Unsupported element type"),
    MISSING_DATA(4015, "Miss data"),
    SAVE_FLOW_INSTANCE_FAILED(4016, "Save flowInstance failed"),
    SAVE_INSTANCE_DATA_FAILED(4017, "Save instanceData failed"),
    GROOVY_CALCULATE_FAILED(4018, "Groovy calculate failed"),
    NOT_FOUND_EXPRESSION_CALCULATOR(4019, "Not found expression calculator"),


    //5000~5999 系统错误
    //保留错误码
    SYSTEM_ERROR(5000, "System error"),
    FAILED(5001, "Failed");

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
