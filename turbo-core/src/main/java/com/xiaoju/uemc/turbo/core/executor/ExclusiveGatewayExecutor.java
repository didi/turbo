package com.xiaoju.uemc.turbo.core.executor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaoju.uemc.turbo.core.bo.HookInfoResponse;
import com.xiaoju.uemc.turbo.core.bo.NodeInstanceBO;
import com.xiaoju.uemc.turbo.core.common.*;
import com.xiaoju.uemc.turbo.core.config.EngineConfig;
import com.xiaoju.uemc.turbo.core.entity.InstanceDataPO;
import com.xiaoju.uemc.turbo.core.exception.ProcessException;
import com.xiaoju.uemc.turbo.core.model.FlowElement;
import com.xiaoju.uemc.turbo.core.model.InstanceData;
import com.xiaoju.uemc.turbo.core.util.FlowModelUtil;
import com.xiaoju.uemc.turbo.core.util.HttpUtil;
import com.xiaoju.uemc.turbo.core.util.InstanceDataUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Stefanie on 2019/12/1.
 */
@Service
public class ExclusiveGatewayExecutor extends ElementExecutor {

    private static final String HOOK_URL_NAME = "infoHook";
    private static final String PARAM_FLOW_INSTANCE_ID = "flowInstanceId";
    private static final String PARAM_DATA_LIST = "dataList";

    @Resource
    private EngineConfig engineConfig;

    /**
     * Update data map: http request to update data map
     * Url: dynamic config
     * Param: one of flowElement's properties
     */
    // TODO: 2019/12/16 common hook in preExecute
    @Override
    protected void doExecute(RuntimeContext runtimeContext) throws Exception {
        //1.get hook param
        FlowElement flowElement = runtimeContext.getCurrentNodeModel();
        String hookInfoParam = FlowModelUtil.getHookInfos(flowElement);
        //ignore while properties is empty
        if (StringUtils.isBlank(hookInfoParam)) {
            return;
        }

        //http post hook and get data result
        Map<String, InstanceData> hookInfoValueMap = getHookInfoValueMap(runtimeContext.getFlowInstanceId(), hookInfoParam);
        LOGGER.info("doExecute getHookInfoValueMap.||hookInfoValueMap={}", hookInfoValueMap);
        if (MapUtils.isEmpty(hookInfoValueMap)) {
            LOGGER.warn("doExecute: hookInfoValueMap is empty.||flowInstanceId={}||hookInfoParam={}||nodeKey={}",
                    runtimeContext.getFlowInstanceId(), hookInfoParam, flowElement.getKey());
            return;
        }

        //merge data to current dataMap
        Map<String, InstanceData> dataMap = runtimeContext.getInstanceDataMap();
        dataMap.putAll(hookInfoValueMap);

        //save data
        if (MapUtils.isNotEmpty(dataMap)) {
            String instanceDataId = saveInstanceDataPO(runtimeContext);
            runtimeContext.setInstanceDataId(instanceDataId);
        }
    }

    private Map<String, InstanceData> getHookInfoValueMap(String flowInstanceId, String hookInfoParam) throws Exception {
        //get hook config: url and timeout
        String hookUrl = engineConfig.getHookUrl();
        if (StringUtils.isBlank(hookUrl)) {
            LOGGER.warn("getHookInfoValueMap: cannot find hookConfig.||flowInstanceId={}", flowInstanceId);
            throw new ProcessException(ErrorEnum.GET_HOOK_CONFIG_FAILED);
        }

        Integer timeout = engineConfig.getHookTimeout();
        if (timeout == null) {
            timeout = Constants.DEFAULT_TIMEOUT;
        }

        //build request and http post
        Map<String, Object> hookParamMap = Maps.newHashMap();
        hookParamMap.put("flowInstanceId", flowInstanceId);
        hookParamMap.put("hookInfoParam", hookInfoParam);

        String responseStr = HttpUtil.postJson(HOOK_URL_NAME, hookUrl, JSONObject.toJSONString(hookParamMap), timeout);
        HookInfoResponse hookInfoResponse = JSONObject.parseObject(responseStr, HookInfoResponse.class);

        if (hookInfoResponse == null || hookInfoResponse.getStatus() != 0) {
            LOGGER.warn("getHookInfoValueMap failed: hookInfoResponse is null." +
                    "||hookUrl={}||hookParamMap={}||responseStr={}", hookUrl, hookParamMap, responseStr);
            return MapUtils.EMPTY_MAP;
        }

        Map<String, Object> data = hookInfoResponse.getData();
        if (MapUtils.isEmpty(data)) {
            LOGGER.warn("getHookInfoValueMap failed: data is empty.||hookUrl={}||hookParamMap={}||responseStr={}",
                    hookUrl, hookParamMap, responseStr);
            return MapUtils.EMPTY_MAP;
        }

        String respFlowInstanceId = (String) data.get(PARAM_FLOW_INSTANCE_ID);
        if (!flowInstanceId.equals(respFlowInstanceId)) {
            LOGGER.warn("getHookInfoValueMap failed: flowInstanceId is not match." +
                    "||hookUrl={}||hookParamMap={}||responseStr={}", hookUrl, hookParamMap, responseStr);
            return MapUtils.EMPTY_MAP;

        }

        List<InstanceData> dataList = Lists.newArrayList();
        JSONArray jsonArray = (JSONArray) data.get(PARAM_DATA_LIST);
        for (int i = 0; i < jsonArray.size(); i++) {
            InstanceData instanceData = jsonArray.getObject(i, InstanceData.class);
            dataList.add(instanceData);
        }
        return InstanceDataUtil.getInstanceDataMap(dataList);
    }

    private String saveInstanceDataPO(RuntimeContext runtimeContext) {
        String instanceDataId = genId();
        InstanceDataPO instanceDataPO = buildHookInstanceData(instanceDataId, runtimeContext);
        instanceDataDAO.insert(instanceDataPO);
        return instanceDataId;
    }

    private InstanceDataPO buildHookInstanceData(String instanceDataId, RuntimeContext runtimeContext) {
        InstanceDataPO instanceDataPO = new InstanceDataPO();
        BeanUtils.copyProperties(runtimeContext, instanceDataPO);
        instanceDataPO.setInstanceDataId(instanceDataId);
        instanceDataPO.setInstanceData(InstanceDataUtil.getInstanceDataListStr(runtimeContext.getInstanceDataMap()));
        instanceDataPO.setNodeInstanceId(runtimeContext.getCurrentNodeInstance().getNodeInstanceId());
        instanceDataPO.setNodeKey(runtimeContext.getCurrentNodeModel().getKey());
        instanceDataPO.setType(InstanceDataType.HOOK);
        instanceDataPO.setCreateTime(new Date());
        return instanceDataPO;
    }

    @Override
    protected void postExecute(RuntimeContext runtimeContext) throws Exception {
        NodeInstanceBO currentNodeInstance = runtimeContext.getCurrentNodeInstance();
        currentNodeInstance.setInstanceDataId(runtimeContext.getInstanceDataId());
        currentNodeInstance.setStatus(NodeInstanceStatus.COMPLETED);
        runtimeContext.getNodeInstanceList().add(currentNodeInstance);
    }

    /**
     * Calculate unique outgoing
     * Expression: one of flowElement's properties
     * Input: data map
     *
     * @return
     * @throws Exception
     */
    @Override
    protected RuntimeExecutor getExecuteExecutor(RuntimeContext runtimeContext) throws Exception {
        FlowElement nextNode = FlowModelUtil.calculateNextNode(runtimeContext.getCurrentNodeModel(),
                runtimeContext.getFlowElementMap(), runtimeContext.getInstanceDataMap());

        runtimeContext.setCurrentNodeModel(nextNode);
        return executorFactory.getElementExecutor(nextNode);
    }
}
