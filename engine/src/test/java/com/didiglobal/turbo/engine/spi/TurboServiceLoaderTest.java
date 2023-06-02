package com.didiglobal.turbo.engine.spi;

import com.didiglobal.turbo.engine.spi.calulator.ExpressionCalculator;
import com.didiglobal.turbo.engine.spi.generator.IdGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

public class TurboServiceLoaderTest {

    @Test
    public void assertExpressionCalculatorExists(){
        Collection<ExpressionCalculator> serviceInterfaces = TurboServiceLoader.getServiceInterfaces(ExpressionCalculator.class);
        Assert.assertTrue(serviceInterfaces.size() > 0);
    }

    @Test
    public void assertIdGeneratorExists(){
        Collection<IdGenerator> serviceInterfaces = TurboServiceLoader.getServiceInterfaces(IdGenerator.class);
        Assert.assertTrue(serviceInterfaces.size() > 0);
    }
}
