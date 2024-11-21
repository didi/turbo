package com.didiglobal.turbo.plugin.common;

public enum ParallelErrorEnum {
    // 兼容ErrorEnum中返回码1000-2000 为成功
    WAITING_SUSPEND(1601, "Join gateway waiting suspend"),
    PARALLEL_EXECUTE_TIMEOUT(6002, "Parallel execute timeout, please obtain the latest process execute status through query"),
    PARALLEL_EXECUTE_REENTRY(6003, "Parallel execute reentry"),
    REQUIRED_ELEMENT_ATTRIBUTES(6004, "required element attributes"),
    FORK_AND_JOIN_NOT_MATCH(6005, "Fork and join not match"),
    UNSUPPORTED_DATA_MERGE_STRATEGY(6006, "Unsupported data merge strategy"),
    UNSUPPORTED_BRANCH_MERGE_STRATEGY(6007, "Unsupported branch merge strategy"),
    BRANCH_MERGE_STRATEGY_ERROR(6008, "Branch merge strategy error"),
    NOT_FOUND_FORK_INSTANCE(6009, "Not found fork instance"),
    NOT_SUPPORT_ROLLBACK(6010, "Parallel and inclusive gateways are not supported for rollback");


    ParallelErrorEnum(int errNo, String errMsg) {
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

    public static com.didiglobal.turbo.engine.common.ErrorEnum getErrorEnum(int errNo) {
        for (com.didiglobal.turbo.engine.common.ErrorEnum e : com.didiglobal.turbo.engine.common.ErrorEnum.values()) {
            if (e.getErrNo() == errNo) {
                return e;
            }
        }
        return null;
    }
}
