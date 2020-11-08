package com.xiaoju.uemc.turbo.engine.common;

/**
 * Created by Stefanie on 2019/12/16.
 */
public class Constants {

    public static final int DEFAULT_TIMEOUT = 3000;

    public static final String ELEMENTLIST = "flowElementList";

    public static final class ELEMENT_PROPERTIES {
        public static final String NAME = "name";
        public static final String CONDITION = "conditionsequenceflow";
        public static final String DEFAULT_CONDITION = "defaultConditions";
        public static final String HOOK_INFO_IDS = "hookInfoIds";
    }

    public static final String NODE_INFO_FORMAT = "nodeKey={0}, nodeName={1}, nodeType={2}";
    public static final String NODE_INSTANCE_FORMAT = "nodeKey={0}, nodeName={1}, nodeInstanceId={2}";
    public static final String MODEL_DEFINITION_ERROR_MSG_FORMAT = "message={0}, elementName={1}, elementKey={2}";
}
