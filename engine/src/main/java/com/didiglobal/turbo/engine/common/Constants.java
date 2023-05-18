package com.didiglobal.turbo.engine.common;

public class Constants {

    public static final int DEFAULT_TIMEOUT = 3000;

    public static final String ELEMENTLIST = "flowElementList";

    public static final class ELEMENT_PROPERTIES {
        public static final String NAME = "name";
        public static final String CONDITION = "conditionsequenceflow";
        public static final String DEFAULT_CONDITION = "defaultConditions";
        public static final String HOOK_INFO_IDS = "hookInfoIds";
        public static final String CALL_ACTIVITY_EXECUTE_TYPE = "callActivityExecuteType";
        public static final String CALL_ACTIVITY_INSTANCE_TYPE = "callActivityInstanceType";
        public static final String CALL_ACTIVITY_FLOW_MODULE_ID = "callActivityFlowModuleId";
        public static final String CALL_ACTIVITY_IN_PARAM_TYPE = "callActivityInParamType";
        public static final String CALL_ACTIVITY_IN_PARAM = "callActivityInParam";
        public static final String CALL_ACTIVITY_OUT_PARAM_TYPE = "callActivityOutParamType";
        public static final String CALL_ACTIVITY_OUT_PARAM = "callActivityOutParam";
    }

    public static final class CALL_ACTIVITY_PARAM_TYPE {
        public static final String NONE = "none";
        public static final String PART = "part";
        public static final String FULL = "full";
    }

    public static final class CALL_ACTIVITY_EXECUTE_TYPE {
        public static final String SYNC = "sync";
        public static final String ASYNC = "async";
    }

    public static final class CALL_ACTIVITY_INSTANCE_TYPE {
        public static final String SINGLE = "single";
        public static final String MULTIPLE = "multiple";
    }

    public static final class CALL_ACTIVITY_DATA_TRANSFER_TYPE {
        public static String SOURCE_TYPE_CONTEXT = "context";
        public static String SOURCE_TYPE_FIXED = "fixed";
    }

    public static final String NODE_INFO_FORMAT = "nodeKey={0}, nodeName={1}, nodeType={2}";
    public static final String NODE_INSTANCE_FORMAT = "nodeKey={0}, nodeName={1}, nodeInstanceId={2}";
    public static final String MODEL_DEFINITION_ERROR_MSG_FORMAT = "message={0}, elementName={1}, elementKey={2}";
}
