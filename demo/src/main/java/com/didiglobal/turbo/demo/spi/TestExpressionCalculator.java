package com.didiglobal.turbo.demo.spi;

import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.spi.calulator.ExpressionCalculator;
import com.google.common.collect.Maps;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestExpressionCalculator implements ExpressionCalculator {

    @Override
    public String getType() {
        return "test";
    }

    @Override
    public Boolean calculate(String expression, Map<String, Object> dataMap) throws ProcessException {
        // 扩展表达式计算方式

        // 执行正则表达式或者其他语言脚本等,如 python
        Pattern compile = Pattern.compile(expression);
        Matcher data = compile.matcher((String) dataMap.get("data"));
        return data.matches();
    }


    /**
     * test
     *
     * @param args
     */
    public static void main(String[] args) {
        TestExpressionCalculator testExpressionCalculator = new TestExpressionCalculator();
        String expression = "[0-9]";
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("data", "1");
        try {
            Boolean result = testExpressionCalculator.calculate(expression, dataMap);
            Assert.isTrue(result, "test fail");
        } catch (ProcessException e) {
            e.printStackTrace();
        }
    }
}
