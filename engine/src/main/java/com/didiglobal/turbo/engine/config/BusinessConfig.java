package com.didiglobal.turbo.engine.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didiglobal.turbo.engine.util.PluginPropertiesUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: james zhangxiao
 * @Date: 11/30/22
 * @Description:
 */
public class BusinessConfig {

    private String callActivityNestedLevel =
            PluginPropertiesUtil.getPropertyValue("callActivity.nested.level");

    public static final int COMPUTING_FLOW_NESTED_LEVEL = -1; // computing flow nested level
    public static final int MIN_FLOW_NESTED_LEVEL = 0; // Flow don't use CallActivity node
    public static final int MAX_FLOW_NESTED_LEVEL = 10;

    public void setCallActivityNestedLevel(String callActivityNestedLevel) {
        if (callActivityNestedLevel != null) {
            this.callActivityNestedLevel = callActivityNestedLevel;
        }
    }

    /**
     * Query callActivityNestedLevel according to caller
     * <p>
     * e.g.1 if flowA quote flowB, flowA callActivityNestedLevel equal to 1.
     * e.g.2 if flowA quote flowB, flowB quote flowC, flowA callActivityNestedLevel equal to 2.
     *
     * @param caller caller
     * @return -1 if unLimited
     */
    public int getCallActivityNestedLevel(String caller) {
        if (StringUtils.isBlank(callActivityNestedLevel)) {
            return MAX_FLOW_NESTED_LEVEL;
        }
        JSONObject callActivityNestedLevelJO = JSON.parseObject(callActivityNestedLevel);
        if (callActivityNestedLevelJO.containsKey(caller)) {
            int callActivityNestedLevel = callActivityNestedLevelJO.getIntValue(caller);
            if (MAX_FLOW_NESTED_LEVEL < callActivityNestedLevel) {
                return MAX_FLOW_NESTED_LEVEL;
            } else {
                return callActivityNestedLevel;
            }
        } else {
            return MAX_FLOW_NESTED_LEVEL;
        }
    }
}
