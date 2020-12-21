package com.xiaoju.uemc.turbo.engine.util;

import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import java.util.Map;

/**
 * 项目名称：turbo
 * 类 名 称：ExpressionCalculator
 * 类 描 述：
 * 创建时间：2020/11/11 5:39 PM
 * 创 建 人：didiwangxing
 */
public interface ExpressionCalculator {

    Boolean calculate(String expression, Map<String, Object> dataMap) throws ProcessException;
}
