package com.xiaoju.uemc.turbo.engine.validator;

import com.xiaoju.uemc.turbo.engine.exception.DefinitionException;
import com.xiaoju.uemc.turbo.engine.exception.ProcessException;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;
import javax.annotation.Resource;

public class ModelValidatorTest extends BaseTest {

    @Resource ModelValidator modelValidator;

    /**
     * Test modelValidator, while model is normal.
     *
     */
    @Test
    public void validateAccess() {
        String modelStr = EntityBuilder.buildModelStringAccess();
        boolean access = false;
        try {
            modelValidator.validate(modelStr);
            access = true;
            Assert.assertTrue(access);
        } catch (DefinitionException e) {
            e.printStackTrace();
            Assert.assertTrue(access);
        } catch (ProcessException e) {
            e.printStackTrace();
            Assert.assertTrue(access);
        }


    }
    /**
     * Test modelValidator, while model is empty.
     *
     */
    @Test
    public void validateEmptyModel() {
        String modelStr = null;
        boolean access = false;
        try {
            modelValidator.validate(modelStr);
            access = true;
            Assert.assertFalse(access);
        } catch (DefinitionException e) {
            e.printStackTrace();
            Assert.assertFalse(access);
        } catch (ProcessException e) {
            e.printStackTrace();
            Assert.assertFalse(access);
        }
    }

}