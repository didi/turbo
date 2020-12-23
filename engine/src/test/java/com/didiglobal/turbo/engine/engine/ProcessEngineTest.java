package com.didiglobal.turbo.engine.engine;

import com.didiglobal.turbo.engine.result.CreateFlowResult;
import com.didiglobal.turbo.engine.result.DeployFlowResult;
import com.didiglobal.turbo.engine.result.UpdateFlowResult;
import com.didiglobal.turbo.engine.engine.impl.ProcessEngineImpl;
import com.didiglobal.turbo.engine.param.CreateFlowParam;
import com.didiglobal.turbo.engine.param.DeployFlowParam;
import com.didiglobal.turbo.engine.param.UpdateFlowParam;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

public class ProcessEngineTest extends BaseTest {

    @Autowired
    ProcessEngine processEngine;

    @Resource
    ProcessEngineImpl processEngineImpl;

    @Test
    public void createFlowTest() {
        CreateFlowParam createFlowParam = EntityBuilder.buildCreateFlowParam();
        try {
            CreateFlowResult createFlowResult = processEngineImpl.createFlow(createFlowParam);
            LOGGER.info("flowModuleId={}", createFlowResult.getFlowModuleId());
        } catch (Exception e) {
            LOGGER.error("", e);
            LOGGER.error("", e);
        }
    }

    @Test
    public void updateFlowTest() {
        UpdateFlowParam updateFlowParam = EntityBuilder.buildUpdateFlowParam();
        updateFlowParam.setFlowModuleId("a038f993-1d7c-11ea-928e-8214dae31b03");
        try {
            UpdateFlowResult updateFlowResult = processEngineImpl.updateFlow(updateFlowParam);
            LOGGER.info("result={}", updateFlowResult);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    @Test
    public void deployFlowTest() {
        DeployFlowParam deployFlowParam = new DeployFlowParam("didi", "optimus-prime");
        deployFlowParam.setFlowModuleId("76bb65d9-35ef-11ea-a4cd-5ef9e2914105");
        deployFlowParam.setOperator("didiwangxing");
        try {
            DeployFlowResult deployFlowResult = processEngineImpl.deployFlow(deployFlowParam);
            LOGGER.info("deployFlowResult={}", deployFlowResult);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}
