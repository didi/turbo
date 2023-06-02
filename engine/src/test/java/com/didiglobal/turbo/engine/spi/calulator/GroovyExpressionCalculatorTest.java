package com.didiglobal.turbo.engine.spi.calulator;

import com.didiglobal.turbo.engine.exception.ProcessException;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GroovyExpressionCalculatorTest {

    @Test
    public void testCompare(){
        String expression = "${key_long < key_int}";
        Assert.assertFalse(ExpressionCalculatorFactory.getExpressionCalculator("groovy").calculate(expression, getData()));
    }

    @Test
    public void testCalculate(){
        String expression = "${key_long + key_int == 223}";
        Assert.assertTrue(ExpressionCalculatorFactory.getExpressionCalculator("groovy").calculate(expression, getData()));
    }

    @Test
    public void testList(){
        String expression = "key_list.contains(1)";
        Assert.assertTrue(ExpressionCalculatorFactory.getExpressionCalculator("groovy").calculate(expression, getData()));
    }

    @Test
    public void testNoSuchProperty(){
        String expression = "key_none.contains(1)";
        Boolean result = false;
        try {
            result = ExpressionCalculatorFactory.getExpressionCalculator("groovy").calculate(expression, getData());
        } catch (ProcessException e) {
            // do nothing
         }
        Assert.assertFalse(result);
    }


    public Map<String, Object> getData(){
        Map<String, Object> data = Maps.newHashMap();
        data.put("key_string", "test string");
        data.put("key_long", 123L);
        data.put("key_int", 100);
        data.put("key_double", 10.1d);
        data.put("key_float", 20.2);
        data.put("key_boolean", false);
        data.put("key_list", getList());
        return data;
    }

    private List getList() {
        return Arrays.asList(1,2,3,4,5);
    }
}
