package com.xiaoju.uemc.turbo.engine.validator;

import com.alibaba.fastjson.JSON;
import com.xiaoju.uemc.turbo.engine.common.Constants;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.exception.ModelException;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.util.FlowModelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：optimus-prime
 * 类 名 称：Element1Validator
 * 类 描 述：
 * 创建时间：2019/12/10 9:53 AM
 * 创 建 人：didiwangxing
 */
@Component
public class StartEventValidator extends ElementValidator {

    protected static final Logger LOGGER = LoggerFactory.getLogger(StartEventValidator.class);

    @Override
    public void checkIncoming(Map<String, FlowElement> flowElementMap, FlowElement flowElement) {
        List<String> incoming = flowElement.getIncoming();

        if (CollectionUtils.isNotEmpty(incoming)) {
            String exceptionMsg = getElementValidatorExceptionMsg(flowElement, ErrorEnum.ELEMENT_TOO_MUCH_INCOMING);
            LOGGER.warn(exceptionMsg);
        }
    }
}
