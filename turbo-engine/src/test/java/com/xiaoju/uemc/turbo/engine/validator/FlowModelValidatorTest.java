package com.xiaoju.uemc.turbo.engine.validator;

import com.xiaoju.uemc.turbo.engine.common.FlowElementType;
import com.xiaoju.uemc.turbo.engine.exception.ModelException;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import com.xiaoju.uemc.turbo.engine.model.EndEvent;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.model.FlowModel;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class FlowModelValidatorTest extends BaseTest {

    @Resource
    FlowModelValidator flowModelValidator;
    @Resource
    private ElementValidatorFactory elementValidatorFactory;

    @Test
    public void validate() {
        List<FlowElement> flowElementsList = EntityBuilder.buildFlowElementList();
        FlowModel flowModel = new FlowModel();
        flowModel.setFlowElementList(flowElementsList);
        try {
            flowModelValidator.validate(flowModel);
        } catch (ProcessException e) {
            e.printStackTrace();
        } catch (ModelException e) {
            e.printStackTrace();
        }
    }

}