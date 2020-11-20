package com.xiaoju.uemc.turbo.engine.validator;

import com.alibaba.fastjson.JSON;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.exception.ModelException;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 项目名称：optimus-prime
 * 类 名 称：Element1Validator
 * 类 描 述：
 * 创建时间：2019/12/10 9:53 AM
 * 创 建 人：didiwangxing
 */
public class ElementValidator {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ElementValidator.class);

    protected void checkIncoming(Map<String, FlowElement> flowElementMap, FlowElement flowElement) throws ModelException {
        List<String> incomingList = flowElement.getIncoming();
        if (CollectionUtils.isEmpty(incomingList)) {
            LOGGER.warn("element lack incoming.||flowElement={}", JSON.toJSONString(flowElement));
            throw new ModelException(ErrorEnum.ELEMENT_LACK_INCOMING);
        }
    }

    protected void checkOutgoing(Map<String, FlowElement> flowElementMap, FlowElement flowElement) throws ModelException {
        List<String> outgoingList = flowElement.getOutgoing();
        if (CollectionUtils.isEmpty(outgoingList)) {
            LOGGER.warn("element lack outgoing.||flowElement={}", JSON.toJSONString(flowElement));
            throw new ModelException(ErrorEnum.ELEMENT_LACK_OUTGOING);
        }

        if (outgoingList.size() > 1) {
            LOGGER.warn("element has too much outgoing.||flowElement={}", JSON.toJSONString(flowElement));
            throw new ModelException(ErrorEnum.ELEMENT_TOO_MUCH_OUTGOING);
        }
    }

    protected void validator(Map<String, FlowElement> flowElementMap, FlowElement flowElement) throws ModelException {
        checkIncoming(flowElementMap, flowElement);
        checkOutgoing(flowElementMap, flowElement);
    }
}
