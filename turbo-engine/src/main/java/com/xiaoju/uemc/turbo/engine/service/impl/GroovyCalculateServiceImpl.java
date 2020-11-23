package com.xiaoju.uemc.turbo.engine.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didiglobal.reportlogger.LoggerFactory;
import com.didiglobal.reportlogger.ReportLogger;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import com.xiaoju.uemc.turbo.engine.service.CalculateService;
import com.xiaoju.uemc.turbo.engine.util.GroovyUtil;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Map;

/**
 * 项目名称：turbo
 * 类 名 称：CalculateService
 * 类 描 述：
 * 创建时间：2020/11/11 5:14 PM
 * 创 建 人：didiwangxing
 */
@Service
public class GroovyCalculateServiceImpl implements CalculateService {

    protected static final ReportLogger LOGGER = LoggerFactory.getLogger(GroovyCalculateServiceImpl.class);

    @Override
    public Boolean calculate(String expression, Map<String, Object> dataMap) throws ProcessException {
        if (expression.startsWith("${") && expression.endsWith("}")) {
            expression = expression.substring(2, expression.length() - 1);
        }
        Object result = null;
        try {
            result = GroovyUtil.execute(expression, dataMap);
            if (result instanceof Boolean) {
                return (Boolean) result;
            } else {
                LOGGER.warn("expression is not instanceof Boolean.||expression={}||dataMap={}", expression, JSON.toJSONString(dataMap));
                throw new ProcessException(ErrorEnum.GROOVY_CALCULATE_FAILED.getErrNo(), "expression is not instanceof bool.");
            }
        } catch (Throwable t) {
            LOGGER.error("processCondition exception.||message={}||expression={}||dataMap={}, ", t.getMessage(), expression, dataMap, t);
            String groovyExFormat = "{0}: expression={1}";
            throw new ProcessException(ErrorEnum.GROOVY_CALCULATE_FAILED, MessageFormat.format(groovyExFormat, t.getMessage(), expression));
        } finally {
            LOGGER.info("processCondition.||expression={}||dataMap={}||result={}", expression, JSONObject.toJSONString(dataMap), result);
        }
    }
}
