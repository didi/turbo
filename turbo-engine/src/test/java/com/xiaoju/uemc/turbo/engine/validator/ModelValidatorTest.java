package com.xiaoju.uemc.turbo.engine.validator;

import com.xiaoju.uemc.turbo.engine.exception.ModelException;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.*;

public class ModelValidatorTest extends BaseTest {

    @Resource ModelValidator modelValidator;

    /**
     *
     *  Zero or more than one start node
     */

    @Test
    public void validate() {
        String modelStr = EntityBuilder.buildModelString();
        try {
            modelValidator.validate(modelStr);
        } catch (ModelException e) {
            e.printStackTrace();
        } catch (ProcessException e) {
            e.printStackTrace();
        }


    }
}