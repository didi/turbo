package com.xiaoju.uemc.turbo.engine.executor;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.xiaoju.uemc.turbo.engine.common.RuntimeContext;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.model.FlowModel;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import com.xiaoju.uemc.turbo.engine.util.FlowModelUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by Stefanie on 2019/12/16.
 */
public class ExclusiveGatewayExecutorTest extends BaseTest {

    @Resource
    private ExecutorFactory executorFactory;

    private ExclusiveGatewayExecutor exclusiveGatewayExecutor;

    private RuntimeContext runtimeContext;

    @Before
    public void initExclusiveGatewayExecutor() {
        List<FlowElement> flowElementList = EntityBuilder.buildFlowElementList();

        FlowModel flowModel = new FlowModel();
        flowModel.setFlowElementList(flowElementList);
        Map<String, FlowElement> flowElementMap = FlowModelUtil.getFlowElementMap(JSONObject.toJSONString(flowModel));

        FlowElement exclusiveGateway = FlowModelUtil.getFlowElement(flowElementMap, "exclusiveGateway1");

        runtimeContext = EntityBuilder.buildRuntimeContext();
        Map<String, InstanceData> instanceDataMap = Maps.newHashMap();
        InstanceData instanceDataA = new InstanceData("a", "integer", 2);
        InstanceData instanceDataB = new InstanceData("b", "integer", 1);
        instanceDataMap.put(instanceDataA.getKey(), instanceDataA);
        instanceDataMap.put(instanceDataB.getKey(), instanceDataB);
        runtimeContext.setInstanceDataMap(instanceDataMap);
        runtimeContext.setCurrentNodeModel(exclusiveGateway);

        try {
            exclusiveGatewayExecutor = (ExclusiveGatewayExecutor) executorFactory
                    .getElementExecutor(exclusiveGateway);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDoExecute() {
        try {
            exclusiveGatewayExecutor.doExecute(runtimeContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetExecuteExecutor() {
        try {
            exclusiveGatewayExecutor.getExecuteExecutor(runtimeContext);
            String modelKey = runtimeContext.getCurrentNodeModel().getKey();
            Assert.assertTrue("sequenceFlow4".equals(modelKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
