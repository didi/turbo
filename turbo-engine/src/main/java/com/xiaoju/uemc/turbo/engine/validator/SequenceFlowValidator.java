package com.xiaoju.uemc.turbo.engine.validator;

import com.alibaba.fastjson.JSON;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.exception.ModelException;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
public class SequenceFlowValidator extends ElementValidator {

    @Override
    protected void checkIncoming(Map<String, FlowElement> flowElementMap, FlowElement flowElement) throws ModelException {
        super.checkIncoming(flowElementMap, flowElement);
        List<String> incomingList = flowElement.getIncoming();
        if (incomingList.size() >1) {
            LOGGER.warn("element has too much incoming.||flowElement={}", JSON.toJSONString(flowElement));
            throw new ModelException(ErrorEnum.ELEMENT_LACK_INCOMING);
        }

    }

    @Override
    protected void checkOutgoing(Map<String, FlowElement> flowElementMap, FlowElement flowElement) {
        List<String> outgoing = flowElement.getOutgoing();
        if (CollectionUtils.isNotEmpty(outgoing)) {
            LOGGER.warn("element has unexpected outgoing.||flowElement={}", JSON.toJSONString(flowElement));
        }
    }
}
