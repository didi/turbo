package com.didiglobal.turbo.engine.validator;

import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.exception.DefinitionException;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

public class ModelValidatorTest extends BaseTest {

    @Resource
    private ModelValidator modelValidator;

    /**
     * Test modelValidator, while model is normal.
     */
    @Test
    public void validateAccess() {
        String modelStr = EntityBuilder.buildModelStringAccess();
        boolean access = false;
        try {
            modelValidator.validate(modelStr);
            access = true;
        } catch (DefinitionException | ProcessException e) {
            LOGGER.error("", e);
        }
        Assert.assertTrue(access);
    }

    /**
     * Test modelValidator, while model is empty.
     */
    @Test
    public void validateEmptyModel() {
        String modelStr = null;
        boolean access = false;
        try {
            modelValidator.validate(modelStr);
            access = true;
        } catch (DefinitionException | ProcessException e) {
            Assert.assertEquals(ErrorEnum.MODEL_EMPTY.getErrNo(), e.getErrNo());
        }
        Assert.assertFalse(access);
    }

}