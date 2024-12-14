package com.didiglobal.turbo.engine.processor;


import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.param.CreateFlowParam;
import com.didiglobal.turbo.engine.param.DeployFlowParam;
import com.didiglobal.turbo.engine.param.GetFlowModuleParam;
import com.didiglobal.turbo.engine.param.UpdateFlowParam;
import com.didiglobal.turbo.engine.result.CreateFlowResult;
import com.didiglobal.turbo.engine.result.DeployFlowResult;
import com.didiglobal.turbo.engine.result.FlowModuleResult;
import com.didiglobal.turbo.engine.result.UpdateFlowResult;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;


public class DefinitionProcessorTest extends BaseTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefinitionProcessor.class);
    @Resource DefinitionProcessor definitionProcessor;

    @Test
    public void createTest() {
        CreateFlowParam createFlowParam = EntityBuilder.buildCreateFlowParam();
        CreateFlowResult createFlowResult = definitionProcessor.create(createFlowParam);
        LOGGER.info("createFlow.||createFlowResult={}", createFlowResult);
        Assert.assertEquals(createFlowResult.getErrCode(), ErrorEnum.SUCCESS.getErrNo());
    }

    @Test
    public void updateTest() {
        CreateFlowParam createFlowParam = EntityBuilder.buildCreateFlowParam();
        UpdateFlowParam updateFlowParam = EntityBuilder.buildUpdateFlowParam();

        CreateFlowResult createFlowResult = definitionProcessor.create(createFlowParam);
        updateFlowParam.setFlowModuleId(createFlowResult.getFlowModuleId());
        UpdateFlowResult updateFlowResult = definitionProcessor.update(updateFlowParam);
        LOGGER.info("updateFlow.||result={}", updateFlowParam);
        Assert.assertEquals(updateFlowResult.getErrCode(), ErrorEnum.SUCCESS.getErrNo());
    }

    @Test
    public void deployTest() {
        CreateFlowParam createFlowParam = EntityBuilder.buildCreateFlowParam();
        UpdateFlowParam updateFlowParam = EntityBuilder.buildUpdateFlowParam();
        DeployFlowParam deployFlowParam = EntityBuilder.buildDeployFlowParm();

        CreateFlowResult createFlowResult = definitionProcessor.create(createFlowParam);
        updateFlowParam.setFlowModuleId(createFlowResult.getFlowModuleId());
        UpdateFlowResult updateFlowResult = definitionProcessor.update(updateFlowParam);
        Assert.assertEquals(updateFlowResult.getErrCode(), ErrorEnum.SUCCESS.getErrNo());

        deployFlowParam.setFlowModuleId(createFlowResult.getFlowModuleId());
        DeployFlowResult deployFlowResult = definitionProcessor.deploy(deployFlowParam);
        LOGGER.info("deployFlowTest.||deployFlowResult={}", deployFlowResult);
        Assert.assertEquals(deployFlowResult.getErrCode(), ErrorEnum.SUCCESS.getErrNo());
    }

    @Test
    public void getFlowModule() {
        CreateFlowParam createFlowParam = EntityBuilder.buildCreateFlowParam();
        UpdateFlowParam updateFlowParam = EntityBuilder.buildUpdateFlowParam();
        DeployFlowParam deployFlowParam = EntityBuilder.buildDeployFlowParm();
        GetFlowModuleParam flowModuleParam = new GetFlowModuleParam();

        CreateFlowResult createFlowResult = definitionProcessor.create(createFlowParam);
        updateFlowParam.setFlowModuleId(createFlowResult.getFlowModuleId());
        UpdateFlowResult updateFlowResult = definitionProcessor.update(updateFlowParam);
        Assert.assertEquals(updateFlowResult.getErrCode(), ErrorEnum.SUCCESS.getErrNo());

        flowModuleParam.setFlowModuleId(updateFlowParam.getFlowModuleId());
        FlowModuleResult flowModuleResultByFlowModuleId = definitionProcessor.getFlowModule(flowModuleParam);
        Assert.assertEquals(flowModuleResultByFlowModuleId.getFlowModuleId(), createFlowResult.getFlowModuleId());

        deployFlowParam.setFlowModuleId(createFlowResult.getFlowModuleId());
        DeployFlowResult deployFlowResult = definitionProcessor.deploy(deployFlowParam);
        flowModuleParam.setFlowDeployId(deployFlowResult.getFlowDeployId());
        flowModuleParam.setFlowModuleId(null);
        FlowModuleResult flowModuleResultByDeployId = definitionProcessor.getFlowModule(flowModuleParam);
        Assert.assertEquals(flowModuleResultByDeployId.getFlowModel(), updateFlowParam.getFlowModel());
    }

}