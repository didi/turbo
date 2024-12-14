package com.didiglobal.turbo.engine.config;

/**
 * @Author: james zhangxiao
 * @Date: 11/30/22
 * @Description:
 */
public interface BusinessConfig {

    int COMPUTING_FLOW_NESTED_LEVEL = -1; // computing flow nested level
    int MIN_FLOW_NESTED_LEVEL = 0; // Flow don't use CallActivity node
    int MAX_FLOW_NESTED_LEVEL = 10;

    /**
     * Query callActivityNestedLevel according to caller
     * <p>
     * e.g.1 if flowA quote flowB, flowA callActivityNestedLevel equal to 1.
     * e.g.2 if flowA quote flowB, flowB quote flowC, flowA callActivityNestedLevel equal to 2.
     *
     * @param caller caller
     * @return -1 if unLimited
     */
    int getCallActivityNestedLevel(String caller);

}
