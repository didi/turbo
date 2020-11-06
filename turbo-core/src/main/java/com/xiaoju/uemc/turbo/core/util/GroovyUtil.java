package com.xiaoju.uemc.turbo.core.util;

import com.didiglobal.reportlogger.LoggerFactory;
import com.didiglobal.reportlogger.ReportLogger;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaoju.uemc.turbo.core.common.ErrorEnum;
import com.xiaoju.uemc.turbo.core.exception.ProcessException;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;
import groovy.lang.Script;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stefanie on 2019/12/13.
 */
// TODO: 2019/12/16
public class GroovyUtil {

    protected static final ReportLogger LOGGER = LoggerFactory.getLogger(GroovyUtil.class);

    //缓存脚本编译生成的Script类，解决groovy解析脚本动态生成过多class类占满Perm区不断触发fullGC的bug。
    private static final Map<String, Class> SCRIPT_CLASS_CACHE = new ConcurrentHashMap<String, Class>();

    public static void main(String[] args) {
        String groovyExpression = "{\"companyId\":\"${rentCarCompanyId}\"}";
//        String groovyExpression1 = "rentCarCompanyId==1";
//        String groovyExpression1 = "rentCarCompanyId.equals(\"1\")";
        String groovyExpression1 = "list.contains(\"2\")";
        Map<String, Object> infos = Maps.newHashMap();
        infos.put("rentCarCompanyId", "1");
        List<String> data1List = Lists.newArrayList();
        data1List.add("2");
        infos.put("list", data1List);

        try {
            Object res = calculate(groovyExpression1, infos);
            System.out.println("====res===" + res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 这个是数学公式计算的方法。返回值类型代表计算结果的类型
     *
     * @param expression 表达式,变量不包含$符号 例如：(1 * orderPrice+returnMoney-deductMoney)/2 + 1000;
     *                   eg1.rentCarCompanyId==1;
     *                   eg2.rentCarCompanyId.equals(\"1\");
     *                   eg3.list.contains("2");
     * @param dataMap    计算表达式需要的变量
     * @return 返回计算结果，class表示数据类型
     */
    public static Object calculate(String expression, Map<String, Object> dataMap) throws Exception {
        if (StringUtils.isBlank(expression)) {
            LOGGER.warn("calculate: expression is empty");
            return null;
        }
        try {
            Binding binding = createBinding(dataMap);
            Script shell = createScript(expression, binding);
            Object resultObject = shell.run();
            LOGGER.info("calculate.||expression={}||resultObject={}", expression, resultObject);
            return resultObject;
        } catch (MissingPropertyException mpe) {
            LOGGER.warn("calculate MissingPropertyException.||expression={}||dataMap={}", expression, dataMap);
            throw new ProcessException(ErrorEnum.MISSING_DATA.getErrNo(), mpe.getMessage());
        }
    }

    private static Script createScript(String groovyExpression, Binding binding) {
        Script script;
        if (SCRIPT_CLASS_CACHE.containsKey(groovyExpression)) {
            Class scriptClass = SCRIPT_CLASS_CACHE.get(groovyExpression);
            script = InvokerHelper.createScript(scriptClass, binding);
        } else {
            script = new GroovyShell(binding).parse(groovyExpression);
            SCRIPT_CLASS_CACHE.put(groovyExpression, script.getClass());
        }
        return script;
    }

    private static Binding createBinding(Map<String, Object> infos) {
        Binding binding = new Binding();
        if (!infos.isEmpty()) {
            for (Map.Entry<String, Object> entry : infos.entrySet()) {
                binding.setVariable(entry.getKey(), entry.getValue());
            }
        }
        return binding;
    }

}
