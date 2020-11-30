package com.xiaoju.uemc.turbo.engine.validator;

import com.xiaoju.uemc.turbo.engine.common.FlowElementType;
import com.xiaoju.uemc.turbo.engine.exception.ModelException;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import com.xiaoju.uemc.turbo.engine.model.*;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class FlowModelValidatorTest extends BaseTest {

    @Resource
    FlowModelValidator flowModelValidator;

    /**
     * Test flowModel's validate, while normal.
     *
     */
    @Test
    public void validateAccess() {
        List<FlowElement> flowElementsList = EntityBuilder.buildFlowElementList();
        FlowModel flowModel = new FlowModel();
        flowModel.setFlowElementList(flowElementsList);
        boolean access = false;
        try {
            flowModelValidator.validate(flowModel);
            access = true;
            Assert.assertTrue(access == true);
        } catch (ProcessException e) {
            e.printStackTrace();
            Assert.assertTrue(access == true);
        } catch (ModelException e) {
            e.printStackTrace();
            Assert.assertTrue(access == true);
        }
    }

    /**
     * Test flowModel's validate, while element's key is not unique.
     *
     */
    @Test
    public void validateElementKeyNotUnique() {
        List<FlowElement> flowElementsList = EntityBuilder.buildFlowElementList();
        SequenceFlow sequenceFlow1 = new SequenceFlow();
        sequenceFlow1.setKey("sequenceFlow1");
        sequenceFlow1.setType(FlowElementType.SEQUENCE_FLOW);
        List<String> sfIncomings = new ArrayList<>();
        sfIncomings.add("startEvent1");
        sequenceFlow1.setIncoming(sfIncomings);
        List<String> sfOutgoings = new ArrayList<>();
        sfOutgoings.add("userTask1");
        sequenceFlow1.setOutgoing(sfOutgoings);
        flowElementsList.add(sequenceFlow1);
        FlowModel flowModel = new FlowModel();
        flowModel.setFlowElementList(flowElementsList);
        boolean access = false;
        try {
            flowModelValidator.validate(flowModel);
            access = true;
            Assert.assertTrue(access == false);
        } catch (ProcessException e) {
            e.printStackTrace();
            Assert.assertTrue(access == false);
        } catch (ModelException e) {
            e.printStackTrace();
            Assert.assertTrue(access == false);
        }
    }

    /**
     * Test flowModel's validate, while startEvent's num is not equal 1.
     *
     */
    @Test
    public void validateStartEventNotOne() {
        List<FlowElement> flowElementsList = EntityBuilder.buildFlowElementList();

        StartEvent startEvent = new StartEvent();
        startEvent.setKey("startEvent2");
        startEvent.setType(FlowElementType.START_EVENT);
        List<String> seOutgoings = new ArrayList<>();
        seOutgoings.add("sequenceFlow1");
        startEvent.setOutgoing(seOutgoings);
        flowElementsList.add(startEvent);
        FlowModel flowModel = new FlowModel();
        flowModel.setFlowElementList(flowElementsList);
        boolean access = false;
        try {
            flowModelValidator.validate(flowModel);
            access = true;
            Assert.assertTrue(access == false);
        } catch (ProcessException e) {
            e.printStackTrace();
            Assert.assertTrue(access == false);
        } catch (ModelException e) {
            e.printStackTrace();
            Assert.assertTrue(access == false);
        }
    }

    /**
     * Test flowModel's validate, while endEvent is null.
     *
     */
    @Test
    public void validateWithoutEndEvent() {
        List<FlowElement> flowElementsList = EntityBuilder.buildFlowElementList();
        int flowElementsListSize = flowElementsList.size();
        flowElementsList.remove(flowElementsListSize-1);
        flowElementsList.remove(flowElementsListSize-2);
        FlowModel flowModel = new FlowModel();
        flowModel.setFlowElementList(flowElementsList);
        boolean access = false;
        try {
            flowModelValidator.validate(flowModel);
            access = true;
            Assert.assertTrue(access == false);
        } catch (ProcessException e) {
            e.printStackTrace();
            Assert.assertTrue(access == false);
        } catch (ModelException e) {
            e.printStackTrace();
            Assert.assertTrue(access == false);
        }
    }
}