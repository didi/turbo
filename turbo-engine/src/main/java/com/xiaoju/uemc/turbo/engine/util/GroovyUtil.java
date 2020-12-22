package com.xiaoju.uemc.turbo.engine.util;

import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;
import groovy.lang.Script;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stefanie on 2019/12/13.
 */
// TODO: 2019/12/16
public class GroovyUtil {

    protected static final Logger LOGGER = LoggerFactory.getLogger(GroovyUtil.class);

    private static final Map<String, Class> SCRIPT_CLASS_CACHE = new ConcurrentHashMap<String, Class>();

    private GroovyUtil() {}

    public static Object execute(String expression, Map<String, Object> dataMap) throws Exception {
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
