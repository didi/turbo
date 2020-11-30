package com.xiaoju.uemc.turbo.engine.processor;


import com.xiaoju.uemc.turbo.engine.dto.CreateFlowDTO;
import com.xiaoju.uemc.turbo.engine.dto.DeployFlowDTO;
import com.xiaoju.uemc.turbo.engine.dto.FlowModuleDTO;
import com.xiaoju.uemc.turbo.engine.exception.ParamException;
import com.xiaoju.uemc.turbo.engine.param.CreateFlowParam;
import com.xiaoju.uemc.turbo.engine.param.DeployFlowParam;
import com.xiaoju.uemc.turbo.engine.param.UpdateFlowParam;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DefinitionProcessorTest extends BaseTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefinitionProcessor.class);
    @Resource DefinitionProcessor definitionProcessor;

    @Test
    public void createTest() {
        CreateFlowParam createFlowParam = EntityBuilder.buildCreateFlowParam();
        try {
            CreateFlowDTO createFlowDTO = definitionProcessor.create(createFlowParam);
            LOGGER.info("createFlow.||createFlowDTO={}", createFlowDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateTest() {
        CreateFlowParam createFlowParam = EntityBuilder.buildCreateFlowParam();
        UpdateFlowParam updateFlowParam = EntityBuilder.buildUpdateFlowParam();
        try {
            CreateFlowDTO createFlowDTO = definitionProcessor.create(createFlowParam);
            updateFlowParam.setFlowModuleId(createFlowDTO.getFlowModuleId());
            boolean result = definitionProcessor.update(updateFlowParam);
            LOGGER.info("updateFlow.||result={}", result);
            Assert.assertTrue(result == true);
        } catch (ParamException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deployTest() {
        CreateFlowParam createFlowParam = EntityBuilder.buildCreateFlowParam();
        UpdateFlowParam updateFlowParam = EntityBuilder.buildUpdateFlowParam();
        DeployFlowParam deployFlowParam = EntityBuilder.buildDeployFlowParm();
        try {
            CreateFlowDTO createFlowDTO = definitionProcessor.create(createFlowParam);
            updateFlowParam.setFlowModuleId(createFlowDTO.getFlowModuleId());
            boolean result = definitionProcessor.update(updateFlowParam);
            Assert.assertTrue(result == true);
            deployFlowParam.setFlowModuleId(createFlowDTO.getFlowModuleId());
            DeployFlowDTO deployFlowDTO = definitionProcessor.deploy(deployFlowParam);
            LOGGER.info("deployFlowTest.||deployFlowDTO={}", deployFlowDTO);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getFlowModule() {
        CreateFlowParam createFlowParam = EntityBuilder.buildCreateFlowParam();
        UpdateFlowParam updateFlowParam = EntityBuilder.buildUpdateFlowParam();
        DeployFlowParam deployFlowParam = EntityBuilder.buildDeployFlowParm();
        try {
            CreateFlowDTO createFlowDTO = definitionProcessor.create(createFlowParam);
            updateFlowParam.setFlowModuleId(createFlowDTO.getFlowModuleId());
            boolean result = definitionProcessor.update(updateFlowParam);
            Assert.assertTrue(result == true);
            FlowModuleDTO flowModuleDTOByFlowModuleId = definitionProcessor.getFlowModule(createFlowDTO.getFlowModuleId(),null);
            Assert.assertTrue(flowModuleDTOByFlowModuleId.getFlowModuleId().equals(createFlowDTO.getFlowModuleId()));
            deployFlowParam.setFlowModuleId(createFlowDTO.getFlowModuleId());
            DeployFlowDTO deployFlowDTO = definitionProcessor.deploy(deployFlowParam);
            FlowModuleDTO flowModuleDTOByDeployId = definitionProcessor.getFlowModule(null,deployFlowDTO.getFlowDeployId());
            Assert.assertTrue(flowModuleDTOByDeployId.getFlowModel().equals(updateFlowParam.getFlowModel()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}