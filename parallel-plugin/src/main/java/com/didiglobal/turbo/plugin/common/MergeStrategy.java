package com.didiglobal.turbo.plugin.common;

public class MergeStrategy {

    /**
     * 分支汇聚策略
     */
    public static final class BRANCH_MERGE {
        /**
         * 全部到达后进行汇聚
         */
        public static final String JOIN_ALL = "JoinAll";

        /**
         * 任意先到达的
         */
        public static final String ANY_ONE = "AnyOne";

        /**
         * 自定义脚本
         */
        public static final String CUSTOM = "Custom";
    }

    /**
     * 流程汇聚策略
     */
    public static final class DATA_MERGE {
        public static final String ALL = "All";
        public static final String NONE = "None";
        public static final String CUSTOM = "Custom";
    }

}
