package com.didiglobal.turbo.spring.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didiglobal.turbo.engine.config.BusinessConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author fxz
 */
public class BusinessConfigImpl implements BusinessConfig {

    @Value("${callActivity.nested.level:#{null}}")
    private String callActivityNestedLevel;

    /**
     * Query callActivityNestedLevel according to caller
     * <p>
     * e.g.1 if flowA quote flowB, flowA callActivityNestedLevel equal to 1.
     * e.g.2 if flowA quote flowB, flowB quote flowC, flowA callActivityNestedLevel equal to 2.
     *
     * @param caller caller
     * @return -1 if unLimited
     */
    @Override
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