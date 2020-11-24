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
        UpdateFlowParam updateFlowParam = EntityBuilder.buildUpdateFlowParam();
        updateFlowParam.setFlowKey("testFlowKey_1605698222732");
        updateFlowParam.setFlowModuleId("955500c2-298f-11eb-b7c8-6eca5e511091");
        updateFlowParam.setFlowName("testFlowName_1605698222732");
        updateFlowParam.setFlowModel("{\"bounds\":[{\"x\":1038,\"y\":248},{\"x\":311,\"y\":168}],\"flowElementList\":[{\"incoming\":[],\"outgoing\":[\"SequenceFlow_120wc9b\"],\"bounds\":[{\"x\":347,\"y\":226},{\"x\":311,\"y\":190}],\"type\":2,\"dockers\":[],\"properties\":{\"name\":\"\"},\"key\":\"StartEvent_06par9m\"},{\"incoming\":[\"SequenceFlow_1nkk9q3\"],\"outgoing\":[],\"bounds\":[{\"x\":1038,\"y\":226},{\"x\":1002,\"y\":190}],\"type\":3,\"dockers\":[],\"properties\":{\"name\":\"\"},\"key\":\"EndEvent_0kx2kpn\"},{\"incoming\":[\"SequenceFlow_120wc9b\"],\"outgoing\":[\"SequenceFlow_1cmui04\"],\"bounds\":[{\"x\":563,\"y\":248},{\"x\":463,\"y\":168}],\"type\":4,\"dockers\":[],\"properties\":{\"isSupportRollBack\":\"false\",\"name\":\"\",\"outPcFormurl\":\"1644\"},\"key\":\"UserTask_11nfgqb\"},{\"incoming\":[\"SequenceFlow_1cmui04\"],\"outgoing\":[\"SequenceFlow_1nkk9q3\"],\"bounds\":[{\"x\":902,\"y\":248},{\"x\":802,\"y\":168}],\"type\":4,\"dockers\":[],\"properties\":{\"name\":\"\",\"outPcFormurl\":\"1645\"},\"key\":\"UserTask_1mzpjpw\"},{\"incoming\":[\"StartEvent_06par9m\"],\"outgoing\":[\"UserTask_11nfgqb\"],\"bounds\":[{\"x\":463,\"y\":208},{\"x\":347,\"y\":208}],\"type\":1,\"dockers\":[{\"x\":18,\"y\":18},{\"x\":50,\"y\":40}],\"properties\":{\"conditionsequenceflow\":\"\",\"optimusFormulaGroups\":\"\",\"defaultConditions\":\"false\"},\"key\":\"SequenceFlow_120wc9b\"},{\"incoming\":[\"UserTask_11nfgqb\"],\"outgoing\":[\"UserTask_1mzpjpw\"],\"bounds\":[{\"x\":802,\"y\":208},{\"x\":563,\"y\":208}],\"type\":1,\"dockers\":[{\"x\":50,\"y\":40},{\"x\":50,\"y\":40}],\"properties\":{\"conditionsequenceflow\":\"\",\"optimusFormulaGroups\":\"\",\"defaultConditions\":\"false\"},\"key\":\"SequenceFlow_1cmui04\"},{\"incoming\":[\"UserTask_1mzpjpw\"],\"outgoing\":[\"EndEvent_0kx2kpn\"],\"bounds\":[{\"x\":1002,\"y\":208},{\"x\":902,\"y\":208}],\"type\":1,\"dockers\":[{\"x\":50,\"y\":40},{\"x\":18,\"y\":18}],\"properties\":{\"conditionsequenceflow\":\"\",\"optimusFormulaGroups\":\"\"},\"key\":\"SequenceFlow_1nkk9q3\"}]}");
        try {

            Boolean result = definitionProcessor.update(updateFlowParam);
            LOGGER.info("updateFlow.||result={}", result);
            Assert.assertTrue(result == true);
        } catch (ParamException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deployTest() {
        DeployFlowParam deployFlowParam = EntityBuilder.buildDeployFlowParm();
        //deployFlowParam.setFlowModuleId("955500c2-298f-11eb-b7c8-6eca5e511091");
        //deployFlowParam.setFlowModuleId("b1598eec-2a32-11eb-bcb2-6eca5e511091");
        deployFlowParam.setFlowModuleId("111");
        try {
            DeployFlowDTO deployFlowDTO = definitionProcessor.deploy(deployFlowParam);
            LOGGER.info("deployFlowTest.||deployFlowDTO={}", deployFlowDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getFlowModule() {
        String flowModuleId = "955500c2-298f-11eb-b7c8-6eca5e511091";
        String deployId = "";
        FlowModuleDTO flowModuleDTO = definitionProcessor.getFlowModule(flowModuleId,"");
        FlowModuleDTO flowModuleDTO1 = definitionProcessor.getFlowModule("", deployId);
        LOGGER.info("deployFlowTest.||flowModuleDTO={}", flowModuleDTO);
    }

}