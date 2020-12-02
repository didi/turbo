package com.xiaoju.uemc.turbo.engine.validator;

import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
